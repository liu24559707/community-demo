package com.liu.entity;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

/**
 * 消息类
 */
@Data
public class Message {
    private String message;

    private String from;
    private String to;
    //消息类型：1：系统通知；2.个人；3.朋友列表；4.最新上线用户；5.下线用户
    private Integer type;
    //时间
    private Date dateTime;
}
