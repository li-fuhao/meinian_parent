package com.atguigu.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.dao.TravelItemDao;
import com.atguigu.entity.PageResult;
import com.atguigu.pojo.TravelItem;
import com.atguigu.service.TravelItemService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service(interfaceClass = TravelItemService.class) //发布服务，注册到zk中心
@Transactional  //声明式事务，所有方法增加事务
public class TravelItemServiceImpl implements TravelItemService {

    @Autowired
    TravelItemDao travelItemDao;

    @Override
    public List<TravelItem> findAll() {
        return travelItemDao.findAll();
    }

    @Override
    public void edit(TravelItem travelItem) {
        travelItemDao.edit(travelItem);
    }

    @Override
    public TravelItem getById(Integer id) {
        return travelItemDao.getById(id);
    }

    @Override
    public void delete(Integer id) {
        // 在删除自由行之前，先判断自由行的id，在中间表中是否存在数据
        long count =  travelItemDao.findCountByTravelItemItemId(id);
        // 中间表如果有数据，不要往后面执行，直接抛出异常
        // 如果非要删除也可以：delete from t_travelgroup_travelitem where travelitem_id = 1
        if (count > 0){//存在关联数据
            throw new RuntimeException("删除自由行失败，因为存在关联数据。先解除关系，再删除!");
        }
        // 使用自由行的id进行删除

        travelItemDao.delete(id);
    }

    @Override
    public PageResult findPage(Integer currentPage, Integer pageSize, String queryString) {
        //分页插件
        //开启分页功能 (第几页，多少条)
        //limit (currentPage-1)*pageSize,pageSize
        PageHelper.startPage(currentPage,pageSize); // limit ?,? 第一个问号表示开始索引，第二个问号表示查询的条数
        Page page = travelItemDao.findPage(queryString); //返回当前页数据
        return new PageResult(page.getTotal(), page.getResult()); //1.总记录数， 2.分页数据集合
    }

    @Override
    public void add(TravelItem travelItem) {  //ctrl + i 调接口方法
        travelItemDao.add(travelItem);
    }
}
