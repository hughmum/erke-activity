package com.mu.activity.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mu.activity.entity.ActivityDO;
import com.mu.activity.entity.SequenceDO;

/**
 * @author 沐
 * Date: 2023-03-10 10:02
 * version: 1.0
 */
public interface SequenceDOMapper extends BaseMapper<SequenceDO> {
    int deleteByPrimaryKey(String name);

    int insert(SequenceDO record);

    int insertSelective(SequenceDO record);

    SequenceDO selectByPrimaryKey(String name);

    SequenceDO getSequenceByName(String name);

    int updateByPrimaryKeySelective(SequenceDO record);

    int updateByPrimaryKey(SequenceDO record);
}
