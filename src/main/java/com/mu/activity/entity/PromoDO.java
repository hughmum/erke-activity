package com.mu.activity.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author 沐
 * Date: 2023-03-09 17:51
 * version: 1.0
 */
@Data
public class PromoDO {
    private Long id;

    private String promoName;

    private Date startDate;

    private Date endDate;

    private Long itemId;

    private Double promoItemPrice;
}
