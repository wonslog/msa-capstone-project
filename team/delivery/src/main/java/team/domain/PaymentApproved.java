package team.domain;

import team.domain.*;
import team.infra.AbstractEvent;
import lombok.Data;
import java.util.Date;
@Data
public class PaymentApproved extends AbstractEvent {

    private Long id;
    private Long orderId;
    private String payStatus;
    private Double payAmount;

}

