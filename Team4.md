# 4팀 배달의 민족 - MSA Capston Project


   |팀|성명|직급|사번|소속||
   |:----:|:------:|:------:|:------|:------|------|
   |4|🎖이원희|대리|202000536|서비스혁신센터|제조방산담당/방산운영1팀|
   ||  조진송|대리|201800069|서비스혁신센터|제조방산담당/방산운영1팀|
   ||  이종성|대리|202102050|서비스혁신센터|제조방산담당/제조운영팀|  

- 분석설계 
- SAGA Pattern - 진송
- CQRS Pattern - 종성
- Correlation / Compensation(Unique Key) - 원희
- Request / Response (Feign Client / Sync.Async) -진송
- Circuit Breaker - 진송
- Gateway - 원희
- Deploy / Pipeline - 종성
- Autoscale(HPA) - 원희
- Self-Healing(Liveness Probe) - 종성
- Zero-Downtime Deploy(Readiness Probe) - 종성
- Config Map / Persistence Volume - 원희
- Polyglot - 미구현

---

# 분석설계
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


# CQRS

주문 발생(취소), 결제(취소), 배달(취소) 이벤트 발생 시 주문, 결제 상태값 주문금액(금액확인)을 고객이 조회할 수 있도록 CQRS로 구현하였습니다.
- 비동기식으로 처리되어 이벤트 기반의 Kafka를 통해 처리되어 별도 Table에 관리한다.
- order, payment, delivery Aggregate의 '마이페이지' 형식과 같이 통합 조회가 가능하다.
- Modeling

![orderDetail_modeling](https://user-images.githubusercontent.com/11211944/168709433-11239a86-4a5b-4540-a3f1-e5a7a7bbb3f1.PNG)

- ViewHandler를 통해 구현(ordered 이벤트 발생 시)
![create](https://user-images.githubusercontent.com/11211944/168712299-b1cdbb33-561f-41d2-b31f-c140879b0fce.PNG)

- OrderCanceled 이벤트 발생 시, orderStatus 상태 값 변경
![update1](https://user-images.githubusercontent.com/11211944/168711242-b665664d-afec-474d-808b-2d6dc9cd5513.PNG)

- PaymentApproved 이벤트 발생 시, paymentStatus 상태 값 변경
![update2](https://user-images.githubusercontent.com/11211944/168711247-04cd6218-0a4f-48b3-8503-334d82e5b4e6.PNG)

- PaymentCanceled 이벤트 발생 시, paymentStatus 상태 값 변경
![update3](https://user-images.githubusercontent.com/11211944/168711252-0028b8d4-7a4f-4d31-bec9-4dd93ea2761c.PNG)

- DeliveryStarted 이벤트 발생 시, orderStatus 상태 값 변경
![update4](https://user-images.githubusercontent.com/11211944/168711258-96ed0aab-291b-4671-bb5a-627f8aec144c.PNG)

- DeliveryCanceled 이벤트 발생 시, orderStatus 상태 값 변경
![update5](https://user-images.githubusercontent.com/11211944/168711224-288a0300-9344-4215-ae2f-70119cf47d0d.PNG)


# Correlation / Compensation(Unique Key)
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


# REQ/RES방식의 연동 - Feign Client
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

# 서킷브레이커를 사용한 테스트 및 장애차단코드 작성

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


# Gateway
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


# CI/CD
- AWS 설정
```
aws configure

AWS Access Key ID: [AWS 액세스 키]
AWS Secret Access Key: [시크릿 키]
Default region name : [본인의 리젼]
Default output format : json

클러스터 이름 확인
eksctl get clusters
클러스터에 접속하기 위한 설정 다운로드
aws eks --region ca-central-1 update-kubeconfig --name [Cluster Name]
접속이 정상적으로 되었다면
kubectl get nodes

ECR docker 명령을 로그인 시키기 위한 설정
password 확인
aws --region ca-central-1 ecr get-login-password
docker login --username AWS -p 위에서나온긴패스워드 [AWS유저아이디-숫자로만된].dkr.ecr.ca-central-1.amazonaws.com

```

- 도커 컨테이너, 이미지 확인
```
도커 컨테이너 확인
docker container ls
도커 이미지 확인
docker image ls
```

- 도커 이미지를 빌드하고, push 한다.
```
docker login (docker hub 계정생성이 되어 있어야 함.)
docker build -t ljsjjong/gateway:v1 .
docker build -t ljsjjong/order:v1 .    
docker build -t ljsjjong/payment:v1 .
docker build -t ljsjjong/delivery:v1 .

docker images
docker push ljsjjong/gateway:v1
docker push ljsjjong/order:v1
docker push ljsjjong/payment:v1
docker push ljsjjong/delivery:v1

deployment.yml 파일에는 도커 이미지 부분이 변경되어 있어야 한다.
kubectl apply -f kubernetes/deployment.yml
kubectl apply -f kubernetes/service.yml
```

- 클러스터 확인
![kubectl_get_svc](https://user-images.githubusercontent.com/11211944/168744492-b8462bc0-28e1-484d-ac83-5ace0f6572ef.PNG)

- 위 이미지에서 Gateway External IP로 접근
![gateway_call](https://user-images.githubusercontent.com/11211944/168744875-0da44705-eb65-4cc8-80ab-495611946803.PNG)

- 주문정보 확인
![aws_orders_get](https://user-images.githubusercontent.com/11211944/168746369-28adfe21-abc3-4eb2-ac33-c96b6e4e02a0.PNG)


# Autoscale(HPA)

kubectl get svc 를 통하여 order 서비스 확인

![20220517_232452](https://user-images.githubusercontent.com/25494054/168834835-bac54d7a-e0ac-40b7-b164-504c51155298.png)

kubectl get pod 를 통하여 order 서비스 및 siege 상태 running 확인

![20220517_232844](https://user-images.githubusercontent.com/25494054/168835789-3a9e3a93-53b3-4c7d-8b81-a5eca160aa5c.png)

생성된 siege Pod 안쪽에서 정상작동 확인

```
kubectl exec -it siege -- /bin/bash
siege -c1 -t2S -v http://order:8080/orders
```

![20220517_233030](https://user-images.githubusercontent.com/25494054/168836190-61a7a89a-6ce2-4b96-a94c-7ba774673bb1.png)

metric server 설치 확인(리소스 사용량 확인)

![20220517_233117](https://user-images.githubusercontent.com/25494054/168836449-01ddb83a-52d1-41f2-b35d-ee30f75c4e1b.png)

Auto Scaler 설정
cpu가 20%를 넘으면 replicas를 최대 3개까지 확장

```
kubectl autoscale deployment order --cpu-percent=20 --min=1 --max=3
```

kubectl get hpa 명령어로 설정값을 확인

![20220517_233634](https://user-images.githubusercontent.com/25494054/168837683-8355ee57-1553-43d7-9d09-03b89a9c51f4.png)

컨테이너에 적용될 리소스의 양을 정의

team > order > kubernetes 폴더로 이동하여 deployment.yaml 파일을 수정
resources.requests.cpu: "200m"을 추가

request는 컨테이너가 생성될때 요청하는 리소스 양이고, 

limit은 컨테이너가 생성된 후에 실행되다가 리소스가 더 필요한 경우 (CPU가 메모리가 더 필요한 경우) 추가로 더 사용할 수 있는 부분이다.

![20220517_234300](https://user-images.githubusercontent.com/25494054/168839141-b2f02a34-eab6-49d0-b3c6-f68d339fe713.png)

변경된 yml 파일 쿠버네티스에 배포
```
cd team/order/kubernetes
kubectl apply -f deployment.yml
```

![20220517_234439](https://user-images.githubusercontent.com/25494054/168839538-31d7cfaf-7ca5-4b83-bdea-947fb068cea3.png)

배포 완료 후 image 와 resources의 값이 정상적으로 설정되어있는지 확인
```
kubectl get deploy order -o yaml
```

![20220517_234543](https://user-images.githubusercontent.com/25494054/168839807-70b587f7-db4a-4f7c-99cc-06569cec7b36.png)

kubectl get po 실행하여 STATUS가 정상적으로 Running 상태 확인

![20220517_234624](https://user-images.githubusercontent.com/25494054/168839957-00f6c934-248e-4ef7-a123-101279325701.png)

watch kubectl get po 명령을 사용하여 pod 가 생성되는 것을 확인

![20220517_234744](https://user-images.githubusercontent.com/25494054/168840289-fa1cf2a3-d648-4682-acda-b5c4077d3b92.png)

kubectl get hpa 명령어로 CPU 값이 늘어난 것을 확인

![20220517_234825](https://user-images.githubusercontent.com/25494054/168840409-88b422df-3311-4e20-8a72-9c40b50af4f6.png)

seige 명령으로 부하를 주어서 Pod 가 늘어나도록 한다.
```
kubectl exec -it siege -- /bin/bash
siege -c30 -t40S -v http://order:8080/orders
```
seige 부하

![20220517_235351](https://user-images.githubusercontent.com/25494054/168841779-eb122a5b-a636-4dbc-beaf-7d9d9217c8e0.png)

늘어난 CPU 값 확인

![20220517_235345](https://user-images.githubusercontent.com/25494054/168841757-59098dc6-31be-434f-a810-dddc6e362446.png)

늘어난 POD REPLICAS

![20220517_235340](https://user-images.githubusercontent.com/25494054/168841738-e2006eb4-59ec-43e6-9067-7f7154af7f77.png)




# self-healing(livenessProbe 설정)
- Pod의 상태가 비정상인 경우 kubelet을 통해 pod들이 재시작된다.
![pod_selfhealing](https://user-images.githubusercontent.com/11211944/168748704-1c02d205-326a-4c3a-8111-a6389eded4c2.PNG)
```
gitpod /workspace/msa-capstone-project/team/payment (main) $ kubectl get po -w
NAME                        READY   STATUS    RESTARTS   AGE
delivery-55994c79f9-4p6c5   0/1     Running   1          4m43s
delivery-8646fcfbcb-d4lk2   0/1     Running   1          4m43s
gateway-5669c77854-c9x5m    1/1     Running   0          4m43s
order-6f86b56898-fwqth      0/1     Running   1          4m43s
order-b8cbfbc79-bgpsm       0/1     Running   1          4m43s
payment-6557776984-g66xm    0/1     Running   1          4m42s
payment-854c56cc8b-bjjm9    0/1     Running   1          4m42s
delivery-55994c79f9-4p6c5   0/1     Running   2          4m45s
order-6f86b56898-fwqth      0/1     Running   2          4m50s
order-b8cbfbc79-bgpsm       0/1     Running   2          4m50s
delivery-8646fcfbcb-d4lk2   0/1     Running   2          4m51s
payment-6557776984-g66xm    0/1     Running   2          4m51s
payment-854c56cc8b-bjjm9    0/1     Running   2          4m52s
delivery-55994c79f9-4p6c5   0/1     Terminating   2          5m47s
delivery-55994c79f9-zzxdt   0/1     Pending       0          0s
delivery-55994c79f9-zzxdt   0/1     Pending       0          0s
delivery-55994c79f9-zzxdt   0/1     ContainerCreating   0          0s
delivery-8646fcfbcb-d4lk2   0/1     Terminating         2          5m48s
delivery-8646fcfbcb-56r5j   0/1     Pending             0          0s
delivery-8646fcfbcb-56r5j   0/1     Pending             0          0s
delivery-8646fcfbcb-56r5j   0/1     ContainerCreating   0          0s
gateway-5669c77854-c9x5m    1/1     Terminating         0          5m48s
gateway-5669c77854-vhsn2    0/1     Pending             0          0s
gateway-5669c77854-vhsn2    0/1     Pending             0          0s
gateway-5669c77854-vhsn2    0/1     ContainerCreating   0          0s
order-6f86b56898-fwqth      0/1     Terminating         2          5m48s
order-6f86b56898-xg5lw      0/1     Pending             0          0s
order-6f86b56898-xg5lw      0/1     Pending             0          0s
order-6f86b56898-xg5lw      0/1     ContainerCreating   0          0s
order-b8cbfbc79-bgpsm       0/1     Terminating         2          5m48s
order-b8cbfbc79-hb9s8       0/1     Pending             0          0s
order-b8cbfbc79-hb9s8       0/1     Pending             0          0s
order-b8cbfbc79-hb9s8       0/1     ContainerCreating   0          0s
payment-6557776984-g66xm    0/1     Terminating         2          5m47s
payment-6557776984-tn85g    0/1     Pending             0          0s
payment-6557776984-tn85g    0/1     Pending             0          0s
payment-6557776984-tn85g    0/1     ContainerCreating   0          1s
delivery-8646fcfbcb-d4lk2   0/1     Terminating         2          5m49s
payment-854c56cc8b-bjjm9    0/1     Terminating         2          5m48s
payment-854c56cc8b-m2vjp    0/1     Pending             0          0s
payment-854c56cc8b-m2vjp    0/1     Pending             0          0s
delivery-55994c79f9-4p6c5   0/1     Terminating         2          5m49s
payment-854c56cc8b-m2vjp    0/1     ContainerCreating   0          0s
order-6f86b56898-fwqth      0/1     Terminating         2          5m49s
gateway-5669c77854-c9x5m    0/1     Terminating         0          5m49s
delivery-8646fcfbcb-d4lk2   0/1     Terminating         2          5m50s
delivery-8646fcfbcb-d4lk2   0/1     Terminating         2          5m50s
payment-854c56cc8b-bjjm9    0/1     Terminating         2          5m49s
delivery-8646fcfbcb-56r5j   0/1     Running             0          2s
delivery-55994c79f9-zzxdt   0/1     Running             0          3s
payment-6557776984-g66xm    0/1     Terminating         2          5m49s
order-6f86b56898-xg5lw      0/1     Running             0          2s
order-b8cbfbc79-bgpsm       0/1     Terminating         2          5m50s
gateway-5669c77854-vhsn2    1/1     Running             0          2s
order-b8cbfbc79-hb9s8       0/1     Running             0          3s
payment-6557776984-tn85g    0/1     Running             0          3s
payment-854c56cc8b-m2vjp    0/1     Running             0          2s
payment-6557776984-g66xm    0/1     Terminating         2          5m52s
payment-6557776984-g66xm    0/1     Terminating         2          5m52s
payment-854c56cc8b-bjjm9    0/1     Terminating         2          5m52s
payment-854c56cc8b-bjjm9    0/1     Terminating         2          5m52s
gateway-5669c77854-c9x5m    0/1     Terminating         0          6m
gateway-5669c77854-c9x5m    0/1     Terminating         0          6m
delivery-55994c79f9-4p6c5   0/1     Terminating         2          6m
delivery-55994c79f9-4p6c5   0/1     Terminating         2          6m
order-6f86b56898-fwqth      0/1     Terminating         2          6m
order-6f86b56898-fwqth      0/1     Terminating         2          6m
order-b8cbfbc79-bgpsm       0/1     Terminating         2          6m
order-b8cbfbc79-bgpsm       0/1     Terminating         2          6m
```

# Zero-Downtime Deploy(Readiness Probe) 무중단 배포

- 배포되는 상황을 보기 위해 siege pod를 추가한다.

```
kubectl apply -f - <<EOF
apiVersion: v1
kind: Pod
metadata:
  name: siege
spec:
  containers:
  - name: siege
    image: apexacme/siege-nginx
EOF
```
- siege 명령어, 딜레이 명령어
```
kubectl exec -it siege -- /bin/bash
(딜레이X) siege -c1 -t2S -v http://a8936b8dc8de349a8a1d4de076f796e2-1549089319.ca-central-1.elb.amazonaws.com:8080/orders
(딜레이O) siege -c1 -t20S -v http://a8936b8dc8de349a8a1d4de076f796e2-1549089319.ca-central-1.elb.amazonaws.com:8080/orders --delay=1S
```
- readinessProbe 없는 상태로 deployment.yml 파일 저장
![readinessProbe_not](https://user-images.githubusercontent.com/11211944/168954040-9a1474d0-029a-45ca-bf1c-4985c2235cb1.PNG)
```
siege 테스트 딜레이 걸어 놓은 후,
kubectl apply -f deployment.yml
배포 시작
```
- siege 로그를 보면서 정지된 부분을 확인
![apply_deployment_not](https://user-images.githubusercontent.com/11211944/168954035-7d245b6a-f718-441d-8568-69bd81441f27.PNG)
---
- readinessProbe 추가 후, deployment.yml 파일 저장
![readinessProbe_add](https://user-images.githubusercontent.com/11211944/168954036-8bf16218-41fc-46f9-bef6-da3b78aa0133.PNG)
```
siege 테스트 딜레이 걸어 놓은 후,
kubectl apply -f deployment.yml
배포 시작
```
- siege 로그를 보면서 무정지 배포가 되었음을 확인
![apply_deployment_add](https://user-images.githubusercontent.com/11211944/168954032-11496d44-b99a-4b62-9557-c1b88fc09b08.PNG)


# Config Map / Persistence Volume / Persistence Volume Claim

컨테이너를 관리하는 구성정보를 하나로 모아서 KEY VALUE 형태로 관리할 수 있는 API - ConfigMap

ConfigMap 생성

```
kubectl create configmap mysqlinfo --from-literal password=password
```

또는 yaml 파일로 생성

```
apiVersion: v1
kind: ConfigMap
metadata:
  name: mysqlinfo
data:
  password: password
```

ConfigMap 사용하기
환경변수 password 값은 mysqlinfo ConfigMap의 password라는 key의 value를 가져와라

![20220518_132522](https://user-images.githubusercontent.com/25494054/168956980-be2f396d-4294-44c1-8f5c-e49b2697be39.png)

PV 및 PVC 기능을 통한 mySQL 연동

PV 및 PVC 생성(pv.yml)

볼륨의 크기는 20g이며 하나의 Pod에 의해서만 마운트될 수 있음

![20220518_141016](https://user-images.githubusercontent.com/25494054/168961626-b2461a62-4946-4816-87eb-8639c45f87dd.png)

생성된 PVC를 POD에 적용(deployment.yml
mysql-pv-claim라는 PVC 사용

![20220518_141253](https://user-images.githubusercontent.com/25494054/168961907-b35cffdd-099e-465a-a4c4-8db9e0b3b67f.png)

생성된 PV 및 PVC 확인

![20220518_141545](https://user-images.githubusercontent.com/25494054/168962334-d5e40200-e353-4571-ac1c-e0d00a467cd9.png)

생성된 PV 및 PVC를 통한 mySQL 연동

```
cd team/mysql/kubernetes/
kubectl apply -f pv.yml
kubectl apply -f configmap.yml
kubectl apply -f deployment.yml
```

mySQL Client 접속하기
최초 접속 시
```
kubectl run -it --rm --image=mysql:5.6 --restart=Never mysql-client -- mysql -h mysql -ppassword
```

![20220518_141753](https://user-images.githubusercontent.com/25494054/168962594-b4db9d1a-6d34-44a7-abcd-92de1a4c330e.png)


pod 생성 후 접속 시(또는 위 mysql-client POD 삭제 후 다시 생성하여 접속)
```
kubectl get po로 위에서 생성한 mySQL POD 명 확인
kubectl exec mysql-client -it /bin/bash
mysql -h mysql -ppassword 하면 mySQL 접속 완료
show databases; 실행 가능
```

