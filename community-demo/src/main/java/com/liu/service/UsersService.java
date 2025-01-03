package com.liu.service;

import com.liu.entity.Users;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liu.entity.dto.PasswordDto;
import com.liu.entity.dto.PhoneDto;
import com.liu.utils.Result;

/**
* @author 86151
* @description 针对表【users】的数据库操作Service
* @createDate 2024-12-25 10:17:46
*/
public interface UsersService extends IService<Users> {

    Result login(Users user);

    Result getCode(String phone);

    Result register(Users user);

    Result loginOut();

    Result getUserInfo();

    Result updatePassword(PasswordDto passwordDto);

    Result bindPhone(String phone);

    Result changeBind(PhoneDto phoneDto);

    Result delUser();
}
