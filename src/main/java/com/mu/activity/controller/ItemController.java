package com.mu.activity.controller;

import com.mu.activity.common.Page;
import com.mu.activity.common.Result;
import com.mu.activity.common.annotations.Access;
import com.mu.activity.common.annotations.RateLimiter;
import com.mu.activity.common.dto.resp.GoodsReqDTO;
import com.mu.activity.common.request.PageRequest;
import com.mu.activity.common.vo.ItemVO;
import com.mu.activity.entity.ItemDO;
import com.mu.activity.entity.PromoDO;
import com.mu.activity.excption.BusinessException;
import com.mu.activity.service.ItemService;
import com.mu.activity.service.PromoService;
import com.mu.activity.utils.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author 沐
 * Date: 2023-03-12 15:46
 * version: 1.0
 */
@RestController
@RequestMapping("/item")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
@Slf4j
public class ItemController {
    @Resource
    private ItemService itemService;

    @Resource
    private PromoService promoService;

    @PostMapping("/create")
    public Result<Object> createGoods(@RequestBody GoodsReqDTO goodsReqDTO) {
        ItemDO itemDO = new ItemDO();
        BeanUtils.copyProperties(goodsReqDTO, itemDO);
        boolean res = itemService.createItem(itemDO);
        return res ? Result.success("添加成功", null) : Result.fail("添加失败", null);
    }



    @GetMapping("/goodsDetail/{id}")
    @Access
    @RateLimiter
    public Result<ItemVO> getGoodsDetail(@PathVariable("id") Long id) {
        ItemDO itemDO = itemService.queryGoods(id);
        ItemVO itemVO = null;
        if (itemDO != null) {
            itemVO = new ItemVO();
            BeanUtils.copyProperties(itemDO, itemVO);
            PromoDO promo = promoService.getPromoByItemId(id);
            itemVO.setPromo(promo);
        }
        return Result.success(itemVO);
    }

    @DeleteMapping("/delete")
    public Result<Object> deleteGoods(@RequestParam("id") Long id) {
        boolean res = itemService.delete(id);
        return res ? Result.success("删除成功", null) : Result.fail("删除失败", null);
    }

    @PutMapping("/update")
    public Result<Object> deleteGoods(ItemDO goods) throws BusinessException {
        if (goods.getId() == null) {
            throw new BusinessException(500, "活动id不能为空");
        }
        boolean res = itemService.updateGoods(goods);
        return res ? Result.success("删除成功", null) : Result.fail("删除失败", null);
    }


    @GetMapping("/list")
    @Access
    public Result<Page<ItemVO>> deleteGoods(ItemDO goods, PageRequest pageRequest) throws BusinessException {
        Page<ItemVO> page = itemService.goodsList(goods, pageRequest);
        return Result.success(page);
    }


}
