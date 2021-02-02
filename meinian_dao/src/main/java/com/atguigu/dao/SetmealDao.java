package com.atguigu.dao;

import com.atguigu.pojo.Setmeal;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface SetmealDao {
    void add(Setmeal setmeal);

    void addSetmealAndTravelGroup(Map<String, Integer> map);

    Page findPage(String queryString);

    Setmeal getById(Integer id);

    List<Integer> getTravelGroupIdsBySetmealId(Integer setmealId);

    void edit(Setmeal setmeal);

    void delete(Integer setmealId);

    long findCountBySetmealId(Integer id);

    void del(Integer id);

    List<Setmeal> getSetmeal();

    Setmeal findById(Integer id);

    Setmeal getSetmeakById(Integer id);

    List<Map> getSetmealReport();
}
