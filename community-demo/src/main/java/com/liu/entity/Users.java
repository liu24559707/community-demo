package com.liu.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName users
 */
@TableName(value ="users")
@Data
public class Users implements Serializable {
    @TableId
    private Long userId;

    private String username;

    private String password;

    private Integer role;

    private String phone;

    private String address;

    @TableField(exist = false)//mybatis映射时忽略该属性
    private String code;

    @TableField(exist = false)
    private String token;

    private Integer creditScore;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic //逻辑删除注解
    private Integer del;

    private static final long serialVersionUID = 1L;
}