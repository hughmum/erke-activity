package com.mu.activity.common.dto.resp;

import lombok.Data;

/**
 * @author 沐
 * Date: 2023-03-10 0:20
 * version: 1.0
 */
@Data
public class UserDTO {
    private String name;
    private Byte gender;
    private Integer age;
    private String telphone;
    private String registerMode;
    private String thirdPartyId;

    private String encrptPassword;
    private Long userId;
}
