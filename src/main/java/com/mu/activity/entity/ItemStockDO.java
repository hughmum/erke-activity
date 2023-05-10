package com.mu.activity.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * item_stock
 * @author 
 */
@Data
public class ItemStockDO implements Serializable {
    private Long id;

    private Integer stock;

    private Long itemId;

    private static final long serialVersionUID = 1L;
}