# 4팀 배달의 민족 - MAS Capston Project

- 분석설계
- SAGA Pattern
- CQRS Pattern
- Correlation / Compensation(Unique Key)
- Request / Response (Feign Client / Sync.Async)
- Gateway
- Deploy / Pipeline
- Circuit Breaker
- Autoscale(HPA)
- Self-Healing(Liveness Probe)
- Zero-Downtime Deploy(Readiness Probe)
- Config Map / Persistence Volume
- Polyglot

---

### 분석설계
- 기능 구현에 집중하기 위해 모델 최소화
- 주문 상세보기를 통해 CQRS 구현

![20220516_224327](https://user-images.githubusercontent.com/25494054/168607817-35bc6c76-4763-4f82-8b68-cddafd15d713.png)

기능 검증 - 주문 요청
![20220516_224327_LI](https://user-images.githubusercontent.com/25494054/168608474-a89cd66c-bb35-47ff-acfa-a038dfb2aa45.jpg)
- 고객이 주문한다.
- 고객이 결제한다.
- 결제가 승인되면 상점으로 주문 요청된다.
- 배달 시작된다.
- 주문 상태가 변경된다.

기능 검증 - 주문 취소
![20220516_224327_LI (2)](https://user-images.githubusercontent.com/25494054/168608490-9e503939-9bd0-49cf-81b8-f5690bffe006.jpg)
- 고객이 주문취소한다.
- 결제 취소된다.
- 상점으로 주문 취소 요청된다.
- 배달 취소된다.
- 주문 상태가 변경된다.

특징
- 주문, 결제, 배달 서비스는 분리된 서비스로, 하나의 시스템이 과중되더라도 나머지 시스템은 기능할 수 있어야 한다. 
- 결제처리를 확인 한 후에 주문 요청 처리가 되도록 한다. 
- 주문상태 확인은 주문, 결제, 배달 서비스 작동 유무와 무관하게 항상 확인할 수 있어야 한다. 

### 실행
```
cd order
mvn spring-boot:run
```

```
cd payment
mvn spring-boot:run
```

```
cd delivery
mvn spring-boot:run
```

```
cd orderDetail
mvn spring-boot:run
```

order : 8082
payment : 8083
delivery : 8081
orderDetail : 8084

```
http localhost:8082/orders
http localhost:8082/orders orderId=1 productName="TV"
http localhost:8082/orders/1
```

### Correlation / Compensation(Unique Key)
마이크로 서비스간의 통신에서 이벤트 메세지를 Pub/Sub 하는 방법을 통해 Compensation(보상) and Correlation(상호 연관)을 테스트
Order 서비스에서 주문취소 이벤트를 발행하였을때 Payment 서비스에서 주문취소 이벤트를 수신하여 작업 후 결제정보를 삭제하면서 결제 취소 이벤트 발행

![20220516_224327_LI (3)](https://user-images.githubusercontent.com/25494054/168731668-4001f6fe-ef5d-4b4d-ba60-fb6c665d77a8.jpg)

주문취소 구현

OrderCanceled.java(주문취소 이벤트 클래스 생성)

![20220517_132659](https://user-images.githubusercontent.com/25494054/168728834-b8981f18-dbe8-450c-a016-54d6506ef289.png)

Order.java(주문취소 시 orderCanceled 이벤트 발행)

![20220517_132758](https://user-images.githubusercontent.com/25494054/168728936-2590a7ca-9fdd-4661-be83-56e2b533bf56.png)

Payment 서비스에서 주문취소 이벤트를 받아 결제 정보를 삭제하는 로직 작성
주문 ID를 통해 결제 정보를 찾는데 그 때 쓰는 Unique Key를 상호연관 ID라고 함(findById)

![20220517_133258](https://user-images.githubusercontent.com/25494054/168729559-46b8f484-b17a-4ad7-85d4-68d17ca943b3.png)

findById 실행을 위한 인터페이스 작성

![20220517_133528](https://user-images.githubusercontent.com/25494054/168729842-c557251b-0870-486e-a21a-824655ae064b.png)

Payment.java(결제 취소 이벤트 발행)

![20220517_135147](https://user-images.githubusercontent.com/25494054/168731738-181451c3-918d-49d9-8ccb-4ecf67a6e70e.png)


검증

카프카 콘솔 접속
docker ps
docker exec -it 829780fac0ce /bin/bash
kafka-console-consumer --bootstrap-server localhost:29092 --topic team --from-beginning
* 포트 찾는 방법 : kafka - docker-compose.xml - kafka ports
* topic 명 : team - 해당 서비스 - src - main - resource - application.yml - bindings destination

주문 없음
http localhost:8082/orders

![20220517_161801](https://user-images.githubusercontent.com/25494054/168753220-ba59b169-6c64-4722-b398-d4bbf96508cb.png)

주문 발생
http localhost:8082/orders orderId="30" productName="APPLE"

![20220517_161845](https://user-images.githubusercontent.com/25494054/168753330-d6f40837-8714-4c3d-98ed-0e30855359bb.png)

결제 정보 생성 확인
http localhost:8083/payments

![20220517_161937](https://user-images.githubusercontent.com/25494054/168753374-9effb1e4-b7ca-4382-acdf-aeb19c5e4e93.png)

이벤트 확인

![20220517_162028](https://user-images.githubusercontent.com/25494054/168753474-7422345e-de57-460a-9705-d05825c5160b.png)

주문 취소
http delete localhost:8082/orders/13

![20220517_162113](https://user-images.githubusercontent.com/25494054/168753516-4e23343d-adf6-47f0-a434-5afb92d26688.png)

이벤트 확인

![20220517_162158](https://user-images.githubusercontent.com/25494054/168753564-a0137f20-7b2e-4071-95be-8b96611303c8.png)

결제 정보 삭제 확인
http localhost:8083/payments

![20220517_162237](https://user-images.githubusercontent.com/25494054/168753603-a3579ccb-7546-4914-86b3-86cf85490e36.png)

### REQ/RES방식의 연동 - Feign Client
주문요청은 결제 완료 후 처리되도록 동기식 호출한다. FeignClient를 사용하여 외부 시스템을 annotation 으로 호출한다.

- feignClient dependency 설정 : pom.xml
```
    <!-- feign client -->
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-openfeign</artifactId>
		</dependency>
```
- payment 서비스 annotation 설정 : order > external > PaymentService.java
```
@FeignClient(name="payment", url="http://localhost:8083")
public interface PaymentService {
    @RequestMapping(method= RequestMethod.POST, path="/payments")
    public void requestPayment(@RequestBody Payment payment);
주문취소 구현

}
```
- order 서비스에서 동기 호출하는 부분과 Application.java 는 msaez 에서 req/res 방식으로 모델링하면 자동으로 소스 exporting 된다.
![image](https://user-images.githubusercontent.com/29937411/168734691-d7f7555c-a0f8-4ba4-9fcc-f20b3d76bcb1.png)

- payment 서비스, order 서비스 모두 run 상태일 때 주문이 정상적으로 처리된다.
![image](https://user-images.githubusercontent.com/29937411/168734842-7b887a49-7cfb-4d88-a186-8bd0f3f2ff5d.png)

### Gateway
게이트웨이를 사용하여 모든 API 서버들의 엔드포인트 단일화

Gateway 설정 파일 수정

![20220517_162835](https://user-images.githubusercontent.com/25494054/168754278-cafa6a17-c025-46b5-96d0-0b2aac0596de.png)

gateway 서비스 실행
```
cd gateway
mvn spring-boot:run
```
order 및 payment 서비스 실행
```
cd order
mvn spring-boot:run
```
```
cd payment
mvn spring-boot:run
```

기존 8082 포트로 주문 생성

![20220517_163200](https://user-images.githubusercontent.com/25494054/168756842-890eccfa-e2b4-49c6-a6ce-8a7a45da0c25.png)

단일진입점인 8088 포트로 주문 생성

![20220517_163258](https://user-images.githubusercontent.com/25494054/168756876-ba5d1b8c-4ecc-4fad-8fac-67b87c85aa94.png)

기존 8083 포트로 결제 정보 확인

![20220517_163337](https://user-images.githubusercontent.com/25494054/168756940-88e4c12b-0c88-4b22-b5f5-1e8b941508c2.png)

단일진입점임 8088 포트로 결제 정보 확인

![20220517_163356](https://user-images.githubusercontent.com/25494054/168756968-5a32e303-e54a-4e91-a5d8-2211cc65a9c3.png)
