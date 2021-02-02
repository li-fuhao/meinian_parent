package com.atguigu.service;

import com.atguigu.entity.PageResult;
import com.atguigu.entity.QueryPageBean;
import com.atguigu.pojo.Address;

import java.util.List;

public interface AddressService {
    List<Address> findAllMaps();

    void addAddress(Address address);

    PageResult findPage(QueryPageBean queryPageBean);

    void deleteById(Integer id);
}
