package com.mu.activity.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mu.activity.entity.ActivityDO;
import com.mu.activity.entity.StockLogDO;

/**
 * @author 沐
 * Date: 2023-03-10 10:02
 * version: 1.0
 */
public interface StockLogDOMapper extends BaseMapper<StockLogDO> {
    int deleteByPrimaryKey(String stockLogId);
    int insert(StockLogDO record);

    int insertSelective(StockLogDO record);

    StockLogDO selectByPrimaryKey(String stockLogId);

    int updateByPrimaryKeySelective(StockLogDO record);

    int updateByPrimaryKey(StockLogDO record);
}
