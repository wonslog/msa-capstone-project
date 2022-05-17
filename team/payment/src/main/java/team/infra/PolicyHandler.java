package team.infra;

import javax.naming.NameParser;

import javax.naming.NameParser;
import java.util.List;
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
    @Autowired PaymentRepository paymentRepository;
    
    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderCanceled_CancelPayment(@Payload OrderCanceled orderCanceled){

        if(!orderCanceled.validate()) return;

        if(orderCanceled.validate()){
            List<Payment> paymentList = paymentRepository.findByOrderId(orderCanceled.getOrderId());
            if ((paymentList != null) && !paymentList.isEmpty()){
                paymentRepository.deleteAll(paymentList);
            }
        }
    }

}


