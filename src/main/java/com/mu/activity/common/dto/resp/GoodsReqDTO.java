package com.mu.activity.common.dto.resp;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author 沐
 * Date: 2023-03-10 0:19
 * version: 1.0
 */
@Data
@ApiModel
public class GoodsReqDTO {

    private String title;

    private Double price;

    private String description;

    private Integer sales;

    private String imgUrl;


}