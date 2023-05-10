package com.mu.activity.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * user_info
 * @author 
 */
@Data
public class UserDO implements Serializable {
    private Long id;

    private String name;

    /**
     * //1代表男性，2代表女性
     */
    private Byte gender;

    private Long age;

    private String telphone;

    /**
     * //byphone,bywechat,byalipay
     */
    private String registerMode;

    private String thirdPartyId;

    private static final long serialVersionUID = 1L;
}