package com.ruoyi.rent.service;

import com.ruoyi.rent.domain.Rent;
import com.ruoyi.rent.mapper.RentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RentService {
@Autowired
RentMapper rentMapper;
public boolean insert(Rent rent){
    return rentMapper.insert(rent);
}
}
