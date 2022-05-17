package team.domain;

import team.domain.Ordered;
import team.domain.OrderCanceled;
import team.OrderApplication;
import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date; 


@Entity
@Table(name="Order_table")
public class Order  {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    
    
    private Long id;
    
    
    private Long orderId;
    
    
    private Long customerId;
    
    
    private String productName;
    
    
    private Float productPrice;
    
    
    private Float orderTotalPrice;
    
    
    private String orderStatus;
    
    
    private Date orderDate;
    
    
    private String customerAddr;
    
    
    private String customerTel;

    @PostPersist
    public void onPostPersist(){
        Ordered ordered = new Ordered();
        BeanUtils.copyProperties(this, ordered);
        ordered.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        //team.external.Payment payment = new team.external.Payment();
        // mappings goes here
        //OrderApplication.applicationContext.getBean(team.external.PaymentService.class)
        //    .requestPayment(payment);

        /*
        OrderCanceled orderCanceled = new OrderCanceled();
        BeanUtils.copyProperties(this, orderCanceled);
        orderCanceled.publishAfterCommit();
        */
    }

    @PreRemove
    public void onPreRemove(){
        OrderCanceled orderCanceled = new OrderCanceled();
        BeanUtils.copyProperties(this, orderCanceled);
        orderCanceled.publishAfterCommit();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Float getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Float productPrice) {
        this.productPrice = productPrice;
    }

    public Float getOrderTotalPrice() {
        return orderTotalPrice;
    }

    public void setOrderTotalPrice(Float orderTotalPrice) {
        this.orderTotalPrice = orderTotalPrice;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getCustomerAddr() {
        return customerAddr;
    }

    public void setCustomerAddr(String customerAddr) {
        this.customerAddr = customerAddr;
    }

    public String getCustomerTel() {
        return customerTel;
    }

    public void setCustomerTel(String customerTel) {
        this.customerTel = customerTel;
    }



    public static OrderRepository repository(){
        OrderRepository orderRepository = OrderApplication.applicationContext.getBean(OrderRepository.class);
        return orderRepository;
    }




}
