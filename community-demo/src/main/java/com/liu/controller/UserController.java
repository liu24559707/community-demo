package com.liu.controller;

import com.liu.entity.Users;
import com.liu.entity.dto.PasswordDto;
import com.liu.entity.dto.PhoneDto;
import com.liu.service.UsersService;
import com.liu.utils.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("user")
public class UserController {

    @Resource
    private UsersService usersService;

    /**
     * 实现账号密码登录或手机号验证码登录
     * @param user
     * @return
     */
    @PostMapping("login")
    public Result login(@RequestBody Users user) {
        return usersService.login(user);
    }

    /**
     * 根据手机号获取验证码
     */
    @GetMapping("getCode/{phone}")
    public Result getCode(@PathVariable String phone) {
       return usersService.getCode(phone);
    }

    /**
     * 注册
     */
    @PostMapping("register")
    public Result register(@RequestBody Users user) {
        return usersService.register(user);
    }

    /**
     * 退出登录
     */
    @GetMapping("loginOut")
    public Result loginOut() {
        return usersService.loginOut();
    }

    /**
     * 获取用户信息
     */
    @GetMapping("getUserInfo")
    public Result getUserInfo() {
        return usersService.getUserInfo();
    }

    /**
     * 修改用户密码
     */
    @PutMapping("updatePassword")
    public Result updatePassword(@RequestBody PasswordDto passwordDto) {
        return usersService.updatePassword(passwordDto);
    }

    /**
     * 绑定手机号
     */
    @PostMapping("bindPhone")
    public Result bindPhone(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        return usersService.bindPhone(phone);
    }

    /**
     * 绑定其他手机号
     */
    @PutMapping("changeBind")
    public Result changBind(@RequestBody PhoneDto phoneDto){
        return usersService.changeBind(phoneDto);
    }

    /**
     * 注销用户
     */
    @DeleteMapping("delUser")
    public Result delUser() {
        return usersService.delUser();
    }
}
