package com.mu.activity.service;

import com.mu.activity.entity.PromoDO;

/**
 * @author 沐
 * Date: 2023-03-10 11:27
 * version: 1.0
 */
public interface PromoService {
    PromoDO getPromoByItemId(Long itemId);
}
