package com.liu.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName properties
 */
@TableName(value ="properties")
@Data
public class Properties implements Serializable {
    private Long propid;

    private String propname;

    private Long ownerid;

    private String description;

    private Integer propstatus;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic
    private Integer del;

    private static final long serialVersionUID = 1L;
}