package com.yy.framework.config.rabbitMq;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitProducerService {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    /**
     * 通用消息发送方法
     * @param exchange     交换机名称
     * @param routingKey   路由键（Direct/Topic必需）
     * @param message      消息内容
     * @param exchangeType 交换机类型（direct/topic/fanout/headers）
     * @param headers      头部参数（headers类型必需）
     */
    public void sendMessage(String exchange,
                            String routingKey,
                            Object message,
                            String exchangeType,
                            java.util.Map<String, Object> headers) {

        Message amqpMsg = convertToAmqpMessage(message);

        switch (exchangeType.toLowerCase()) {
            case "direct":
            case "topic":
                rabbitTemplate.convertAndSend(exchange, routingKey, amqpMsg);
                System.out.println("推送消息成功");
                break;

            case "fanout":
                rabbitTemplate.convertAndSend(exchange, "", amqpMsg); // 路由键为空
                break;

            case "headers":
                MessageProperties props = new MessageProperties();
                props.setHeaders(headers);
                rabbitTemplate.convertAndSend(exchange, "",
                        MessageBuilder.withBody(amqpMsg.getBody())
                                .andProperties(props)
                                .build());
                break;

            default:
                throw new IllegalArgumentException("不支持的交换机类型: " + exchangeType);
        }
    }

    private Message convertToAmqpMessage(Object obj) {
        // 实际项目中使用JSON序列化
        byte[] bytes = obj.toString().getBytes();
        return MessageBuilder.withBody(bytes)
                .setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
                .build();
    }
}
