package com.yy.framework.config.rabbitMq;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitProducerConfig {

    // 生产者连接工厂（绑定yml中的rabbitmq.producer配置）
    @Bean
    @ConfigurationProperties(prefix = "spring.rabbitmq.producer")
    public CachingConnectionFactory producerConnectionFactory() {
        return new CachingConnectionFactory();
    }

    // 生产者模板（使用专属连接工厂）
    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory producerConnectionFactory) {
        RabbitTemplate template = new RabbitTemplate(producerConnectionFactory);
        // 可添加消息转换器等配置
        return template;
    }
}
