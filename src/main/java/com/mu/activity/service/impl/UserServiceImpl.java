package com.mu.activity.service.impl;

import com.mu.activity.common.dto.resp.UserDTO;
import com.mu.activity.dao.UserDOMapper;
import com.mu.activity.dao.UserPasswordDOMapper;
import com.mu.activity.entity.UserDO;
import com.mu.activity.entity.UserPasswordDO;
import com.mu.activity.excption.BusinessException;
import com.mu.activity.service.UserService;
import com.mu.activity.utils.BeanUtils;
import com.mu.activity.utils.IdUtils;
import com.mu.activity.utils.MD5Utils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author 沐
 * Date: 2023-03-10 11:28
 * version: 1.0
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserDOMapper userDOMapper;

    @Resource
    private UserPasswordDOMapper userPasswordDOMapper;

    @Override
    public void register(UserDTO userDTO) {
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userDTO, userDO);
        long userId = IdUtils.generatrId();
        userDO.setId(userId);
        userDOMapper.insert(userDO);

        UserPasswordDO userPasswordDO = new UserPasswordDO();
        userPasswordDO.setId(IdUtils.generatrId());
        userPasswordDO.setUserId(userId);
        userPasswordDO.setEncrptPassword(userDTO.getEncrptPassword());
        userPasswordDOMapper.insertSelective(userPasswordDO);
    }

    @Override
    public UserDO getUserById(Long id) {
        return userDOMapper.selectByPrimaryKey(id);
    }

    @Override
    public UserDO validateLogin(String telephone, String password) throws BusinessException {
        UserDO userDO = userDOMapper.selectByTelphone(telephone);
        if (userDO == null) {
            throw new BusinessException(500, "用户不存在,请输入正确的电话号码");
        }
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        boolean b = userPasswordDO.getEncrptPassword().equals(MD5Utils.encode(password));
        return b ? userDO : null;
    }

    @Override
    public UserDO getUserByPhone(String telephone) {
        return userDOMapper.selectByTelphone(telephone);
    }
}
