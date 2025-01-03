package com.liu.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName accept
 */
@TableName(value ="accept")
@Data
public class Accept implements Serializable {
    private Long acceptid;

    private Long entrustid;

    private Long acceptUser;

    private Integer progress;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

//    由于自动填充需要这个属性，所以必须声明这个变量
    @TableField(exist = false)
    private Date updateTime;

    @TableField(exist = false)
    private String context;

    @TableLogic
    private Integer del;

    private static final long serialVersionUID = 1L;
}