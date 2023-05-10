package com.mu.activity.service.provider;

import com.mu.activity.dao.ActivityDOMapper;
import com.mu.activity.dao.StockLogDOMapper;
import com.mu.activity.entity.ActivityDO;
import com.mu.activity.entity.StockLogDO;
import com.mu.activity.utils.SpringContextUtils;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 沐
 * Date: 2023-03-12 15:28
 * version: 1.0
 */
public class TransactionListenerImpl implements TransactionListener {

    //存储对应事务的状态信息 key：事务ID value：当前事务的状态
    private ConcurrentHashMap<String, Integer> localTrans = new ConcurrentHashMap<>();


    private final ActivityDOMapper activityDOMapper = SpringContextUtils.getBean("activityDOMapper", ActivityDOMapper.class);
    private final StockLogDOMapper logDOMapper = SpringContextUtils.getBean("stockLogDOMapper", StockLogDOMapper.class);

    /**
     * 每次推送消息会执行executeLocalTransaction方法，首先会发送半消息，到这里的时候是执行具体本地业务，
     *      * 执行成功后手动返回RocketMQLocalTransactionState.COMMIT状态，
     *      * 这里是保证本地事务执行成功，如果本地事务执行失败则可以返回ROLLBACK进行消息回滚。 此时消息只是被保存到broker，
     *      并没有发送到topic中，broker会根据本地返回的状态来决定消息的处理方式。
     * @param message
     * @param o
     * @return
     */
    @Override
    public LocalTransactionState executeLocalTransaction(Message message, Object o) {
//        //事务ID
//        String transactionId = msg.getTransactionId();
//
//        //业务执行 处理本地事务 service
//        System.out.println("hello！-Demo-Transaction");
//        try {
//            System.out.println("正在执行本地事务---");
//            Thread.sleep(60000);
//            System.out.println("正在执行本地事务--成功");
//            localTrans.put(transactionId, 1);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            localTrans.put(transactionId, 2);
//            return LocalTransactionState.ROLLBACK_MESSAGE;
//        }
//        return LocalTransactionState.COMMIT_MESSAGE;
        //事务ID
        String transactionId = message.getTransactionId();

        Map<String, Object> args = (Map) o;
        ActivityDO activity = (ActivityDO) args.get("activity");
        String stockLogId = (String) args.get("stockLogId");
        // 订单落库
        int insertCount = activityDOMapper.insertSelective(activity);
        // 订单创建成功
        if (insertCount > 0) {
            StockLogDO stockLogDO = new StockLogDO();
            stockLogDO.setStockLogId(stockLogId);
            stockLogDO.setStatus(2);
            logDOMapper.updateByPrimaryKeySelective(stockLogDO);
            localTrans.put(transactionId, 2);
            return LocalTransactionState.COMMIT_MESSAGE;
        } else {
            StockLogDO stockLogDO = new StockLogDO();
            stockLogDO.setStockLogId(stockLogId);
            stockLogDO.setStatus(3);
            logDOMapper.updateByPrimaryKeySelective(stockLogDO);
            localTrans.put(transactionId, 3);
            // 订单创建失败
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        //事务ID
        String transactionId = msg.getTransactionId();
        //获取对应事务ID的执行状态
        Integer status = localTrans.get(transactionId);

        System.out.println("消息回查----transacstionId: "+ transactionId + "status: " + status );

        switch (status) {
            case 0:
                return LocalTransactionState.UNKNOW;
            case 2:
                return LocalTransactionState.COMMIT_MESSAGE;
            case 3:
                return LocalTransactionState.ROLLBACK_MESSAGE;
        }
        return LocalTransactionState.UNKNOW;
    }
}
