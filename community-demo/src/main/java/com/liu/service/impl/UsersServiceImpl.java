package com.liu.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.entity.Users;
import com.liu.entity.dto.PasswordDto;
import com.liu.entity.dto.PhoneDto;
import com.liu.service.UsersService;
import com.liu.mapper.UsersMapper;
import com.liu.utils.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
* @author 86151
* @description 针对表【users】的数据库操作Service实现
* @createDate 2024-12-25 10:17:46
*/
@Slf4j
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users>
    implements UsersService{

    @Resource
    private UsersMapper usersMapper;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private JwtHelper jwtHelper;

    /**
     * 实现账号密码登录或手机号登录
     * @param user
     * @return
     * 先将两种登录方式的未登录情况罗列出来，再对已登录进行
     */
    @Transactional
    @Override
    public Result login(Users user) {
            LambdaQueryWrapper<Users> queryWrapper = new LambdaQueryWrapper<>();
            //管理员账号是没有办法登录移动端的，因为管理员要处理的数据过多
            queryWrapper.ne(Users::getRole,1);
            Users user1 = null;
            //未登录
        if (StringUtils.isNotBlank(user.getUsername())||
                StringUtils.isNotBlank(user.getPassword())) {//账号登录
            //加密
            user.setPassword(MD5Util.encrypt(user.getPassword()));
            queryWrapper.eq(Users::getUsername,user.getUsername());
            Users users = usersMapper.selectOne(queryWrapper);
            if(users==null) {//不存在该用户或该用户已被注销，这里统称为用户名错误
                return Result.build(null,500,"用户名或密码有误");
            }else if(!user.getPassword().equals(users.getPassword())) {
                return Result.build(null,500,"用户名或密码有误");
            }
            user1=users;
        }else {//手机号登录
            queryWrapper.eq(Users::getPhone,user.getPhone());
            Users users = usersMapper.selectOne(queryWrapper);
            String code = (String)redisTemplate.opsForValue().get("code:phone:" + user.getPhone());
            if(users==null) {
                return Result.build(null,500,"手机号未绑定账号");
            }else if(!user.getCode().equals(code)) {
                return Result.build(null,500,"验证码有误");
            }
            user1=users;
        }
        //登录
        //考虑抢号问题
        String key ="user:"+user1.getUserId();
        String token = jwtHelper.createToken(user1.getUserId());
        //存储到redis中，给寿命
        //创建token，token可以用于查看登录状态，也以此考虑异地登录问题:
        //在登录拦截器实现，查询该用户的token，如果传入的token与存放的token不一致，则说明此次登录是抢号登录
        //需要通知其他登录该账号的用户异地登录
        //每次请求都会刷新该用户的寿命，在刷新拦截器实现
        user1.setToken(token);
        //将token和用户权限存入redis
        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(user1);
        redisTemplate.opsForHash().putAll(key,stringObjectMap);
        redisTemplate.expire(key,2,TimeUnit.HOURS);
        user1.setPassword(null);
        return Result.ok(user1);
    }


    /**
     * 发送验证码，同时使用redis存储验证码
     * @param phone
     * @return
     * 1.生成验证码
     * 2.创建键并存入redis，设置有效时间5分钟
     * 3.
     */
    @Override
    public Result getCode(String phone) {
        //生成随机的6位验证码
        String key = "code:phone:" + phone;
        if(redisTemplate.hasKey(key)) {
            return Result.build(null,500,"请两分钟后再次获取验证码");
        }
        String code = RandomUtil.randomNumbers(6);
        redisTemplate.opsForValue().set(key,code,2, TimeUnit.MINUTES);
        log.info("【i社区】 \t登录验证码：{}，有效时长为2分钟，请尽快登录,发送时间：{}", code,
                IdAndTimeGeneration.currentTime("yyyy-MM-dd HH:mm"));
        return Result.ok(null);
    }

    /**
     * 注册用户
     * @param user
     * @return
     */
    @Override
    public Result register(Users user) {

        return null;
    }

    /**
     * 退出登录
     * @return
     */
    @Override
    public Result loginOut() {
        String userId = UserHolder.getUserId();
        String key = "user:"+userId;
        if(redisTemplate.hasKey(key)) {
            redisTemplate.delete(key);
            return Result.ok(null);
        }

        return Result.build(null,500,"退出登录失败");
    }

    /**
     * 获取用户信息
     * @return
     */
    @Override
    public Result getUserInfo() {
        String userId = UserHolder.getUserId();
        String key = "user:"+userId;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        Users users = JSON.parseObject(JSON.toJSONString(entries), Users.class);
        return Result.ok(users);
    }

    /**
     * 修改用户密码
     * @param passwordDto
     * @return
     */
    @Transactional
    @Override
    public Result updatePassword(PasswordDto passwordDto) {
        String userId = UserHolder.getUserId();
        String key = "user:"+userId;
        //1.检验旧密码是否正确
        String password = (String)redisTemplate.opsForHash().get(key, "password");
        if (!password.equals(MD5Util.encrypt(passwordDto.getOldPassword()))) {
            return Result.build(null,500,"旧密码有误");
        }
        //2.修改数据库用户密码
        String MD5Password = MD5Util.encrypt(passwordDto.getNewPassword());
        Users user = new Users();
        user.setUserId(Long.valueOf(userId));
        user.setPassword(MD5Password);
        usersMapper.updateById(user);
        //3.修改缓存中的用户密码
        redisTemplate.opsForHash().put(key, "password", MD5Password);
        return Result.ok("密码修改成功");
    }

    /**
     * 绑定手机号
     * @param phone
     * @return
     */
    @Transactional
    @Override
    public Result bindPhone(String phone) {
        String userId = UserHolder.getUserId();
        String key = "user:"+userId;
        String phone1 = (String)redisTemplate.opsForHash().get(key, "phone");

        //1.检查是否含有手机号
        if (phone1 != null) {
          return Result.build(null,500,"该账号已绑定其他手机号");
        }
        //2.向数据库插入手机号,
        LambdaUpdateWrapper<Users> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Users::getUserId,Long.valueOf(userId)).setSql("phone = "+phone);
        this.update(updateWrapper);
        //3.插入缓存中的手机号
        redisTemplate.opsForHash().put(key, "phone", phone);
       return Result.ok(null);
    }

    /**
     * 换绑手机号
     * @param phoneDto
     * @return
     */
    @Override
    public Result changeBind(PhoneDto phoneDto) {
        String userId = UserHolder.getUserId();
        String key = "user:"+userId;
        String phone = (String)redisTemplate.opsForHash().get(key, "phone");

        if (phone == null) {
            return Result.build(null,500,"你未绑定手机号");
        }
        if (!phone.equals(phoneDto.getOldPhone())) {
            return Result.build(null,500,"旧手机号有误");
        }
        //2.向数据库插入手机号
        LambdaUpdateWrapper<Users> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Users::getUserId,Long.valueOf(userId)).setSql("phone = "+phoneDto.getNewPhone());
        this.update(updateWrapper);
        //3.缓存存入新手机号
        redisTemplate.opsForHash().put(key, "phone", phoneDto.getNewPhone());
        return Result.ok(null);
    }

    @Transactional
    @Override
    public Result delUser() {
        String userId = UserHolder.getUserId();
        //删除数据库数据
        usersMapper.deleteById(Long.valueOf(userId));
        //删除缓存
        String key = "user:"+userId;
        redisTemplate.delete(key);
        return Result.ok(null);
    }


}




