package team.domain;

import team.domain.*;
import team.infra.AbstractEvent;
import java.util.Date;
import lombok.Data;

@Data
public class PaymentApproved extends AbstractEvent {

    private Long id;
    private Long orderId;
    private String payStatus;
    private Double payAmount;

    public PaymentApproved(){
        super();
    }
}
