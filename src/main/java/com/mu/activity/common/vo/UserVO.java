package com.mu.activity.common.vo;

import lombok.Data;

/**
 * @author 沐
 * Date: 2023-03-09 17:57
 * version: 1.0
 */
@Data
public class UserVO {
    private Long id;
    private String name;
    private Byte gender;
    private Integer age;
    private String telphone;
    private String registerMode;
    private String thirdPartyId;
}
