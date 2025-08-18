package com.yy.framework.config.rabbitMq;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@ConfigurationProperties(prefix = "spring.rabbitmq")
@Data
public class RabbitConsumerServersConfig {
    private List<ConsumerServerConfig> consumers;

    @Data
    public static class ConsumerServerConfig {
        private String serverId;
        private String virtualHost;
        private String host;
        private int port;
        private String username;
        private String password;
        private List<ExchangeConfig> exchanges;
        private List<QueueConfig> queues;
        private ListenerConfig listener;

        @Data
        public static class ExchangeConfig {
            private String name;
            private String type; // direct/topic/fanout/headers
        }

        @Data
        public static class QueueConfig {
            private String name;
            private String routingKey;
            private String exchange; // 关联的交换机名称
            private boolean enabled;
        }

        @Data
        public static class ListenerConfig {
            private int concurrency;
            private int maxConcurrency;
            private String acknowledgeMode;
        }
    }
}
