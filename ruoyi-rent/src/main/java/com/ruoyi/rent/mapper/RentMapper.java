package com.ruoyi.rent.mapper;

import com.ruoyi.rent.domain.Rent;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface RentMapper {

    public boolean insert(Rent rent);
}


