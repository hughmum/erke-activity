package com.mu.activity.service.impl;

import com.mu.activity.dao.PromoDOMapper;
import com.mu.activity.entity.PromoDO;
import com.mu.activity.service.PromoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 沐
 * Date: 2023-03-13 21:45
 * version: 1.0
 */
@Service
public class PromoServiceImpl implements PromoService {
    @Autowired
    private PromoDOMapper promoDOMapper;

    @Override
    public PromoDO getPromoByItemId(Long itemId) {
        return promoDOMapper.selectByItemId(itemId);
    }
}
