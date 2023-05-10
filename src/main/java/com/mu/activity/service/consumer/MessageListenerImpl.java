package com.mu.activity.service.consumer;

import com.mu.activity.dao.ItemDOMapper;
import com.mu.activity.dao.ItemStockDOMapper;
import com.mu.activity.dao.ActivityDOMapper;
import com.mu.activity.dao.StockLogDOMapper;
import com.mu.activity.entity.ActivityDO;
import com.mu.activity.entity.StockLogDO;
import com.mu.activity.utils.JsonUtils;
import com.mu.activity.utils.RedisUtils;
import com.mu.activity.utils.SpringContextUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 沐
 * Date: 2023-03-14 18:57
 * version: 1.0
 */
public class MessageListenerImpl implements MessageListenerConcurrently {

    private final static String ORDER_PREFIX = "activity_";
    private final ItemStockDOMapper itemStockDOMapper = SpringContextUtils.getBean("itemStockDOMapper", ItemStockDOMapper.class);
    private final ActivityDOMapper activityDOMapper = SpringContextUtils.getBean("activityDOMapper", ActivityDOMapper.class);
    private final StockLogDOMapper stockLogDOMapper = SpringContextUtils.getBean("stockLogDOMapper", StockLogDOMapper.class);
    private final ItemDOMapper itemDOMapper = SpringContextUtils.getBean("itemDOMapper", ItemDOMapper.class);

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        try {
            MessageExt messageExt = list.get(0);

            String body = new String(messageExt.getBody());
            Map<String, Object> msg = JsonUtils.jsonToObject(body, Map.class);
            Long itemId = (Long) msg.get("itemId");
            Integer amount = (Integer) msg.get("amount");
            String stockLogId = (String) msg.get("stockLogId");
            String activityId = (String) msg.get("activityId");
            Boolean b = RedisUtils.hasKey(ORDER_PREFIX + activityId);
            if(b){
                // 之前消费过
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
            int updateCount = itemStockDOMapper.decreaseStock(itemId, amount);
            if(updateCount > 0){
                // 修改订单状态
                ActivityDO activityDO = new ActivityDO();
                activityDO.setId(activityId);
                activityDO.setStatus(2);
                activityDOMapper.updateByPrimaryKeySelective(activityDO);
                itemDOMapper.increaseSales(itemId,amount);
            }else{
                // 扣减库存失败
                ActivityDO activityDO = new ActivityDO();
                activityDO.setId(activityId);
                activityDO.setStatus(3);
                // 更新日志状态
                StockLogDO stockLogDO = new StockLogDO();
                stockLogDO.setStockLogId(stockLogId);
                stockLogDO.setStatus(3);
                stockLogDOMapper.updateByPrimaryKeySelective(stockLogDO);
                activityDOMapper.updateByPrimaryKeySelective(activityDO);
            }
            // TODO: 2023/1/14 这里用应该用数据库做幂等更好一下
            RedisUtils.set(ORDER_PREFIX + activityId, " ", 15, TimeUnit.MINUTES);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            // 这里可以统计失败次数，超过 n 次错误以后将消息记录在数据库中，然后使用定时任务处理或者通知人工处理
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }
}

