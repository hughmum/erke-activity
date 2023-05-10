package com.mu.activity.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mu.activity.entity.ActivityDO;
import org.apache.ibatis.annotations.Param;

/**
 * @author 沐
 * Date: 2023-03-10 10:01
 * version: 1.0
 */
public interface ActivityDOMapper extends BaseMapper<ActivityDO> {
    int selectActivityStatusById(@Param("id") String activityId);

    int deleteByPrimaryKey(String id);


    int insert(ActivityDO record);

    int insertSelective(ActivityDO record);

    ActivityDO selectByPrimaryKey(String id);


    int updateByPrimaryKeySelective(ActivityDO record);

    int updateByPrimaryKey(ActivityDO record);
}
