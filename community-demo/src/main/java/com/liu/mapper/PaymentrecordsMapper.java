package com.liu.mapper;

import com.liu.entity.Paymentrecords;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author 86151
* @description 针对表【paymentrecords】的数据库操作Mapper
* @createDate 2024-12-27 09:38:36
* @Entity com.liu.entity.Paymentrecords
*/
public interface PaymentrecordsMapper extends BaseMapper<Paymentrecords> {

    /**
     * 查看根据ids查看缓存中不存在的缴费记录
     *
     * @param ids
     * @param userId
     * @return
     */
    List<Paymentrecords> queryWithRecordsAndDemo(List<Long> ids, Long userId);

    @Select("select payrid from paymentrecords")
    List<Long> queryIds(Long userId);
}




