package com.liu.utils;

/**
 * 数据类型
 * 作为数据id标识的的一部分
 */
public enum dataType {
    UserData(100,"用户"),
    NewsData(101,"新闻"),
    NewsTypeData(102,"新闻类型"),
    PaymentDemo(103,"缴费项目"),
    PaymentRecords(104,"缴费记录"),
    Notice(105,"通知"),
    Entrust(106,"委托"),
    Accept(107,"接收委托"),
    Complain(108,"投诉");


    private int dataTypeId;
    private String dataTypeName;

    dataType(int dataTypeId, String dataTypeName) {
        this.dataTypeId = dataTypeId;
        this.dataTypeName = dataTypeName;
    }
    public int getDataTypeId() {
        return dataTypeId;
    }
    public String getDataTypeName() {
        return dataTypeName;
    }
}
