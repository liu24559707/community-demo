package com.liu.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName newstype
 */
@TableName(value ="newstype")
@Data
public class Newstype implements Serializable {
    @TableId
    private Integer typeid;

    private String typename;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic
    private Integer del;

    private static final long serialVersionUID = 1L;
}