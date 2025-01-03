package com.liu.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @TableName paymentdemo
 */
@TableName(value ="paymentdemo")
@Data
public class Paymentdemo implements Serializable {
    private Long paydid;

    private String demoname;

    private BigDecimal amount;

    private String remarks;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


    @TableLogic
    private Integer del;

    private static final long serialVersionUID = 1L;
}