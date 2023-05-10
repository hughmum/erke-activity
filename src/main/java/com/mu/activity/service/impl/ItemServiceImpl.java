package com.mu.activity.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.mu.activity.common.Page;
import com.mu.activity.common.request.PageRequest;
import com.mu.activity.common.vo.ItemVO;
import com.mu.activity.dao.ItemDOMapper;
import com.mu.activity.dao.ItemStockDOMapper;
import com.mu.activity.dao.StockLogDOMapper;
import com.mu.activity.entity.ItemDO;
import com.mu.activity.entity.ItemStockDO;
import com.mu.activity.entity.PromoDO;
import com.mu.activity.entity.StockLogDO;
import com.mu.activity.service.ItemService;
import com.mu.activity.service.PromoService;
import com.mu.activity.utils.BeanUtils;
import com.mu.activity.utils.CacheClient;
import com.mu.activity.utils.IdUtils;
import com.mu.activity.utils.RedisUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.mu.activity.common.contants.RedisConstants.CACHE_ITEM_TTL;
import static com.mu.activity.common.contants.RedisConstants.ITEM_PREFIX;

/**
 * @author 沐
 * Date: 2023-03-12 15:50
 * version: 1.0
 */
@Service
public class ItemServiceImpl extends ServiceImpl<ItemDOMapper, ItemDO> implements ItemService {

    //防止缓存被击穿
    private static ItemDO EMPTY_OBJECT = new ItemDO();

    @Resource
    private ItemDOMapper itemDOMapper;

    @Resource
    private CacheClient cacheClient;

    @Resource
    private ItemStockDOMapper itemStockDOMapper;

    @Resource
    private StockLogDOMapper stockLogDOMapper;

    @Resource
    private PromoService promoService;


    @Override
    public boolean createItem(ItemDO itemDO) {
        long id = IdUtils.generatrId();
        itemDO.setId(id);
        int i = itemDOMapper.insert(itemDO);
        return i > 0;
    }

    @Override
    public ItemDO queryGoods(Long id) {

        //缓存击穿
        ItemDO itemDO = cacheClient.queryWithMutex(ITEM_PREFIX, id, ItemDO.class, this::getById,CACHE_ITEM_TTL,TimeUnit.MINUTES);
        return itemDO;

    }

    @Override
    public boolean delete(Long id) {
        return itemDOMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public boolean updateGoods(ItemDO itemDO) {
        // todo 更新后需要更新缓存，是一个典型的数据不一致问题 解决办法：1.延时双删 2.串行化执行
        int updateRetCount = itemDOMapper.updateByPrimaryKeySelective(itemDO);
        return updateRetCount > 0;
    }

    @Override
    public Page<ItemVO> goodsList(ItemDO goods, PageRequest pageRequest) {
        Integer pageNum = pageRequest.getPageNum();
        Integer pageSize = pageRequest.getPageSize();
        com.github.pagehelper.Page<Object> pageHelper = PageHelper.startPage(pageNum, pageSize);
        List<ItemDO> list = itemDOMapper.selectListAll(goods, pageRequest);

        //将list转化为供前端可以展示的VOlist
        List<ItemVO> listVO = list.stream().map(itemDO -> {
            ItemVO itemVO = new ItemVO();
            BeanUtils.copyProperties(itemDO, itemVO);
            ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
            PromoDO promo = promoService.getPromoByItemId(itemVO.getId());
            itemVO.setPromo(promo);
            itemVO.setStock(itemStockDO.getStock());
            return itemVO;
        }).collect(Collectors.toList());

        Page<ItemVO> page = new Page<ItemVO>();
        page.setData(listVO);
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);
        page.setTotal(pageHelper.getTotal());
        return page;
    }

    @Override
    public boolean decreaseStock(Long itemId, Integer amount) {
        int updateRetCount = itemStockDOMapper.decreaseStock(itemId, amount);
        return updateRetCount > 0;
    }

    @Override
    public boolean increaseSales(Long itemId, Integer amount) {
        int updateRetCount = itemDOMapper.increaseSales(itemId, amount);
        return updateRetCount > 0;
    }

    @Override
    public String initStockLog(Long itemId, Integer amount) {
        StockLogDO stockLogDO = new StockLogDO();
        stockLogDO.setItemId(itemId);
        stockLogDO.setAmount(amount);
        stockLogDO.setStockLogId(UUID.randomUUID().toString().replace("-",""));
        stockLogDO.setStatus(1);

        stockLogDOMapper.insertSelective(stockLogDO);

        return stockLogDO.getStockLogId();

    }


}

