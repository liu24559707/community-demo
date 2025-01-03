package com.liu.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 社区新闻
 * @TableName news
 */
@TableName(value ="news")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class News implements Serializable {

    @TableId
    private Long nid;

    private String title;

    private Integer newsType;

    private String content;

    private Integer clickCount;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic //逻辑删除注解
    private Integer del;

    private static final long serialVersionUID = 1L;

}