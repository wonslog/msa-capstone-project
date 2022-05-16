package team.domain;

import team.infra.AbstractEvent;


public class DeliveryCanceled extends AbstractEvent {

    private Long id;
    private Long orderId;
    private String orderStatus;
    private String customerAddr;
    private String customerTel;
    private String customerId;
    private Long deliveryId;

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
    public String getorderStatus() {
        return orderStatus;
    }

    public void setorderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
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
    public String getcustomerId() {
        return customerId;
    }

    public void setcustomerId(String customerId) {
        this.customerId = customerId;
    }
    public Long getdeliveryId() {
        return deliveryId;
    }

    public void setdeliveryId(Long deliveryId) {
        this.deliveryId = deliveryId;
    }
}
