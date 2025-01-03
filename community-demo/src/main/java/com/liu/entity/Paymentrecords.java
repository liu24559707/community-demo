package com.liu.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @TableName paymentrecords
 */
@TableName(value ="paymentrecords")
@Data
public class Paymentrecords implements Serializable {
    private Long payrid;

    /**
     * 缴费项目id
     */
    private Long paydid;

    /**
     * 缴费状态
     */
    private Integer progress;

    /**
     * 缴费项目
     */
    @TableField(exist = false)
    private String demoname;

    /**
     * 缴费金额
     */
    @TableField(exist = false)
    private BigDecimal amount;

    /**
     * 缴费者id
     */

    private Long userId;
    /**
     * 房产号
     */
    private Long propid;

    /**
     * 房产名
     */
    @TableField(exist = false)
    private String propname;

    /**
     * 房产拥有者
     */
    @TableField(exist = false)
    private String username;



    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic
    private Integer del;

    private static final long serialVersionUID = 1L;
}