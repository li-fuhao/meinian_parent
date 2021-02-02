package com.atguigu.dao;

import com.atguigu.pojo.Address;
import com.github.pagehelper.Page;

import java.util.List;

public interface AddressDao {
    List<Address> findAllMaps();

    void addAddress(Address address);

    Page findPage(String queryString);

    void deleteById(Integer id);
}
