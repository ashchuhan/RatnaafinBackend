package com.ratnaafin.crm.common.rabbitmq.cam;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class CAMFailedConsumer {
    @Autowired
    private AmqpTemplate amqpTemplate;

    @RabbitListener(queues = "CAMFailedRequestQueue")
    public void camFailedRequestQueue(String message){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("Message sent time:"+sdf.format(new Date()));
        amqpTemplate.convertAndSend("AgentForDelayCAMRequest", "CAMDelayRequest", message, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setHeader("x-delay",10800000);
                return message;
            }
        });
    }
}
