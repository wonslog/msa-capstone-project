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
    @Autowired DeliveryRepository deliveryRepository;
    
    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaymentCanceled_CancelDelivery(@Payload PaymentCanceled paymentCanceled){

        if(!paymentCanceled.validate()) return;
        PaymentCanceled event = paymentCanceled;
        System.out.println("\n\n##### listener CancelDelivery : " + paymentCanceled.toJson() + "\n\n");


        

        // Sample Logic //
        Delivery.cancelDelivery(event);
        

        

    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaymentApproved_RequestOrder(@Payload PaymentApproved paymentApproved){

        if(!paymentApproved.validate()) return;
        PaymentApproved event = paymentApproved;
        System.out.println("\n\n##### listener RequestOrder : " + paymentApproved.toJson() + "\n\n");


        

        // Sample Logic //
        Delivery.requestOrder(event);
        

        

    }


}


