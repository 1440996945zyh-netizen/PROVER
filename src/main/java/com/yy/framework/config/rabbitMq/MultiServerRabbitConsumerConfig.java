package com.yy.framework.config.rabbitMq;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableConfigurationProperties(RabbitConsumerServersConfig.class)
public class MultiServerRabbitConsumerConfig {
    private static final Logger log = LoggerFactory.getLogger(MultiServerRabbitConsumerConfig.class);

    private final RabbitConsumerServersConfig consumerServersConfig;
    private final MessageStorageService storageService;

    public MultiServerRabbitConsumerConfig(RabbitConsumerServersConfig consumerServersConfig,
                                           MessageStorageService storageService) {
        this.consumerServersConfig = consumerServersConfig;
        this.storageService = storageService;
    }

    // 为每个服务器创建连接工厂
    @Bean
    public Map<String, ConnectionFactory> consumerConnectionFactories() {
        Map<String, ConnectionFactory> factories = new HashMap<>();
        if (consumerServersConfig.getConsumers() == null) return factories;

        for (RabbitConsumerServersConfig.ConsumerServerConfig server : consumerServersConfig.getConsumers()) {
            CachingConnectionFactory factory = new CachingConnectionFactory();
            factory.setHost(server.getHost());
            factory.setPort(server.getPort());
            factory.setVirtualHost(server.getVirtualHost());
            factory.setUsername(server.getUsername());
            factory.setPassword(server.getPassword());

            // 添加连接测试
            try {
                factory.createConnection().close();
                System.out.println("成功连接到RabbitMQ服务器: " + server.getServerId());
            } catch (Exception e) {
                System.err.println("无法连接到RabbitMQ服务器: " + server.getServerId());
                log.warn("无法连接到RabbitMQ服务器: " ,e);
            }

            factories.put(server.getServerId(), factory);
        }
        return factories;
    }

    // 为每个服务器创建监听容器工厂
    @Bean
    public Map<String, SimpleRabbitListenerContainerFactory> consumerContainerFactories(
            Map<String, ConnectionFactory> consumerConnectionFactories) {
        Map<String, SimpleRabbitListenerContainerFactory> containerFactories = new HashMap<>();
        if (consumerServersConfig.getConsumers() == null) return containerFactories;

        for (RabbitConsumerServersConfig.ConsumerServerConfig server : consumerServersConfig.getConsumers()) {
            SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
            factory.setConnectionFactory(consumerConnectionFactories.get(server.getServerId()));
            factory.setConcurrentConsumers(server.getListener().getConcurrency());
            factory.setMaxConcurrentConsumers(server.getListener().getMaxConcurrency());
            factory.setAcknowledgeMode(org.springframework.amqp.core.AcknowledgeMode.MANUAL);
            containerFactories.put(server.getServerId(), factory);
        }
        return containerFactories;
    }

    // 动态注册所有启用的队列监听
    @Bean
    public List<SimpleMessageListenerContainer> messageListenerContainers(
            Map<String, SimpleRabbitListenerContainerFactory> consumerContainerFactories) {
        List<SimpleMessageListenerContainer> containers = new ArrayList<>();
        if (consumerServersConfig.getConsumers() == null) return containers;

        for (RabbitConsumerServersConfig.ConsumerServerConfig server : consumerServersConfig.getConsumers()) {
            // 获取当前服务器启用的队列
            List<String> enabledQueues = server.getQueues().stream()
                    .filter(RabbitConsumerServersConfig.ConsumerServerConfig.QueueConfig::isEnabled)
                    .map(RabbitConsumerServersConfig.ConsumerServerConfig.QueueConfig::getName)
                    .collect(Collectors.toList());

            SimpleRabbitListenerContainerFactory containerFactory = consumerContainerFactories.get(server.getServerId());
            for (String queue : enabledQueues) {
                // 创建并配置容器
                SimpleMessageListenerContainer container = containerFactory.createListenerContainer();
                container.setQueueNames(queue);

                // 使用 ChannelAwareMessageListener
                container.setMessageListener(new CustomMessageListener(storageService));

                container.start();
                containers.add(container);
                System.out.println("已启动监听容器，监听队列: " + queue);
            }
        }
        return containers;
    }

    // 自定义消息监听器（实现 ChannelAwareMessageListener）
    public static class CustomMessageListener implements ChannelAwareMessageListener {
        private final MessageStorageService storageService;

        public CustomMessageListener(MessageStorageService storageService) {
            this.storageService = storageService;
        }

        @Override
        public void onMessage(Message message, Channel channel) throws Exception {
            long deliveryTag = message.getMessageProperties().getDeliveryTag();
            String queueName = message.getMessageProperties().getConsumerQueue();
            String content = new String(message.getBody(), StandardCharsets.UTF_8);

            try {
                System.out.println("收到消息 [" + queueName + "]: " + content);
                // 业务处理逻辑、存储到数据库
                storageService.saveToDatabase(queueName, content);
                channel.basicAck(deliveryTag, false);
                System.out.println("消息确认成功 [" + queueName + "] deliveryTag: " + deliveryTag);
            } catch (Exception e) {
                System.err.println("消息处理失败 [" + queueName + "]: " + e.getMessage());
                try {
                    if (channel.isOpen()) {
                        channel.basicNack(deliveryTag, false, true);
                        System.out.println("消息已重新入队 [" + queueName + "]");
                    } else {
                        System.err.println("无法执行 nack 操作：channel 已关闭");
                    }
                } catch (IOException ex) {
                    System.err.println("执行 nack 时出错: " + ex.getMessage());
                }

                // 重新抛出异常以便容器处理
                throw e;
            }
        }
    }
}
