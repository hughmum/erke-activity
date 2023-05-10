package com.mu.activity.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mu.activity.common.contants.ResultStatus;
import com.mu.activity.dao.ItemStockDOMapper;
import com.mu.activity.dao.ActivityDOMapper;
import com.mu.activity.dao.SequenceDOMapper;
import com.mu.activity.entity.*;
import com.mu.activity.excption.BusinessException;
import com.mu.activity.service.ItemService;
import com.mu.activity.service.ActivityService;
import com.mu.activity.service.PromoService;
import com.mu.activity.service.UserService;
import com.mu.activity.service.provider.TransactionProducer;
import com.mu.activity.utils.JsonUtils;
import com.mu.activity.utils.RedisUtils;
import com.mu.activity.utils.SimpleRedisLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.Message;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.mu.activity.common.contants.RedisConstants.USER_ITEM_KEY;
import static com.mu.activity.common.contants.UserConstants.USER_INFO;

/**
 * @author 沐
 * Date: 2023-03-14 12:11
 * version: 1.0
 */
@Service
@Slf4j
public class ActivityServiceImpl extends ServiceImpl<ActivityDOMapper, ActivityDO> implements ActivityService {

    private static String ITEM_PREFIX = "item_";
    private static String STOCK_PREFIX = "stock_";
    private static Cache<String, Boolean> cache;
    private static String TX_ORDER_TOPIC = "tx_activity";
    private static String TX_ORDER_TAG = "activity_tag";

    @PostConstruct
    private void init() {
        cache = CacheBuilder.newBuilder()
                // 设置初始容量为50
                .initialCapacity(1000)
                // 50byte * 10000 = 500,000 -> 50KB
                .maximumSize(10000)
                .expireAfterAccess(30L, TimeUnit.MINUTES)
                .build();
    }


    @Resource
    private SequenceDOMapper sequenceDOMapper;

    @Resource
    private ItemService itemService;


    @Resource
    private ItemStockDOMapper itemStockDOMapper;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private UserService userService;

    @Resource
    private ActivityDOMapper activityDOMapper;

    @Resource
    private PromoService promoService;
    @Resource
    private TransactionProducer producer;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


 /*   @Override
    public String seckillActivity(Long userId, Long itemId, Long promoId, Integer amount) throws BusinessException, ExecutionException {
        //1. 校验下单状态 下单的活动是否存在 用户是否合法 报名数量是否正确
        ItemDO itemDO = null;
        itemDO = RedisUtils.get(ITEM_PREFIX + itemId);
        if (itemDO == null) {
            itemDO = itemService.queryGoods(itemId);
            RedisUtils.set(ITEM_PREFIX + itemId, itemDO, 1, TimeUnit.HOURS);
        }

        if (itemDO == null) {
            throw new BusinessException(ResultStatus.GOODS_NOT_EXIST);
        }

        UserDO userDO = userService.getUserById(userId);
        if (userDO == null) {
            throw new BusinessException(ResultStatus.USER_NOT_FOUND);
        }
        //校验下单数量是否合法
        if (amount <= 0 || amount > 99) {
            throw new BusinessException(ResultStatus.QUANTITY_ERROR);
        }
        //校验活动信息
        Date now = new Date(System.currentTimeMillis());
        PromoDO promoActivity = null;
        if (promoId != null) {
            // TODO: 2023/3/14 直接将活动缓存在内存中，报名活动常驻内存
            promoActivity = promoService.getPromoByItemId(itemId);
            if (promoActivity == null) {
                throw new BusinessException(ResultStatus.ACTIVITY_NOT_EXIST);
            } else if (now.before(promoActivity.getStartDate())) {
                throw new BusinessException(ResultStatus.ACTIVITY_NOT_STARTED);
            } else if (now.after(promoActivity.getEndDate())) {
                throw new BusinessException(ResultStatus.ACTIVITY_HAS_ENDED);
            }
        }
        // 先查本地缓存活动是否有库存
        // 穿透型缓存   设置了一个spring 中的cache作为中间件缓存，缓存预热后加载到里面，
        int dbAmount = 0;
        if (!cache.get(STOCK_PREFIX + itemId, () -> {
            Integer count = RedisUtils.get(STOCK_PREFIX + itemId);
            System.out.println(count);
            if (count == null) {
                return false;
            }
            return count > 0;
        })) {
            dbAmount = itemStockDOMapper.selectByItemId(itemId).getStock();
        }
        RedisUtils.set(STOCK_PREFIX + itemId, dbAmount);
        if (dbAmount == 0) {
            throw new BusinessException(ResultStatus.STOCK_NOT_ENOUGH);
        }
        Long curUserId = USER_INFO.get().getId();

        synchronized (curUserId.toString().intern()) {
            ActivityService proxy = (ActivityService) AopContext.currentProxy();
            return proxy.createActivity2(userId, itemId, promoId, amount, promoActivity, itemDO);
        }

    }
*/
    @Override
    public String seckillActivity(Long userId, Long itemId, Long promoId, Integer amount) throws BusinessException, ExecutionException {
        //1. 校验下单状态 下单的活动是否存在 用户是否合法 报名数量是否正确
        ItemDO itemDO = null;
        itemDO = RedisUtils.get(ITEM_PREFIX + itemId);
        if (itemDO == null) {
            itemDO = itemService.queryGoods(itemId);
            RedisUtils.set(ITEM_PREFIX + itemId, itemDO, 1, TimeUnit.HOURS);
        }

        if (itemDO == null) {
            throw new BusinessException(ResultStatus.GOODS_NOT_EXIST);
        }

        UserDO userDO = userService.getUserById(userId);
        if (userDO == null) {
            throw new BusinessException(ResultStatus.USER_NOT_FOUND);
        }
        //校验下单数量是否合法
        if (amount <= 0 || amount > 99) {
            throw new BusinessException(ResultStatus.QUANTITY_ERROR);
        }
        //校验活动信息
        Date now = new Date(System.currentTimeMillis());
        PromoDO promoActivity = null;
        if (promoId != null) {
            // TODO: 2023/3/14 直接将活动缓存在内存中，报名活动常驻内存
            promoActivity = promoService.getPromoByItemId(itemId);
            if (promoActivity == null) {
                throw new BusinessException(ResultStatus.ACTIVITY_NOT_EXIST);
            } else if (now.before(promoActivity.getStartDate())) {
                throw new BusinessException(ResultStatus.ACTIVITY_NOT_STARTED);
            } else if (now.after(promoActivity.getEndDate())) {
                throw new BusinessException(ResultStatus.ACTIVITY_HAS_ENDED);
            }
        }
        // 先查本地缓存活动是否有库存
        // 穿透型缓存   设置了一个spring 中的cache作为中间件缓存，缓存预热后加载到里面，
        int dbAmount = 0;
        if (!cache.get(STOCK_PREFIX + itemId, () -> {
            Integer count = RedisUtils.get(STOCK_PREFIX + itemId);
            System.out.println(count);
            if (count == null) {
                return false;
            }
            return count > 0;
        })) {
            throw new BusinessException(ResultStatus.STOCK_NOT_ENOUGH);
        }
        dbAmount = itemStockDOMapper.selectByItemId(itemId).getStock();
        RedisUtils.set(STOCK_PREFIX + itemId, dbAmount);
        if (dbAmount == 0) {
            throw new BusinessException(ResultStatus.STOCK_NOT_ENOUGH);
        }
        Long curUserId = USER_INFO.get().getId();

        //创建锁对象，
        SimpleRedisLock lock = new SimpleRedisLock(USER_ITEM_KEY + userId, stringRedisTemplate);
        //获取锁
        boolean isLock = lock.tryLock(1200);
        //是否获取锁成功
        if (!isLock) {
            //获取锁失败
            throw new BusinessException(ResultStatus.ORDER_NO_REPETITION_ALLOWED);
        }
        try {
            ActivityService proxy = (ActivityService) AopContext.currentProxy();
            return proxy.createActivity2(userId, itemId, promoId, amount, promoActivity, itemDO);
        } finally {
            //释放锁
            lock.unlock();
        }

    }


    @Override
    @Transactional
    public String createActivity2(Long userId, Long itemId, Long promoId, Integer amount,PromoDO promoActivity,ItemDO itemDO) throws BusinessException {
        //一人一单
        int orderCount = query().eq("user_id", userId).eq("item_id", itemId).count();
        if (orderCount > 0) {
            //用户已经买过了
            throw new BusinessException(ResultStatus.ORDER_EXIST);
        }
        // 预扣减
        Long stock = RedisUtils.decr(STOCK_PREFIX + itemId, amount);
        if (stock < 0) {
            // 反向补偿
            RedisUtils.incr(STOCK_PREFIX + itemId, amount);
            Integer count = RedisUtils.get(STOCK_PREFIX + itemId);
            // 判断库存是否为0
            if (count == 0) {
                cache.put(STOCK_PREFIX + itemId, false);
            }
            throw new BusinessException(ResultStatus.STOCK_NOT_ENOUGH);
        }

        // 创建订单
        ActivityDO activityDO = new ActivityDO();
        activityDO.setUserId(userId);
        activityDO.setItemId(itemId);
        activityDO.setAmount(amount);
        if (promoId != null) {
            // 促销所需分值
            activityDO.setItemPrice(promoActivity.getPromoItemPrice());
        } else {
            // 日常所需分值
            activityDO.setItemPrice(itemDO.getPrice());
        }
        activityDO.setPromoId(promoId);
        // fixme 抽象工具类
        activityDO.setActivityPrice((new BigDecimal(activityDO.getItemPrice()).multiply(new BigDecimal(amount))).doubleValue());
        //生成交易流水号,订单号
        activityDO.setId(generateActivityNo());

        // 记录库存流水
        String stockLogId = itemService.initStockLog(itemId,amount);

        // 发送消息
        try {
            Map<String, Object> msg = new HashMap<>(4);
            msg.put("itemId",itemId);
            msg.put("amount",amount);
            msg.put("stockLogId",stockLogId);
            msg.put("activityId",activityDO.getId());
            Message message = new Message();
            message.setTopic(TX_ORDER_TOPIC);
            message.setTags(TX_ORDER_TAG);
            message.setBody(JsonUtils.objectToJson(msg).getBytes(StandardCharsets.UTF_8));

            HashMap<String, Object> args = new HashMap<>(2);
            args.put("activity",activityDO);
            args.put("stockLogId",stockLogId);
            producer.sendMessage(message,args);
        } catch (MQClientException e) {
            log.error("errorMessage:{}    method:{}",e.getErrorMessage(),Thread.currentThread().getStackTrace()[1].getMethodName());
            throw new BusinessException(ResultStatus.CREATE_ORDER_FAIL);
        }
        return activityDO.getId();
    }

    //缓存预热
    @Override
    public String preheat(Long itemId) {
        cache.put(STOCK_PREFIX + itemId, true);
        RedisUtils.set(STOCK_PREFIX + itemId, itemStockDOMapper.selectByItemId(itemId).getStock());
        return null;
    }


    @Override
    public String createActivity(Long userId, Long itemId, Long promoId, Integer amount) throws BusinessException, ExecutionException {
        //1. 校验下单状态 下单的活动是否存在 用户是否合法 报名数量是否正确
        ItemDO itemDO = null;
        itemDO = RedisUtils.get(ITEM_PREFIX + itemId);
        if (itemDO == null) {
            itemDO = itemService.queryGoods(itemId);
            RedisUtils.set(ITEM_PREFIX + itemId, itemDO, 1, TimeUnit.HOURS);
        }

        if (itemDO == null) {
            throw new BusinessException(ResultStatus.GOODS_NOT_EXIST);
        }

        UserDO userDO = userService.getUserById(userId);
        if (userDO == null) {
            throw new BusinessException(ResultStatus.USER_NOT_FOUND);
        }
        //校验下单数量是否合法
        if (amount <= 0 || amount > 99) {
            throw new BusinessException(ResultStatus.QUANTITY_ERROR);
        }

        //校验活动信息
        Date now = new Date(System.currentTimeMillis());
        PromoDO promoActivity = null;
        if (promoId != null) {
            // TODO: 2023/3/14 直接将活动缓存在内存中，报名活动常驻内存
            promoActivity = promoService.getPromoByItemId(itemId);
            if (promoActivity == null) {
                throw new BusinessException(ResultStatus.ACTIVITY_NOT_EXIST);
            } else if (now.before(promoActivity.getStartDate())) {
                throw new BusinessException(ResultStatus.ACTIVITY_NOT_STARTED);
            } else if (now.after(promoActivity.getEndDate())) {
                throw new BusinessException(ResultStatus.ACTIVITY_HAS_ENDED);
            }
        }

        // 先查本地缓存活动是否有库存
        // 穿透型缓存   设置了一个spring 中的cache作为中间件缓存，缓存预热后加载到里面，
        int dbAmount = 0;
        if (!cache.get(STOCK_PREFIX + itemId, () -> {
            Integer count = RedisUtils.get(STOCK_PREFIX + itemId);
            System.out.println(count);
            if (count == null) {
                return false;
            }
            return count > 0;
        }))
        dbAmount = itemStockDOMapper.selectByItemId(itemId).getStock();
        RedisUtils.set(STOCK_PREFIX + itemId, amount);
        if (dbAmount == 0) {
            throw new BusinessException(ResultStatus.STOCK_NOT_ENOUGH);
        }
        // 预扣减
        Long stock = RedisUtils.decr(STOCK_PREFIX + itemId, amount);
        if (stock < 0) {
            // 反向补偿
            RedisUtils.incr(STOCK_PREFIX + itemId, amount);
            Integer count = RedisUtils.get(STOCK_PREFIX + itemId);
            // 判断库存是否为0
            if (count == 0) {
                cache.put(STOCK_PREFIX + itemId, false);
            }
            throw new BusinessException(ResultStatus.STOCK_NOT_ENOUGH);
        }

        // 创建订单
        ActivityDO activityDO = new ActivityDO();
        activityDO.setUserId(userId);
        activityDO.setItemId(itemId);
        activityDO.setAmount(amount);
        if (promoId != null) {
            // 促销所需分值
            activityDO.setItemPrice(promoActivity.getPromoItemPrice());
        } else {
            // 日常所需分值
            activityDO.setItemPrice(itemDO.getPrice());
        }
        activityDO.setPromoId(promoId);
        // fixme 抽象工具类
        activityDO.setActivityPrice((new BigDecimal(activityDO.getItemPrice()).multiply(new BigDecimal(amount))).doubleValue());
        //生成交易流水号,订单号
        activityDO.setId(generateActivityNo());

        // 记录库存流水
        String stockLogId = itemService.initStockLog(itemId,amount);

        // 发送消息
        try {
            Map<String, Object> msg = new HashMap<>(4);
            msg.put("itemId",itemId);
            msg.put("amount",amount);
            msg.put("stockLogId",stockLogId);
            msg.put("activityId",activityDO.getId());
            Message message = new Message();
            message.setTopic(TX_ORDER_TOPIC);
            message.setTags(TX_ORDER_TAG);
            message.setBody(JsonUtils.objectToJson(msg).getBytes(StandardCharsets.UTF_8));

            HashMap<String, Object> args = new HashMap<>(2);
            args.put("activity",activityDO);
            args.put("stockLogId",stockLogId);
            producer.sendMessage(message,args);
        } catch (MQClientException e) {
            log.error("errorMessage:{}    method:{}",e.getErrorMessage(),Thread.currentThread().getStackTrace()[1].getMethodName());
            throw new BusinessException(ResultStatus.CREATE_ORDER_FAIL);
        }
        return activityDO.getId();
    }

    @Override
    public int getActivityStatus(String activityId) {
        return activityDOMapper.selectActivityStatusById(activityId);
    }

    // FIXME: 2023/1/12 抽象成工具类
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String generateActivityNo() {
        //订单号有16位
        StringBuilder stringBuilder = new StringBuilder();
        //前8位为时间信息，年月日
        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-", "");
        stringBuilder.append(nowDate);

        //中间6位为自增序列
        //获取当前sequence
        int sequence = 0;
        SequenceDO sequenceDO = sequenceDOMapper.getSequenceByName("activity_info");
        sequence = sequenceDO.getCurrentValue();
        sequenceDO.setCurrentValue(sequenceDO.getCurrentValue() + sequenceDO.getStep());
        sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);
        String sequenceStr = String.valueOf(sequence);
        for (int i = 0; i < 6 - sequenceStr.length(); i++) {
            stringBuilder.append(0);
        }
        stringBuilder.append(sequenceStr);

        //最后2位为分库分表位,暂时写死
        stringBuilder.append("00");

        return stringBuilder.toString();
    }

}
