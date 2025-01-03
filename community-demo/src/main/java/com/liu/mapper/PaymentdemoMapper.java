package com.liu.mapper;

import com.liu.entity.Paymentdemo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author 86151
* @description 针对表【paymentdemo】的数据库操作Mapper
* @createDate 2024-12-27 09:38:36
* @Entity com.liu.entity.Paymentdemo
*/
public interface PaymentdemoMapper extends BaseMapper<Paymentdemo> {

    @Select("select payDid from paymentdemo")
    List<Long> selectIds();
}




