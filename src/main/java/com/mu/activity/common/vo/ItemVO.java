package com.mu.activity.common.vo;

import com.mu.activity.entity.PromoDO;
import lombok.Data;

/**
 * @author 沐
 * Date: 2023-03-09 17:57
 * version: 1.0
 */
@Data
public class ItemVO {

    private Long id;

    private String title;

    private Double price;

    private String description;

    private Integer sales;

    private String imgUrl;

    private PromoDO promo;
    private Integer stock;
}
