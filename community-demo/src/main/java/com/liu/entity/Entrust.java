package com.liu.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName entrust
 */
@TableName(value ="entrust")
@Data
public class Entrust implements Serializable {
    @TableId
    private Long entrustid;

    private Long publisher;

    private String context;

    private Integer grade;

    private Date endTime;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill=FieldFill.INSERT_UPDATE)
    private Date updateTime;


    @TableLogic
    private Integer del;

    private static final long serialVersionUID = 1L;
}