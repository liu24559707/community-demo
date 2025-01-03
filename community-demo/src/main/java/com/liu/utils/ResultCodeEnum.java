package com.liu.utils;

public enum ResultCodeEnum {
    SUCCESS(200,"success"),
    USERNAME_ERROR(501,"usernameError"),
    PASSWORD_ERROR(503,"passwordError"),
    NOT_LOGIN(504,"notLogin"),
    USERNAME_USED(505,"usernameUsed"),
    ADD_ERROR(506,"addError"),
    UPDATE_ERROR(507,"updateError"),
    DELETE_ERROR(508,"deleteError"),
    ORDERS_EXISTS(509,"该订单已存在"),
    ORDERS_ADD_FAIL(510,"下单失败"),
    ORDERS_DEL_FAIL(511,"撤销账单失败")
    ;

    private Integer code;
    private String message;

    //枚举调用构造器来赋值
    private ResultCodeEnum(Integer code,String message ){
        this.code=code;
        this.message=message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
