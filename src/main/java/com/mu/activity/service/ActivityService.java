package com.mu.activity.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mu.activity.entity.ActivityDO;
import com.mu.activity.entity.ItemDO;
import com.mu.activity.entity.PromoDO;
import com.mu.activity.excption.BusinessException;

import java.util.concurrent.ExecutionException;

/**
 * @author 沐
 * Date: 2023-03-10 11:26
 * version: 1.0
 */
public interface ActivityService extends IService<ActivityDO> {
    //使用1,通过前端url上传过来报名活动id，然后下单接口内校验对应id是否属于对应活动且活动已开始
    //    //2.直接在下单接口内判断对应的活动是否存在报名活动，若存在进行中的则以报名所需分值下单
    String createActivity(Long userId, Long itemId, Long promoId, Integer amount) throws BusinessException, ExecutionException;

    int getActivityStatus(String activityId);

    String seckillActivity(Long userId, Long itemId, Long promoId, Integer amount) throws BusinessException, ExecutionException;

    String createActivity2(Long userId, Long itemId, Long promoId, Integer amount, PromoDO promoActivity, ItemDO itemDO) throws BusinessException;

    String preheat(Long itemId);
}
