package com.mu.activity.entity;

import lombok.Data;

/**
 * @author 沐
 * Date: 2023-03-09 17:52
 * version: 1.0
 */
@Data
public class StockLogDO {

    private String stockLogId;

    private Long itemId;

    private Integer amount;


    /**
     * 1 表示创建 2表示订单创建成功 3表示订单创建失败
     */

    private Integer status;


}