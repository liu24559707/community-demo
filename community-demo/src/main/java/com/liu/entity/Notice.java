package com.liu.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName notice
 */
@TableName(value ="notice")
@Data
public class Notice implements Serializable {
    private Long noticeid;

    private String context;

    private Integer grade;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


    private static final long serialVersionUID = 1L;
}