package team.external;

public class Payment {

    private Long id;
    private Long orderId;
    private String payStatus;
    private Double payAmount;

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
    public String getPayStatus() {
        return payStatus;
    }
    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }
    public Double getPayAmount() {
        return payAmount;
    }
    public void setPayAmount(Double payAmount) {
        this.payAmount = payAmount;
    }

}
