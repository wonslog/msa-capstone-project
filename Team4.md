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
```

### 서비스 설계 
-SAGA Pattern : SAGA Pattern 은 MSA 환경에서 분산 트랜잭션으로 데이터의 영속성을 유지하는 디자인 패턴으로, 트랜잭션의 주체가 DBMS가 아닌 Application에 있습니다.

-서비스 시나리오
[주문-결제-배달]
1. 고객이 메뉴를 선택하여 주문한다
2. 고객이 결제한다
3. 결제가되면 주문 요청이 들어간다.
4. 주문이 요청되면 배달이 시작된다.
5. 배달이 시작되면 주문상태가 변경된다.

[주문취소]
1. 고객이 주문을 취소 할 수 있다.
2. 주문이 취소되면 결제가 취소된다.
3. 결제가 취소되면 배달이 취소된다.
4. 배달이 취소되면 주문상태가 변경된다.

[주문조회]
1. 고객은 주문상태를 중간중간 조회한다.

[특징]
1. 주문, 결제, 배달 서비스는 분리된 서비스로, 하나의 시스템이 과중되더라도 나머지 시스템은 기능할 수 있어야 한다. 
2. 결제처리를 확인 한 후에 주문 요청 처리가 되도록 한다. 
3. 주문상태 확인은 주문, 결제, 배달 서비스 작동 유무와 무관하게 항상 확인할 수 있어야 한다. 

