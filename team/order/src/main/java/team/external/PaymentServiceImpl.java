package team.external;

import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService{

    /**
     * 결제 fallback
     */
    public void requestPayment(Payment payment) {
        System.out.println("@@@@@@@ 결제가 지연중 입니다. @@@@@@@@@@@@");
        System.out.println("@@@@@@@ 결제가 지연중 입니다. @@@@@@@@@@@@");
        System.out.println("@@@@@@@ 결제가 지연중 입니다. @@@@@@@@@@@@");
    }

}