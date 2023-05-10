package com.mu.activity.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mu.activity.entity.ActivityDO;
import com.mu.activity.entity.ItemStockDO;
import org.apache.ibatis.annotations.Param;

public interface ItemStockDOMapper extends BaseMapper<ItemStockDO> {

    int deleteByPrimaryKey(Integer id);

    int insert(ItemStockDO record);

    int insertSelective(ItemStockDO record);


    ItemStockDO selectByPrimaryKey(Integer id);

    ItemStockDO selectByItemId(Long itemId);

    int decreaseStock(@Param("itemId") Long itemId, @Param("amount") Integer amount);

    int updateByPrimaryKeySelective(ItemStockDO record);


    int updateByPrimaryKey(ItemStockDO record);
}