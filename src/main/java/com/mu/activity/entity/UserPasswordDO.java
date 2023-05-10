package com.mu.activity.entity;

import lombok.Data;

/**
 * @author 沐
 * Date: 2023-03-09 17:56
 * version: 1.0
 */
@Data
public class UserPasswordDO {

    private Long id;

    private String encrptPassword;

    private Long userId;

}