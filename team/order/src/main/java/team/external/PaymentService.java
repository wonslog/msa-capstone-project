package team.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

//@FeignClient(name="payment", url="http://localhost:8083")
@FeignClient(name="payment", url="http://localhost:8083", fallback = PaymentServiceImpl.class)
public interface PaymentService {
    @RequestMapping(method= RequestMethod.POST, path="/payments")
    public void requestPayment(@RequestBody Payment payment);

}

