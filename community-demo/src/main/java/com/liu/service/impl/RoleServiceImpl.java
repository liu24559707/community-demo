package com.liu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.entity.Role;
import com.liu.service.RoleService;
import com.liu.mapper.RoleMapper;
import org.springframework.stereotype.Service;

/**
* @author 86151
* @description 针对表【role】的数据库操作Service实现
* @createDate 2024-12-25 10:17:30
*/
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>
    implements RoleService{

}




