package com.mu.activity.controller;

import com.mu.activity.common.Result;
import com.mu.activity.common.annotations.Access;
import com.mu.activity.common.dto.resp.UserDTO;
import com.mu.activity.common.vo.UserVO;
import com.mu.activity.entity.UserDO;
import com.mu.activity.excption.BusinessException;
import com.mu.activity.service.UserService;
import com.mu.activity.utils.BeanUtils;
import com.mu.activity.utils.JwtUtils;
import com.mu.activity.utils.MD5Utils;
import com.mu.activity.utils.RedisUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.mu.activity.common.contants.UserConstants.USER_INFO;

/**
 * @author 沐
 * Date: 2023-03-10 11:29
 * version: 1.0
 */
@RestController
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
@Slf4j
public class UserController {


    @Resource
    private UserService userService;

    private static final String OPT_PREFIX = "opt_";
    private static final String USER_PREFIX = "user_";


    @ApiOperation("注册接口")
    @PostMapping(value = "/register")
    @Access
    public Result<Object> register(@RequestParam(name = "telephone") String telephone,
                                   @RequestParam(name = "optCode") String otpCode,
                                   @RequestParam(name = "name") String name,
                                   @RequestParam(name = "gender") Integer gender,
                                   @RequestParam(name = "age") Integer age,
                                   @RequestParam(name = "password") String password) throws BusinessException {
        if (otpCode == null) {
            throw new BusinessException(500, "验证码不能为空");
        }
        String opt = RedisUtils.get(OPT_PREFIX + telephone);
        if (!otpCode.equals(opt)) {
            throw new BusinessException(500, "验证码错误");
        }
        UserDO user = userService.getUserByPhone(telephone);
        if (user != null) {
            throw new BusinessException(500, "用户已经注册");
        }
        UserDTO userDto = new UserDTO();

        userDto.setName(name);
        userDto.setTelphone(telephone);
        userDto.setGender(new Byte(String.valueOf(gender.intValue())));
        userDto.setAge(age);
        userDto.setRegisterMode("byphone");
        userDto.setEncrptPassword(MD5Utils.encode(password));

        userService.register(userDto);
        return Result.build(200, "注册成功", null);
    }

    @ApiOperation("用户短信接口")
    @Access
    @GetMapping(value = "/getOtp")
    public Result<Object> getOtp(@NotNull(message = "用户电话不能为空") @RequestParam(name = "telephone") String telephone) {
        //模拟验证码
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        randomInt += 10000;
        String otpCode = String.valueOf(randomInt);
        //TODO: 要解决验证码短时间不能重复发送问题
        RedisUtils.set(OPT_PREFIX + telephone, otpCode, 1000, TimeUnit.SECONDS);

        //将验证码通过短信通道发送给用户 省略
        log.info("telephone = " + telephone + " & otpCode = " + otpCode);
        return Result.build(200, "验证码已发送", null);
    }

    @GetMapping("/get")
    public Result<UserVO> getUser(@RequestParam(name = "id") @NotNull(message = "用户Id不能为空") Long id) throws BusinessException {
        // 从threadLocal中取
        UserDO userDO
                = USER_INFO.get();
        if (userDO != null) {
            log.info("user in threadLocal : {}", userDO.toString());
        }

        if (userDO == null) {
            userDO = userService.getUserById(id);
        }
        //若获取的对应用户信息不存在
        if (userDO == null) {
            throw new BusinessException(200, "用户不存在");
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userDO, userVO);

        //返回通用对象
        return Result.success(userVO);
    }


    @ApiOperation("用户登录")
    @PostMapping(value = "/login")
    @Access
    public Result<Object> login(@NotNull(message = "用户电话不能为空") @RequestParam(name = "telephone") String telephone,
                                @NotNull(message = "用户密码不能为空") @RequestParam(name = "password") String password) throws BusinessException {

        //用户登录服务,用来校验用户登录是否合法
        UserDO userDO = userService.validateLogin(telephone, password);

        if (userDO == null) {
            throw new BusinessException(500, "密码错误");
        }
        RedisUtils.set(USER_PREFIX + userDO.getId(), userDO, 60, TimeUnit.MINUTES);
        log.info("userId:{}", userDO.getId());
        String token = JwtUtils.createToken(userDO.getId(), 24 *60);
        Map<String, Object> data = new HashMap<>(4);
        data.put("userId", userDO.getId());
        data.put("header", "token");
        data.put("token", token);
        return Result.build(200, "登录成功", data);

    }

}
