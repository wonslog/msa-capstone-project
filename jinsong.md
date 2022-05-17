###서킷브레이커를 사용한 테스트 및 장애차단코드 작성

REQ/RES 방식에서 서킷브레이커를 통하여 장애 전파를 원천 차단
- 주문서비스 확인
```
http localhost:8082/orders orderId=1 productName=TV
```


- 서킷브레이커 설정 : order > application.yml
```
feign:
  hystrix:
    enabled: true

hystrix:
  command:
    # 전역설정
    default:
      execution.isolation.thread.timeoutInMilliseconds: 610
```      

- payment 서비스에서 요청처리 지연 코드 작성
```
    @PostPersist
    public void onPostPersist(){

        PaymentApproved paymentApproved = new PaymentApproved();
        BeanUtils.copyProperties(this, paymentApproved);
        paymentApproved.publishAfterCommit();

        try {
            Thread.currentThread().sleep((long) (1000 + Math.random() * 220));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
```

- 부하 툴을 사용하여 주문을 넣어본다.
```
siege -c2 -t10S  -v --content-type "application/json" 'http://localhost:8082/orders POST {"orderId":4}'
```

- 결과
![image](https://user-images.githubusercontent.com/29937411/168758407-03924bfd-49c8-4c14-8456-0bd204d65b67.png)

- 장애차단 코드 작성
fallback 함수 설정. 
```
@FeignClient(name="payment", url="http://localhost:8083", fallback = PaymentServiceImpl.class)
public interface PaymentService {
    @RequestMapping(method= RequestMethod.POST, path="/payments")
    public void requestPayment(@RequestBody Payment payment);

}
```

paymentServiceImpl.java
```
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
```

- 테스트결과
정상처리
![image](https://user-images.githubusercontent.com/29937411/168758952-b20adf09-5472-4272-8458-8dda8c39d561.png)

결제지연처리 메세지 확인
![image](https://user-images.githubusercontent.com/29937411/168759002-f920e770-5cd4-4ad5-99de-2ca4502ee9e6.png)



