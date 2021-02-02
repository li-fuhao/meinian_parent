package com.atguigu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.constant.MessageConstant;
import com.atguigu.constant.RedisConstant;
import com.atguigu.entity.PageResult;
import com.atguigu.entity.QueryPageBean;
import com.atguigu.entity.Result;
import com.atguigu.pojo.Setmeal;
import com.atguigu.service.SetmealService;
import com.atguigu.util.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Reference
    SetmealService setmealService;

    @Autowired
    JedisPool jedisPool;

    @RequestMapping("/del")
    @PreAuthorize("hasAuthority('SETMEAL_DELETE')")//权限校验，使用TRAVELITEM_DELETE123测试
    public Result delete(Integer id){
        try {
            setmealService.del(id);
            return new Result(true, "删除套餐预约成功");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(false, e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除套餐预约失败");
        }
    }

    @RequestMapping("/edit")
    @PreAuthorize("hasAuthority('SETMEAL_EDIT')")//权限校验
    public Result edit(Integer[] travelgroupIds,@RequestBody Setmeal setmeal){
        try {
            setmealService.edit(travelgroupIds,setmeal);
            return new Result(true, "编辑套餐预约成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "编辑套餐预约失败");
        }
    }

    @RequestMapping("/getTravelGroupIdsBySetmealId")
    public Result getTravelGroupIdsBySetmealId(Integer setmealId){
        try {
            List<Integer> setmealIds = setmealService.getTravelGroupIdsBySetmealId(setmealId);
            return new Result(true, "根据套餐查询跟团游成功",setmealIds);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, "根据套餐查询跟团游失败");
        }
    }

    @RequestMapping("/getById")
    public Result getById(Integer id){
        try {
            Setmeal setmeal = setmealService.getById(id);
            return new Result(true, MessageConstant.QUERY_ORDER_SUCCESS,setmeal);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.QUERY_ORDER_FAIL);
        }
    }

    @RequestMapping("/findPage")
    @PreAuthorize("hasAuthority('SETMEAL_QUERY')")//权限校验
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
        PageResult pageResult =setmealService.findPage(queryPageBean.getCurrentPage(),
                queryPageBean.getPageSize(),
                queryPageBean.getQueryString());
        return pageResult;
    }

    @RequestMapping("/add")
    @PreAuthorize("hasAuthority('SETMEAL_ADD')")//权限校验
    public Result add(Integer[] travelgroupIds, @RequestBody Setmeal setmeal){
        try {
            setmealService.add(travelgroupIds,setmeal);
            return new Result(true, MessageConstant.ADD_SETMEAL_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.ADD_SETMEAL_FAIL);

        }
    }

    // imgFile:需要跟页面el-upload里面的name保持一致
    @RequestMapping("/upload")
    public Result upload(@RequestParam("imgFile")MultipartFile imgFile){
        try {
            //获取原始数据
            String originalFilename = imgFile.getOriginalFilename();
            //找到.最后出现的位置
            int lastIndexOf = originalFilename.lastIndexOf(".");
            //获取文件后缀
            String suffix = originalFilename.substring(lastIndexOf);
            //使用UUID随机产生文件名称，防止同文件覆盖
            String fileName = UUID.randomUUID().toString() + suffix;
            //调用工具类，上传图片到七牛云
            QiniuUtils.upload2Qiniu(imgFile.getBytes(), fileName);

            //*****************补充代码 解决七牛云上垃圾图片的问题*********************************
            //将上传图片名称存入Redis，基于Redis的Set集合存储
            jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_RESOURCES,fileName);
            //******************************************************

            //图片上传成功
            return new Result(true, MessageConstant.PIC_UPLOAD_SUCCESS,fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.PIC_UPLOAD_SUCCESS);
        }
    }
}
