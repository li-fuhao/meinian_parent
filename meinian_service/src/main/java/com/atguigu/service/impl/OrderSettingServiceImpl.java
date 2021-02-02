package com.atguigu.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.dao.OrderSettingDao;
import com.atguigu.pojo.OrderSetting;
import com.atguigu.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service(interfaceClass = OrderSettingService.class)
@Transactional
public class OrderSettingServiceImpl implements OrderSettingService {

    @Autowired
    OrderSettingDao orderSettingDao;

    //根据日期修改可预约人数
    @Override
    public void editNumberByDate(OrderSetting orderSetting) {
        int count = orderSettingDao.findOrderSettingByOrderDate(orderSetting.getOrderDate());
        if (count>0){
            //当前日期已经进行了预约设置，需要进行修改操作
            orderSettingDao.editNumberByOrderDate(orderSetting);
        }else {
            //当前日期没有进行预约设置，进行添加操作
            orderSettingDao.add(orderSetting);
        }
    }

    //-----------------------老版麻烦-----------------------
    /*@Override
    public void editNumberByOrderDate(Map map) {
        try {
            String dateSte = (String) map.get("orderDate");
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(dateSte);
            OrderSetting orderSetting = new OrderSetting();
            orderSetting.setNumber(Integer.parseInt((String)map.get("value")));
            orderSetting.setOrderDate(date);
            int count = orderSettingDao.findOrderSettingByOrderDate(date);
            if (count>0){
                orderSettingDao.edit(orderSetting);
            }else {

                orderSettingDao.add(orderSetting);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }*/
    //-----------------------老版麻烦-----------------------

    /**  传递的参数
     *   date（格式：2021-01）
     *  构建的数据List<Map>
     *    map.put("date",1);
     map.put("number",120);
     map.put("reservations",10);

     *  查询方案：SELECT * FROM t_ordersetting WHERE orderDate LIKE '2021-01-%'
     *  查询方案：SELECT * FROM t_ordersetting WHERE orderDate BETWEEN '2021-01-1' AND '2021-01-31'
     */
    //根据日期查询预约设置数据
    @Override
    public List<Map> getOrderSettingByMonth(String date) {
        // 1.组织查询Map，startDate表示月份开始时间，endDate月份结束时间
        String startDate = date+"-1"; //2021-1-1
        String endDate = date+"-31";    //2021-1-31
        Map param = new HashMap();
        param.put("startDate",startDate);
        param.put("endDate",endDate);
        // 2.查询当前月份的预约设置
        List<OrderSetting> list = orderSettingDao.getOrderSettingByMonth(param);
        List<Map> listData = new ArrayList<Map>();
        // 3.将List<OrderSetting>，组织成List<Map>
        for (OrderSetting orderSetting : list) {
            Map map = new HashMap();
            map.put("date",orderSetting.getOrderDate().getDate());//获得日期（几号）
            map.put("number",orderSetting.getNumber());//可预约人数
            map.put("reservations",orderSetting.getReservations());//已预约人数
            listData.add(map);
        }
        return listData;
    }

    @Override
    public void addBatch(List<OrderSetting> listData) {
        for (OrderSetting orderSetting : listData) {
            //如果日期对应设置存在，就修改，否则添加
            int count = orderSettingDao.findOrderSettingByOrderDate(orderSetting.getOrderDate());
            if (count>0){//预约设置存在,更新number数量
                orderSettingDao.editNumberByOrderDate(orderSetting);
            }else {
                //如果没有设置过预约日期，执行保存
                orderSettingDao.add(orderSetting);
            }
        }
    }
}
