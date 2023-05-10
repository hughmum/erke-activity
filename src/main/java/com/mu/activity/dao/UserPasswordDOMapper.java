package com.mu.activity.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mu.activity.entity.ActivityDO;
import com.mu.activity.entity.UserPasswordDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 沐
 * Date: 2023-03-10 10:02
 * version: 1.0
 */
public interface UserPasswordDOMapper extends BaseMapper<UserPasswordDO> {
    int insertSelective(UserPasswordDO record);

    UserPasswordDO selectByUserId(Long id);
}
