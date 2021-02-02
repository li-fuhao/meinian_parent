package com.atguigu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.constant.MessageConstant;
import com.atguigu.entity.Result;
import com.atguigu.pojo.OrderSetting;
import com.atguigu.service.OrderSettingService;
import com.atguigu.util.POIUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orderSetting")
public class OrderSettingController {

    @Reference
    OrderSettingService orderSettingService;
    /**
     * 根据指定日期修改可预约人数
     * @param
     * @return
     */
    @RequestMapping("/editNumberByDate")
    public Result editNumberByDate(@RequestBody OrderSetting orderSetting){
        try {
            orderSettingService.editNumberByDate(orderSetting);
            //预约设置成功
            return new Result(true, MessageConstant.ORDERSETTING_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            //预约失败
            return new Result(false, MessageConstant.ORDERSETTING_FAIL);
        }
    }
    //-----------------------老版麻烦-----------------------
    /*@RequestMapping("/editNumberByOrderDate")
    public Result editNumberByOrderDate(@RequestBody Map map){
        try {
            orderSettingService.editNumberByOrderDate(map);
            return new Result(true, MessageConstant.ORDERSETTING_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.ORDERSETTING_FAIL);
        }
    }*/
    //-----------------------老版麻烦-----------------------


    /**
     * 根据日期查询预约设置数据(获取指定日期所在月份的预约设置数据)
     * @param date
     * @return
     */

    @RequestMapping("/getOrderSettingByMonth")
    public Result getOrderSettingByMonth(String date){//参数格式为：2021-01
        try {
            List<Map> list = orderSettingService.getOrderSettingByMonth(date);
            //获取预约设置数据成功
            return  new Result(true, MessageConstant.GET_ORDERSETTING_SUCCESS,list);
        } catch (Exception e) {
            e.printStackTrace();
            //获取预约设置数据失败
            return  new Result(false, MessageConstant.GET_ORDERSETTING_FAIL);
        }
    }

    @RequestMapping("/upload")
    public Result upload(MultipartFile excelFile){
        try {
            List<String[]> list = POIUtils.readExcel(excelFile); //保存到数据库里面，解析这一条数据，把数组变成一个bean对象

            List<OrderSetting> listData = new ArrayList<OrderSetting>();
            for (String[] strArray : list) {
                String dateStr = strArray[0];
                String numberStr = strArray[1];
                //每次循环封装成一个OrderSetting
                OrderSetting orderSetting = new OrderSetting();
                //要求是一个date类型，而我们这个是字符串所以要把字符串转换成日期对象
                orderSetting.setOrderDate(new Date(dateStr));
                orderSetting.setNumber(Integer.parseInt(numberStr));
                //orderSetting.setReservations(0);
                //循环放到集合里
                listData.add(orderSetting);
            }
            orderSettingService.addBatch(listData);
            return new Result(true, MessageConstant.UPLOAD_SUCCESS);
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.UPLOAD_FAIL);
        }
    }
}
