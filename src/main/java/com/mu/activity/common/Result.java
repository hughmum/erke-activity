package com.mu.activity.common;

import com.mu.activity.common.contants.ResultStatus;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 沐
 * Date: 2023-03-09 17:57
 * version: 1.0
 */
@Data
public class Result<T> implements Serializable {
    private static final long serialVersionUID = -3948389268046368059L;
    int code;
    String message;
    T data;

    public Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static Result<Object> success() {
        return new Result<Object>(ResultStatus.SUECCSS.code, ResultStatus.SUECCSS.message);
    }

    public static <T> Result<T> success(T data) {
        Result<T> res = new Result<>(ResultStatus.SUECCSS.code, ResultStatus.ORDER_CREATE_SUCCESS.message);
        res.setData(data);
        return res;
    }

    public static <T> Result<T> success(String message,T data){
        Result<T> res = new Result<>(ResultStatus.SUECCSS.code, message);
        res.setData(data);
        return res;
    }

    public static Result<Object> fail(){
        return new Result<Object>(ResultStatus.FAIL.code, ResultStatus.FAIL.message );
    }
    public static <T> Result<T> fail(T data){
        Result<T> res = new Result<>(ResultStatus.FAIL.code, ResultStatus.FAIL.message);
        res.setData(data);
        return res;
    }

    public static <T> Result<T> fail(String message,T data){
        Result<T> res = new Result<>(ResultStatus.FAIL.code, message);
        res.setData(data);
        return res;
    }

    public static <T> Result<T> build(ResultStatus status,T data){
        Result<T> res = new Result<>(status.code, status.message);
        res.setData(data);
        return res;
    }

    public static <T> Result<T> build(int code,String message,T data){
        Result<T> res = new Result<>(code, message);
        res.setData(data);
        return res;
    }



}
