# MSA 4팀 - 배달의민족 커버
분석/설계/구현/운영 4단계를 커버하는 것을 목표로 구성해 보았습니다.

# msa-capstone-project
<img src= "https://t1.daumcdn.net/cfile/tistory/997A00365C79475E04?download">



## 📢 About
* 지원
  - 미래혁신센터 / Cloud Native개발팀
  - 양기훈 과장, 변용수 대리
* Platform
  - ZOOM
    + URL : https://us06web.zoom.us/j/83816607931?pwd=NFRNUWU2Skh2R1B2bDN5RDB3aDluUT09
    + 회의 ID : 838 1660 7931
    + 암호 : 642939

  - Kakao
    + URL : https://open.kakao.com/o/gguDsE5d

## 🕙 Schedule
* 일자별 진행

    |일자|진행|내용|
    |:------:|:---|:---|
    |05/16 AM|OJT|과정설명<br>과제수행환경설명|
    |05/16 PM|Brain Storming|msaez.io|
    |05/17 ALL|Team Project|팀별과제 수행|
    |05/18 AM|Team Project|팀별과제 수행(계속)|
    |05/18 PM|Wrap Up|과제 제출<br>시작:14시<br>마감:16시|
    |05/19 ALL|*과제평가*|*인사팀 전달*|

* 시간별 진행
  - 09:00 ~ 11:30 오전과정
  - 11:30 ~ 13:00 점심시간
  - 13:00 ~ 17:30 오후과정
  
    > NOTE
  
      + 팀별과제 수행중에는 오전/오후과정 중 자율적으로 휴식
      + 3일차 과제제출시에는 준비된 팀부터 팀별 소그룹에서 발표 진행(마감시간 준수)
  
  
## 👫 Team

   |팀|성명|직급|사번|소속||
   |:----:|:------:|:------:|:------|:------|------|
   |1|🎖김동준|과장|200802525|서비스혁신센터|유화서비스담당/서비스운영1팀|
   ||  홍기웅|대리|201800073|데이터센터|금융운영팀|
   ||  박상우|사원|201907352|PDM사업TF|Tech그룹|
   |2|🎖서정훈|과장|201600017|서비스혁신센터|제조방산담당/방산운영2팀|
   ||  신세호|대리|202100009|서비스혁신센터|제조방산담당/제조운영팀|
   ||   정호현|사원|201903684|데이터센터|금융운영팀|
   |3|🎖이성호|과장|201105900|데이터센터|SharedService담당/SharedService3팀|
   ||  이태민|대리|201801827|데이터센터|금융운영팀|
   ||  박수지|사원|201903628|서비스혁신센터|유화서비스담당/서비스운영2팀|
   |4|🎖이원희|대리|202000536|서비스혁신센터|제조방산담당/방산운영1팀|
   ||  조진송|대리|201800069|서비스혁신센터|제조방산담당/방산운영1팀|
   ||  이종성|대리|202102050|서비스혁신센터|제조방산담당/제조운영팀|  
   



## 🤷🏻‍♂️🤷🏻‍♀️ Curriculum
<img width="1614" alt="image" src="https://user-images.githubusercontent.com/90342002/168257940-b01648f5-c547-4ab0-beab-55b2d9b558b7.png">
<img width="1616" alt="image" src="https://user-images.githubusercontent.com/90342002/168258082-e80d2483-e9ef-471e-84ee-51ca1ae937ef.png">
<img width="1623" alt="image" src="https://user-images.githubusercontent.com/90342002/168258166-64ede392-a491-4bff-a6c4-eb255c0846d5.png">
<img width="1615" alt="image" src="https://user-images.githubusercontent.com/90342002/168258318-29d21d32-7b9d-42aa-b887-322eed942211.png">
<img width="1617" alt="image" src="https://user-images.githubusercontent.com/90342002/168258428-23b1aaf3-155a-416b-965c-2172ae5bb52f.png">




## ✏️ Evaluation

  * 분석설계
  * SAGA Pattern
  * CQRS Pattern
  * Correlation / Compensation(Unique Key)
  * Request / Response (Feign Client / Sync.Async)
  * Gateway
  * Deploy / Pipeline
  * Circuit Breaker
  * Autoscale(HPA)
  * Self-Healing(Liveness Probe)
  * Zero-Downtime Deploy(Readiness Probe)
  * Config Map / Persistence Volume
  * Polyglot

    > NOTE
      + 구현방법 및 산출물 양식은 제한 없이 자유롭게 진행
      + 문항별 담당자명 표시



## 📑 To-Do

* <a href="https://www.msaez.io/#/" target="_blank">Brain Storming</a>
  + 팀별로 소회의실에서 진행
  + 동시수정
* <a href="https://gitpod.io/#/https://github.com/kihoon-yang/msa-capstone-project" target="_blank">GitPod</a>
  + Github 계정 및 Repositoy(public) 준비 ( **for FORK** )
  + gitpod.io/#/{Github-Repository-URL}
* <a href="https://794003312682.signin.aws.amazon.com/console">AWS</a> (*약 15~20분 소요*)
  + 계정정보(IAM)메일 발송
  + Region-Code : `ca-central-1`
  + Cluster-Name : Account-Id
  + Image-Repository-Name : Account-Id

> 참고

  이전실습환경(https://labs.msaez.io/)에 접속해서 실습내용을 확인해 볼 수 있으나, `INTO THE LAB`에서 실습했었던 온라인상의 실행은 불가하므로, `파일다운로드`버튼을 이용해 실습했던 code를 다운로드 해서 확인



## 🧨 Attention
* 교육을 위한 재택근무가 아닌, 출근지에서 업무와 동시에 과제 수행시, Github 사이트로 Local에서 작성한 프로그램이 `Upload` 되지 않도록 주의
* Web Browser IDE 환경에서 과제 수행시, Client의 Network 상황에 따라 작업중인 내용이 유실될 있으니 `저장` 또는 `commit(push)`
* GitPod는 `Github 계정 1개` 기준으로 `약 50개`까지의 Pod를 각각 `약 24시간`까지 유지하므로 진행시 유의 
* 과제수행 중 문의사항있을 경우, 카카오톡 오픈채팅창 이용



## 👍 Good Practice
  * 배달의 민족 Cover : [https://github.com/msa-ez/example-food-delivery]
  * Air BnB Cover : [https://github.com/msa-ez/airbnb_project]
  * 숙박예약 Cover : [https://github.com/msa-ez/airbnb_project]
  


## 🪶 Lite Version (for FORK )
  * Dragon Water Lite 🦖💧 : [https://github.com/yongdoohar/msa-capstone-project]


