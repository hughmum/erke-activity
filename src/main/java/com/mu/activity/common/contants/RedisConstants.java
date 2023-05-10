package com.mu.activity.common.contants;

/**
 * @author 沐
 * Date: 2023-05-09 21:00
 * version: 1.0
 */
public class RedisConstants {
    public static final String LOGIN_CODE_KEY = "login:code:";
    public static final Long LOGIN_CODE_TTL = 2L;
    public static final String LOGIN_USER_KEY = "login:token:";
    public static final Long LOGIN_USER_TTL = 36000L;
    public static final String ITEM_PREFIX = "item_";
    public static final Long CACHE_NULL_TTL = 2L;

    public static final Long CACHE_ITEM_TTL = 30L;
    public static final String CACHE_ITEM_KEY = "cache:item:";
    public static final String CACHE_SHOPTYPE_KEY = "cache:shopType:";

    public static final String LOCK_ITEM_KEY = "lock:shop:";
    //用户抢活动时需要获取锁的key
    public static final String USER_ITEM_KEY = "userItem:";
    public static final Long LOCK_SHOP_TTL = 10L;
   
    public static final String SECKILL_STOCK_KEY = "seckill:stock:";
    public static final String BLOG_LIKED_KEY = "blog:liked:";
    public static final String FEED_KEY = "feed:";
    public static final String SHOP_GEO_KEY = "shop:geo:";
    public static final String USER_SIGN_KEY = "sign:";
}
