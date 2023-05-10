package com.mu.activity.service;

import com.mu.activity.common.dto.resp.UserDTO;
import com.mu.activity.entity.UserDO;
import com.mu.activity.excption.BusinessException;

/**
 * @author Mr.mu
 */
public interface UserService {
    void register(UserDTO userDto);

    UserDO getUserById(Long id);

    UserDO validateLogin(String telephone, String password) throws BusinessException;

    UserDO getUserByPhone(String telephone);
}
