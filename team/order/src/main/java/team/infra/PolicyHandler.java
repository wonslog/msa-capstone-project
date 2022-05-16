package team.infra;

import javax.naming.NameParser;

import javax.naming.NameParser;

import team.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import team.domain.*;


@Service
public class PolicyHandler{
    @Autowired OrderRepository orderRepository;
    
    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverDeliveryStarted_OrderStatusChange(@Payload DeliveryStarted deliveryStarted){

        if(!deliveryStarted.validate()) return;
        DeliveryStarted event = deliveryStarted;
        System.out.println("\n\n##### listener OrderStatusChange : " + deliveryStarted.toJson() + "\n\n");


        

        // Sample Logic //

        

    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverDeliveryCanceled_OrderStatusChange(@Payload DeliveryCanceled deliveryCanceled){

        if(!deliveryCanceled.validate()) return;
        DeliveryCanceled event = deliveryCanceled;
        System.out.println("\n\n##### listener OrderStatusChange : " + deliveryCanceled.toJson() + "\n\n");


        

        // Sample Logic //

        

    }


}


