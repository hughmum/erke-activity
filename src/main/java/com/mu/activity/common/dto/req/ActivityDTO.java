package com.mu.activity.common.dto.req;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 沐
 * Date: 2023-03-10 0:22
 * version: 1.0
 */
@Data
public class ActivityDTO {
    //2018102100012828
    private String id;

    //报名的用户id
    private Integer userId;

    //报名的活动id
    private Integer itemId;

    //若非空，则表示是以报名活动方式下单
    private Integer promoId;

    //报名活动的单价,若promoId非空，则表示报名活动所需分值
    private BigDecimal itemPrice;

    //报名数量
    private Integer amount;

    //报名金额,若promoId非空，则表示报名活动所需分值
    private BigDecimal activityPrice;


}
