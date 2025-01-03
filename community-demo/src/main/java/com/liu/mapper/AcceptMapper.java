package com.liu.mapper;

import com.liu.entity.Accept;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author 86151
* @description 针对表【accept】的数据库操作Mapper
* @createDate 2024-12-31 09:25:38
* @Entity com.liu.entity.Accept
*/
public interface AcceptMapper extends BaseMapper<Accept> {


    List<Accept> selectAllList(String userId);
}




