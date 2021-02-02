package com.atguigu.service;

import com.atguigu.pojo.OrderSetting;

import java.util.List;
import java.util.Map;

public interface OrderSettingService {
    void addBatch(List<OrderSetting> listData);

    List<Map> getOrderSettingByMonth(String date);//参数格式为：2021-01

    //void editNumberByOrderDate(Map map);

    void editNumberByDate(OrderSetting orderSetting);
}
