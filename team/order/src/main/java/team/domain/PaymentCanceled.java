package team.domain;

import team.infra.AbstractEvent;

import java.util.Date; 

public class PaymentCanceled extends AbstractEvent {

    private Long id;
    private Long orderId;
    private String payStatus;
    private Double payAmount;
    private Date addDate;
    private Date changeDate;

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
    public String getPaymentStatus() {
        return payStatus;
    }

    public void setPaymentStatus(String payStatus) {
        this.payStatus = payStatus;
    }
    public Double getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(Double payAmount) {
        this.payAmount = payAmount;
    }
    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }
    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }
}
