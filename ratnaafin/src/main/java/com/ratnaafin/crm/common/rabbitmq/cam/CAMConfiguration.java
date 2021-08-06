package com.ratnaafin.crm.common.rabbitmq.cam;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CAMConfiguration {

    @Bean
    DirectExchange deadLetterExchange() {
        return new DirectExchange("AgentForCAMFailedRequest");
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange("AgentForCAMRequest");
    }

    @Bean
    Queue dlq() {
        return QueueBuilder.durable("CAMFailedRequestQueue").build();
    }

    @Bean
    Queue queue() {
        return QueueBuilder.durable("CAMRequestQueue").withArgument("x-dead-letter-exchange", "AgentForCAMFailedRequest")
                .withArgument("x-dead-letter-routing-key", "CAMFailed").build();
    }

    @Bean
    Binding DLQbinding() {
        return BindingBuilder.bind(dlq()).to(deadLetterExchange()).with("CAMFailed");
    }

    @Bean
    Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with("CAMRequest");
    }

//    public class ReceiverConfig {
//        @Bean
//        public CAMConsumer camConsumerOne() {
//            return new CAMConsumer();
//        }
//
//        @Bean
//        public CAMConsumer camConsumerTwo() {
//            return new CAMConsumer();
//        }
//    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public CustomExchange camDelayExchange(){
        Map<String,Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange("AgentForDelayCAMRequest","x-delayed-message",true,false,args);
    }

    @Bean
    public Binding camDelayBinding(){
        return BindingBuilder.bind(queue()).to(camDelayExchange()).with("CAMDelayRequest").noargs();
    }
}
