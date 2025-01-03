package com.liu.mapper;

import com.liu.entity.Properties;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author 86151
* @description 针对表【properties】的数据库操作Mapper
* @createDate 2024-12-27 09:38:37
* @Entity com.liu.entity.Properties
*/
public interface PropertiesMapper extends BaseMapper<Properties> {

    @Select("select propId,ownerId from properties order by ownerId asc ")
    List<Properties> selectListWithPropIdAndOwnerId();
}




