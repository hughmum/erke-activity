package com.mu.activity.excption;

import com.mu.activity.common.contants.ResultStatus;

/**
 * @author 沐
 * Date: 2023-03-10 10:34
 * version: 1.0
 */
public class BusinessException extends Exception {


    Integer code;
    public BusinessException(Integer code,String message) {
        super(message);
        this.code = code;
    }
    public BusinessException(ResultStatus status) {
        super(status.message);
        this.code = status.code;
    }


    public Integer getCode(){
        return this.code;
    }
}
