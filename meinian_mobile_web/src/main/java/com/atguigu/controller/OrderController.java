package com.atguigu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.constant.MessageConstant;
import com.atguigu.constant.RedisMessageConstant;
import com.atguigu.entity.Result;
import com.atguigu.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    OrderService orderService;

    @Autowired
    JedisPool jedisPool;


    @RequestMapping("/findById")
    public Result findById(Integer orderId){
        try {
            Map map = orderService.findById4Detail(orderId);
            //查询预约信息成功
            return new Result(true, MessageConstant.QUERY_ORDER_SUCCESS,map);
        } catch (Exception e) {
            e.printStackTrace();
            //查询预约信息失败
            return new Result(false, MessageConstant.QUERY_ORDER_FAIL);
        }
    }

    /**
     * 由于页面表单数据来自多张表的数据操作，用pojo对象接收，接收不完整，就用map接收
     * @param map
     * @return
     */
    @RequestMapping("/saveOrder")
    public Result saveOrder(@RequestBody Map map){
        try {
            System.out.println("map = " + map);

//            点击提交预约, 把用户信息 提交到服务器
//                    在Controller里面
//            - 获得用户信息
//                    - 校验验证码(redis里面存的和用户输入的比较)
//                    - 调用业务, 进行预约, 响应
            String telephone = (String) map.get("telephone");
            String validateCode = (String) map.get("validateCode");
            String redisCode = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_ORDER);
            if (redisCode == null || !redisCode.equals(validateCode)){
                return new Result(false, MessageConstant.VALIDATECODE_ERROR);
            }
            Result result = orderService.saveOrder(map);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.ORDER_FAIL);

        }
    }
}
