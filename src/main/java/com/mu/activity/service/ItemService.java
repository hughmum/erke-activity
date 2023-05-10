package com.mu.activity.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mu.activity.common.Page;
import com.mu.activity.common.request.PageRequest;
import com.mu.activity.common.vo.ItemVO;
import com.mu.activity.entity.ItemDO;

/**
 * @author 沐
 * Date: 2023-03-10 11:25
 * version: 1.0
 */
public interface ItemService extends IService<ItemDO> {
    /**
     * 创建活动
     * @param itemDO 活动实体对象
     */
    boolean createItem(ItemDO itemDO);


    /**
     * 查询活动
     * @param id 活动id
     * @return 活动实体
     */
    ItemDO queryGoods(Long id);

    /**
     *
     * @param id 活动id
     * @return 删除结果
     */
    boolean delete(Long id);

    /**
     *
     * @param itemDO
     * @return
     */
    boolean updateGoods(ItemDO itemDO);

    Page<ItemVO> goodsList(ItemDO goods, PageRequest pageRequest);

    //库存扣减
    boolean decreaseStock(Long itemId,Integer amount);

    //活动销量增加
    boolean increaseSales(Long itemId,Integer amount);

    //初始化库存流水日志
    String initStockLog(Long itemId, Integer amount);
}
