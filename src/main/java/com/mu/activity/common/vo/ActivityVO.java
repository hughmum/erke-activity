package com.mu.activity.common.vo;

import lombok.Data;



/**
 * @author 沐
 * Date: 2023-03-09 17:57
 * version: 1.0
 */
@Data
public class ActivityVO {
    private String id;

    //报名的用户id
    private Long userId;

    //报名的活动id
    private Long itemId;

    //若非空，则表示是以报名活动方式下单
    private Long promoId;

    //报名活动的单价,若promoId非空，则表示报名活动所需分值
    private Double itemPrice;

    //报名数量
    private Integer amount;

    //报名金额,若promoId非空，则表示报名活动所需分值
    private Double activityPrice;


}
