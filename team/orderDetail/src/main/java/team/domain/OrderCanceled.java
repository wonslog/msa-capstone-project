package team.domain;

import team.infra.AbstractEvent;

import java.util.Date; 

public class OrderCanceled extends AbstractEvent {

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

    public Long getid() {
        return id;
    }

    public void setid(Long id) {
        this.id = id;
    }
    public Long getorderId() {
        return orderId;
    }

    public void setorderId(Long orderId) {
        this.orderId = orderId;
    }
    public Long getcustomerId() {
        return customerId;
    }

    public void setcustomerId(Long customerId) {
        this.customerId = customerId;
    }
    public String getproductName() {
        return productName;
    }

    public void setproductName(String productName) {
        this.productName = productName;
    }
    public Float getproductPrice() {
        return productPrice;
    }

    public void setproductPrice(Float productPrice) {
        this.productPrice = productPrice;
    }
    public Float getorderTotalPrice() {
        return orderTotalPrice;
    }

    public void setorderTotalPrice(Float orderTotalPrice) {
        this.orderTotalPrice = orderTotalPrice;
    }
    public String getorderStatus() {
        return orderStatus;
    }

    public void setorderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
    public Date getorderDate() {
        return orderDate;
    }

    public void setorderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
    public String getcustomerAddr() {
        return customerAddr;
    }

    public void setcustomerAddr(String customerAddr) {
        this.customerAddr = customerAddr;
    }
    public String getcustomerTel() {
        return customerTel;
    }

    public void setcustomerTel(String customerTel) {
        this.customerTel = customerTel;
    }
}
