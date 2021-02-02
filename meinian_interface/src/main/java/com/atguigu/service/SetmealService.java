package com.atguigu.service;

import com.atguigu.entity.PageResult;
import com.atguigu.pojo.Setmeal;

import java.util.List;
import java.util.Map;

public interface SetmealService {
    void add(Integer[] travelgroupIds, Setmeal setmeal);

    PageResult findPage(Integer currentPage, Integer pageSize, String queryString);

    Setmeal getById(Integer id);

    List<Integer> getTravelGroupIdsBySetmealId(Integer setmealId);

    void edit(Integer[] travelgroupIds, Setmeal setmeal);

    void del(Integer id);

    List<Setmeal> getSetmeal();

    Setmeal findById(Integer id);

    Setmeal getSetmeakById(Integer id);

    List<Map> getSetmealReport();
}
