package com.mu.activity.controller;

import com.mu.activity.common.Result;
import com.mu.activity.common.contants.ResultStatus;
import com.mu.activity.entity.UserDO;
import com.mu.activity.excption.BusinessException;
import com.mu.activity.service.ActivityService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.mu.activity.common.contants.UserConstants.USER_INFO;

/**
 * @author 沐
 * Date: 2023-03-14 12:06
 * version: 1.0
 */
@RestController
@Slf4j
@RequestMapping("activity")
@CrossOrigin(origins = {"*"}, allowCredentials = "true")
public class  ActivityController {
    @Resource
    private ActivityService activityService;

    //封装下单请求
    @PostMapping(value = "/createActivity")
    public Result<Object> createActivity(@NotNull(message = "活动不能为空") @RequestParam(name = "itemId") Long itemId,
                                      @NotNull(message = "数量不能为空") @RequestParam(name = "amount") Integer amount,
                                      @RequestParam(name = "promoId", required = false) Long promoId) throws BusinessException, ExecutionException {

        UserDO userDO = USER_INFO.get();
        if (userDO == null) {
            throw new BusinessException(ResultStatus.LOGIN_EXPIRED);
        }
        Long userId = userDO.getId();
        String activityId = activityService.seckillActivity(userId, itemId, promoId, amount);
        Map<String, Object> data = new HashMap(1);
        data.put("activityId", activityId);
        return Result.build(ResultStatus.SUECCSS, data);
    }

    //缓存预热
    @GetMapping(value = "/preheat")
    public Result<Object> createActivity(@RequestParam(name = "itemId", required = false) Long itemId)  {
        activityService.preheat(itemId);
        return Result.build(ResultStatus.SUECCSS, "OK");
    }
    @GetMapping("getActivityStatus")
    @ApiOperation(value = "创建订单的回调接口")
    public Result<Object> getActivityStatus(@RequestParam("orederId") String activityId) {
        int status = activityService.getActivityStatus(activityId);
        /**
         *  1 刚创建
         *  2 创建成功
         *  3 创建失败
         */
        switch (status) {
            case 2:
                return Result.build(ResultStatus.ORDER_CREATE_SUCCESS, null);
            case 3:
                return Result.build(ResultStatus.ORDER_CREATE_FAIL, null);
            default:
                return Result.build(ResultStatus.ORDER_CREATING, null);
        }
    }
}
