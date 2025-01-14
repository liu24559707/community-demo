package com.liu.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName complain
 */
@TableName(value ="complain")
@Data
public class Complain implements Serializable {
    private Long complainid;

    private String context;

    //投诉图片
    private String complainimg;

    //投诉进程
    private Integer progress;

    private Long user;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic
    private Integer del;

    private static final long serialVersionUID = 1L;
}