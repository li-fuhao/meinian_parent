package com.atguigu.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.constant.RedisConstant;
import com.atguigu.dao.SetmealDao;
import com.atguigu.entity.PageResult;
import com.atguigu.pojo.Setmeal;
import com.atguigu.service.SetmealService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = SetmealService.class)
@Transactional
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    SetmealDao setmealDao;

    @Autowired
    JedisPool jedisPool;

    @Override
    public List<Map> getSetmealReport() {
        return setmealDao.getSetmealReport();
    }

    @Override
    public Setmeal getSetmeakById(Integer id) {
        return setmealDao.getSetmeakById(id);
    }

    @Override
    public Setmeal findById(Integer id) {
        return setmealDao.findById(id);
    }

    @Override
    public List<Setmeal> getSetmeal() {
        return setmealDao.getSetmeal();
    }

    @Override
    public void del(Integer id) {
        // 在删除跟团游之前，先判断自由行的id，在中间表中是否存在数据
        long count =  setmealDao.findCountBySetmealId(id);
        // 中间表如果有数据，不要往后面执行，直接抛出异常
        // 如果非要删除也可以：delete from t_travelgroup_travelitem where travelitem_id = 1
        if (count > 0){
            throw new RuntimeException("删除套餐预约失败，因为存在关联数据。先解除关系，再删除!");
        }
        // 使用自由行的id进行删除
        setmealDao.del(id);
    }

    @Override
    public void edit(Integer[] travelgroupIds, Setmeal setmeal) {
        setmealDao.edit(setmeal);
        Integer setmealId = setmeal.getId();

        //先删除中间表的关联数据
        setmealDao.delete(setmealId);

        //重新再增加关联数据
        setSetmealAndTravelGrop(travelgroupIds, setmealId);
    }

    @Override
    public List<Integer> getTravelGroupIdsBySetmealId(Integer setmealId) {
        return setmealDao.getTravelGroupIdsBySetmealId(setmealId);
    }

    @Override
    public Setmeal getById(Integer id) {
        return setmealDao.getById(id);
    }

    @Override
    public PageResult findPage(Integer currentPage, Integer pageSize, String queryString) {
        PageHelper.startPage(currentPage, pageSize);
        Page page = setmealDao.findPage(queryString);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void add(Integer[] travelgroupIds, Setmeal setmeal) {
        //1.保存套餐
        setmealDao.add(setmeal);//主键回填

        //2.保存关联数据
        Integer setmealId = setmeal.getId();
        setSetmealAndTravelGrop(travelgroupIds,setmealId);

        //****************补充代码 解决垃圾图片问题********************************
        String pic = setmeal.getImg();
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES,pic);
        //************************************************
    }

    private void setSetmealAndTravelGrop(Integer[] travelgroupIds, Integer setmealId) {
        if (travelgroupIds!=null && travelgroupIds.length>0){
            for (Integer travelgroupId : travelgroupIds) {
                Map<String,Integer> map = new HashMap<>();
                map.put("travelgroupId",travelgroupId);
                map.put("setmealId",setmealId);
                setmealDao.addSetmealAndTravelGroup(map);
            }
        }
    }
}
