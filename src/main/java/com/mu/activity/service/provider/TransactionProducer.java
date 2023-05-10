package com.mu.activity.service.provider;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author 沐
 * Date: 2023-03-12 15:16
 * version: 1.0
 */
@Component
@DependsOn("springContextUtils")
public class TransactionProducer {
    private TransactionMQProducer producer;

    @PostConstruct
    private void init() throws MQClientException {
        this.producer = new TransactionMQProducer("pg");
        // FIXME: 2023/3/12 namesrv应该写在配置文件中
        producer.setNamesrvAddr("101.42.13.83:9876");
        producer.setRetryTimesWhenSendAsyncFailed(3);
        producer.setTransactionListener(new TransactionListenerImpl());
        start();
    }
    private void start() throws MQClientException {
        this.producer.start();
    }

    public TransactionSendResult sendMessage(Message message, Object args) throws MQClientException {
        return producer.sendMessageInTransaction(message, args);
    }
}
