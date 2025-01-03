package com.liu.utils;

/**
 * 全局统一返回结果
 * @param <T>
 */
public class Result <T>{
    private Integer code;
    private String message;
    private T data;
    public Result(){}

    protected static <T> Result<T>  build(T data){
        Result<T>result=new Result<T>();
        if (data != null) {
            result.setData(data);
        }
        return result;
    }
    public static <T>Result<T>build(T body,Integer code,String message){
        Result<T>result=build(body);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
    public static <T>Result<T>build(T body,ResultCodeEnum resultCodeEnum){
        Result<T>result=build(body);
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        return result;
    }

    /**
     * 操作成功
     * @return
     */
    public static <T>Result<T> ok(T data){
        Result<T> result = build(data);
        return  build(data,ResultCodeEnum.SUCCESS);
    }

    public Result<T> message(String message){
        this.message=message;
        return this;
    }
    public Result<T> code(Integer code){
        this.code=code;
        return this;
    }
    public Result<T> data(T data){
        this.data=data;
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
