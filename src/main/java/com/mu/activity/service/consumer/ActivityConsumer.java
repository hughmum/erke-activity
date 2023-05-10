package com.mu.activity.service.consumer;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author 沐
 * Date: 2023-03-14 18:55
 * version: 1.0
 */
@Component
@DependsOn("springContextUtils")
public class ActivityConsumer {
    private static String TX_ORDER_TOPIC = "tx_activity";
    private DefaultMQPushConsumer consumer;

    @PostConstruct
    private void init() throws MQClientException {
        this.consumer = new DefaultMQPushConsumer("cg");
        consumer.setNamesrvAddr("101.42.13.83:9876");
        consumer.setConsumeThreadMax(30);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.subscribe(TX_ORDER_TOPIC, "*");
        consumer.setMessageListener(new MessageListenerImpl());
        start();
    }
    private void start() throws MQClientException {
        this.consumer.start();
    }
}
