package com.mu.activity.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author 沐
 * Date: 2023-03-09 10:40
 * version: 1.0
 */

@Data
@TableName("activity_info")
public class ActivityDO {

    private String id;

    private Long userId;

    private Long itemId;

    private Double itemPrice;

    private Integer amount;

    private Double activityPrice;

    private Long promoId;

    /**
     * 1、默认状态
     * 2、创建成功
     * 3、创建失败
     */
    private Integer status;
}
