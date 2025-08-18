package com.yy.framework.config.rabbitMq;
import org.springframework.stereotype.Service;

@Service
public class MessageStorageService {

    // 模拟数据库存储
    public void saveToDatabase(String queueName, String messageContent) {
        System.out.println("存储消息到数据库:");
        System.out.println("  队列: " + queueName);
        System.out.println("  内容: " + messageContent);
    }
}
