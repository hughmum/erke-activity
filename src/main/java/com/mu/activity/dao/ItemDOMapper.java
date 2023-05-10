package com.mu.activity.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mu.activity.common.request.PageRequest;
import com.mu.activity.entity.ActivityDO;
import com.mu.activity.entity.ItemDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ItemDOMapper extends BaseMapper<ItemDO> {
    int deleteByPrimaryKey(Long id);

    int insert(ItemDO record);

    int insertSelective(ItemDO record);

    ItemDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ItemDO record);

    int updateByPrimaryKey(ItemDO record);

    List<ItemDO> selectListAll(@Param("item") ItemDO goods, @Param("page") PageRequest pageRequest);

    int increaseSales(@Param("id")Long id,@Param("amount")Integer amount);
}