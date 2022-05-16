package team.domain;

import team.domain.DeliveryStarted;
import team.domain.DeliveryCanceled;
import team.DeliveryApplication;
import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name="Delivery_table")
@Data

public class Delivery  {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    
    
    private Long id;
    
    
    private Long orderId;
    
    
    private String orderStatus;
    
    
    private String customerAddr;
    
    
    private String customerTel;
    
    
    private String customerId;
    
    
    private Long deliveryId;

    @PostPersist
    public void onPostPersist(){
        DeliveryStarted deliveryStarted = new DeliveryStarted();
        BeanUtils.copyProperties(this, deliveryStarted);
        deliveryStarted.publishAfterCommit();

        DeliveryCanceled deliveryCanceled = new DeliveryCanceled();
        BeanUtils.copyProperties(this, deliveryCanceled);
        deliveryCanceled.publishAfterCommit();

    }


    public static DeliveryRepository repository(){
        DeliveryRepository deliveryRepository = DeliveryApplication.applicationContext.getBean(DeliveryRepository.class);
        return deliveryRepository;
    }


    public static void cancelDelivery(PaymentCanceled paymentCanceled){

        Delivery delivery = new Delivery();
        /*
        LOGIC GOES HERE
        */
        // repository().save(delivery);


    }
    public static void requestOrder(PaymentApproved paymentApproved){

        Delivery delivery = new Delivery();
        /*
        LOGIC GOES HERE
        */
        // repository().save(delivery);


    }


}
