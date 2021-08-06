package com.ratnaafin.crm.common.rabbitmq.Equifax;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EquifaxRabbitMQConfig {
    public static final String URL_SHORTENER_AGENT = "URLShortenerAgent";
    public static final String URL_SHORTENER_QUEUE = "URLShortener_Q";
    public static final String URL_SHORTENER_KEY = "URLShortener_rk";

    public static final String EQFX_LINK_SEND_AGENT = "EquifaxLinkSendAgent";
    public static final String EQFX_LINK_SEND_Q = "EquifaxLinkSend_Q";
    public static final String EQFX_LINK_SEND_KEY = "EquifaxLinkSend_rk";

    public static final String EQFX_DATA_SET_AGENT = "EquifaxDataSetAgent";
    public static final String EQFX_DATA_SET_Q = "EquifaxDataSet_Q";
    public static final String EQFX_DATA_SET_KEY = "EquifaxDataSet_rk";


    /**=================================Exchanges||Agents
    /**===============================================================**/
    @Bean
    DirectExchange urlShortenerAgent() { return new DirectExchange(URL_SHORTENER_AGENT); }
    @Bean
    DirectExchange equifaxLinkSendAgent() {
        return new DirectExchange(EQFX_LINK_SEND_AGENT);
    }
    @Bean
    DirectExchange equifaxDataSetAgent() {
        return new DirectExchange(EQFX_DATA_SET_AGENT);
    }

    /**=================================Queues
    /**===============================================================**/
    @Bean
    Queue urlShortener_Q() {
        return QueueBuilder.durable(URL_SHORTENER_QUEUE).build();
    }
   /* @Bean
    Queue urlShortener_Q() {
        return QueueBuilder.durable(URL_SHORTENER_QUEUE)
                .withArgument("x-dead-letter-exchange",URL_SHORTENER_FAILED_AGENT)
                .withArgument("x-dead-letter-routing-key",URL_SHORTENER_FAILED_KEY).build();
    }*/
    @Bean
    Queue equifaxLinkSend_Q() {
        return QueueBuilder.durable(EQFX_LINK_SEND_Q).build();
    }
    @Bean
    Queue equifaxDataSet_Q() {
        return QueueBuilder.durable(EQFX_DATA_SET_Q).build();
    }

    /**=================================bindings**/
    @Bean
    Binding urlShortener_QBinding() {
        return BindingBuilder.bind(urlShortener_Q()).to(urlShortenerAgent()).with(URL_SHORTENER_KEY);
    }
    @Bean
    Binding equifaxLinkSend_QBinding() {
        return BindingBuilder.bind(equifaxLinkSend_Q()).to(equifaxLinkSendAgent()).with(EQFX_LINK_SEND_KEY);
    }
    @Bean
    Binding equifaxDataSet_QBinding() {
        return BindingBuilder.bind(equifaxDataSet_Q()).to(equifaxDataSetAgent()).with(EQFX_DATA_SET_KEY);
    }
    /*@Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }*/

    /*@Bean
    public CustomExchange equifaxDelayExchange(){
        Map<String,Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(DELAY_EXCHANGE,"x-delayed-message",true,false,args);
    }
    @Bean
    public Binding equifaxDelayBinding(){
        return BindingBuilder.bind(equifaxLinkSend_Q()).to(equifaxDelayExchange()).with(ROUTING_KEY3).noargs();
    }*/
}
