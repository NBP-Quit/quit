## QUIT : 대규모 트래픽 처리 식당 예약 서비스

![프로젝트 대표 이미지](.jpg)

**QUIT**는 고객을 대기줄에서 '나가게(Quit)' 하여 **더 빠르고 편리한 식사 경험을 제공하겠다는 의미**를 담았습니다.

**대규모 트래픽**을 **안정적으로 처리**할 수 있는 **식당 예약 플랫폼**을 구축하여 사용자에게 빠르고 원활한 예약 서비스를 제공합니다.

<br>

## 목차
[프로젝트 핵심 목표](#프로젝트-핵심-목표)

[기술 스택](#기술-스택)

[KEY Summary](#key-summary)

[ERD](#erd)

[인프라 아키텍처](#인프라-아키텍처)

[주요 기능](#주요-기능)

[역할 분담](#역할-분담)

<br>

## 프로젝트 핵심 목표

1. **대규모 트래픽 대응**
   - Redis와 Kafka를 활용한 비동기 처리를 통해 API 요청 200req/sec 이상 처리.
   - 동시성 문제를 해결하며 식당 예약 서비스 제공.

2. **성능 최적화**
   - Redis 기반 캐싱으로 실시간 상품 조회 성능을 향상.
   - Redisson을 사용하여 안정적 데이터 처리 구현.

3. **운영 및 배포 효율화**
   - Docker와 Github Actions를 이용한 CI/CD 파이프라인 구축으로 배포 자동화.
   - Prometheus와 Grafana를 활용한 실시간 모니터링으로 시스템 안정성 확보.

4. **데이터 일관성 및 트랜잭션 관리**
   - Kafka를 이용한 SAGA 패턴으로 분산 트랜잭션 관리.

<br>

## 기술 스택
### Backend
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white)
![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-000?style=for-the-badge&logo=apachekafka)

### DevOps
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-%23FF9900.svg?style=for-the-badge&logo=amazon-aws&logoColor=white)

### Monitering & Notification
![Slack](https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=slack&logoColor=white)
![Grafana](https://img.shields.io/badge/grafana-%23F46800.svg?style=for-the-badge&logo=grafana&logoColor=white)
![Prometheus](https://img.shields.io/badge/Prometheus-E6522C?style=for-the-badge&logo=Prometheus&logoColor=white)

### Communication
![Slack](https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=slack&logoColor=white)
![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)
![Notion](https://img.shields.io/badge/Notion-%23000000.svg?style=for-the-badge&logo=notion&logoColor=white)

### Etc.
![Amazon S3](https://img.shields.io/badge/Amazon%20S3-FF9900?style=for-the-badge&logo=amazons3&logoColor=white)

<br>

## KEY Summary

### 🍁 **성능 개선 : 자주 조회되는 데이터 


1. **한 줄 요약**  
   - Redis 도입으로 기존 DB 조회보다 **348% 성능 개선**  
   - 대규모 트래픽 환경에서도 안정적인 서비스 유지  

   ![성능 개선 이미지]

2. **도입 배경**  
   - 상품의 최저가를 제공하기 위해 외부 서버에서 제공하는 타임세일 상품의 할인율과  
     상품 자체의 할인율을 비교하는 기능이 필요  

3. **기술적 선택지**  

   1. **DB 데이터 적재**  
      - 스케줄링 작업으로 짧은 시간 내 대량의 데이터를 수정하는 것은 데이터베이스에 과도한 부하 발생  
      - 상품 자체의 할인율과 타임세일 할인율을 분리하여 별도 컬럼 저장 필요  

   2. **Redis 캐싱**  
      - 실시간 최저가 할인율로 최신 정보와 가격 제공  
      - TTL 설정으로 타임세일 종료 시 자동 데이터 삭제  

   **결론:** Redis 도입을 결정하여 성능 및 효율성을 크게 개선  

<br>

## ERD
<img width="1576" alt="image" src="https://github.com/user-attachments/assets/29210eeb-0237-4dd7-9064-07ae9447c03f" />

<br>

## 인프라 아키텍처

### 아키텍처 다이어그램
<img width="1209" alt="image" src="https://github.com/user-attachments/assets/68cf4a40-6465-47fc-929e-beca5d7359a7" />


위 아키텍처는 **MSA 기반의 서비스** 구조를 나타냅니다.  
각 모듈은 OpenFeign, Kafka를 통해 통신하며, Docker로 컨테이너화되어 CI/CD를 통해 자동 배포됩니다.

<br>

## 주요 기능
### 🥐 **Kafka를 통한 비동기 메시징 처리**

- 대기열, 예약, 예약 인원 관리, 결제, 알림 간 비동기 메시지 처리로 서비스 간 독립성과 확장성 확보.
- 대규모 트래픽 환경에서도 안정적인 데이터 전송과 처리 지원.

### 🥐 **WebFlux 기반 비동기 대기열 서비스**

- 대기열 서비스에 **WebFlux 비동기 모델** 도입으로 높은 동시성과 빠른 응답 속도 제공.
- 비동기 처리 방식으로 대규모 트래픽 처리와 리소스 사용 최적화.

### 🥐 **Redisson 분산 락을 활용한 동시성 제어**

- 예약 관련 로직에 분산 락을 적용.
- 데이터 정합성 유지하여 동시성 문제를 방지하고 안정적인 데이터 처리 구현

### 🥐 **Redis를 활용한 캐싱 처리**

- 빈번하게 조회되는 자원에 캐싱을 적용하여 데이터베이스 부하를 감소.
- 빠른 데이터 응답 속도로 사용자 경험 개선.

<br>

## 기술적 의사결정
<details>
  <summary>대규모 트래픽 처리와 안정성을 위한 Kafka 도입</summary>
  
### 도입 배경

- MSA 환경에서 동기 처리 방식은 대규모 트래픽 상황에서 성능 및 확장성의 한계를 드러냄.
- 비동기 메시지 처리를 통해 서비스 간 독립성을 유지하고, 대규모 트래픽 상황에서도 안정적인 성능을 제공하기 위해 메시징 시스템 도입을 검토.

### 기술적 선택지

1. **RabbitMQ**
    - 장점
        - 간단한 설정과 사용법, 메시지 전달 지연 시간 낮음.
    - 단점
        - 수평 확장이 어렵고, 대규모 트래픽 처리 시 성능 제한.
2. **Kafka**
    - 장점
        - 수평 확장이 용이하고, 대용량 트래픽을 안정적으로 처리.
        - 메시지의 중복 처리 및 장기 보관이 가능하여 높은 신뢰성 제공.
    - 단점
        - 초기 설정 및 운영 복잡도가 높음.
    

### 의사 결정

✅ **Kafka 선택**

- **선택 이유**
    - **Kafka**는 대규모 트래픽 환경에서 **RabbitMQ**보다 **뛰어난 성능과 안정성**을 제공.
    - **수평 확장이 용이**해, 향후 서비스 확장을 고려할 때 **유연한 대응**이 가능.
    - **메시지 보관** 및 **중복 데이터 처리**에서 높은 신뢰성을 보장.

➡️ 이러한 장점은 초기 설정 및 운영의 **높은 복잡도를 상쇄할 만큼의 가치**가 있다고 판단하여 **Kafka를 채택**.
</details>
<details>
  <summary>대기열 서비스 WebFlux 기반 비동기 모델 도입</summary>

### 도입 배경

- 대기열 서비스는 높은 동시성 요구와 대규모 트래픽 처리 능력이 필수적.
- 기존의 동기 처리 방식은 대규모 트래픽 상황에서 자원 소모와 성능 저하 문제를 초래할 가능성이 있음.
- 이를 해결하기 위해 비동기 처리 방식을 도입하여 적은 리소스로 높은 동시성과 빠른 응답 속도를 제공하고자 함.

### 기술적 선택지

1. **Spring MVC**
    - **장점**: 기존 동기 처리 방식으로 간단한 개발과 익숙한 코드베이스 제공.
    - **단점**: 동기식 요청-응답 구조로 인해 동시 처리 능력과 리소스 효율성이 제한적.
2. **Spring WebFlux**
    - **장점**: Non-blocking I/O 기반으로 적은 리소스에서 높은 동시성을 제공.
    - **Reactive Stream**을 활용해 백프레셔를 관리하고, 소비자 속도에 맞춰 데이터를 처리 가능.
    - Redis와의 연동 시 **Reactive Redis Template**을 사용해 기존 동기 방식보다 데이터 처리 속도가 우수.
    - **단점**: 기존 동기식 개발 방식보다 높은 러닝 커브와 코드 복잡도.
    

### 의사 결정

✅ **WebFlux**와 **Reactive Redis Template 조합**으로 대기열 서비스의 비동기 처리 시스템 구성

- 선택이유
    - Non-blocking I/O 기반으로 높은 동시성과 대규모 트래픽 처리에 적합.
    - Reactive Stream을 통해 데이터 처리 속도와 백프레셔 관리 효율성 강화.
    - Redis와 **Reactive Redis Template**의 조합으로 대기열 상태 관리와 성능 최적화 기대.
    
    ➡️  초기 러닝 커브와 코드 복잡도를 고려했지만, **대규모 트래픽 상황에서 안정적이고 효율적인 성능을 제공**할 수 있어 **WebFlux를 채택**.
</details>
<details>
  <summary>Toss Payments API 연동 방식 선정</summary>

**RestTemplate vs RestClient vs OpenFeign**

### 도입 배경

- 결제 기능 구현에서 Toss Payments API를 연동하기 위해 HTTP 통신 방식을 선택 필요
- 결제와 같은 민감한 서비스에서 안정성과 유지보수성을 보장할 수 있어야 하며, 코드의 간결성과 확장성도 고려해야함

### 기술적 선택지

- **RestTemplate**
    - **장점**: Spring에서 널리 사용되며, 비교적 간단하게 설정 및 사용 가능.
    - **단점**: **Boilerplate 코드가 많아 코드 가독성과 유지보수성에서 불리.
    - Spring 5부터는 `Deprecated`로 선언되어 장기적인 사용이 권장되지 않음.
- **RestClient**
    - **장점**: Spring 6부터 제공되는 HTTP 클라이언트로, 현대적인 HTTP 요청 처리를 지원.
    - **단점**: 초기 사용자가 적어 커뮤니티 지원이 부족하며, 프로젝트 초기에 안정성을 보장하기 어려움.
- **OpenFeign**
    - **장점**: 선언형 HTTP 클라이언트를 통해 직관적인 코드 작성 가능.
    - Boilerplate 코드를 줄이고, 유지보수성과 확장성이 뛰어남.
    - **단점**: 학습 곡선이 존재하며, 설정이 RestTemplate보다 복잡할 수 있음.

***Boilerplate 코드 : 반복적이고 구조적으로 큰 변화 없이 자주 작성되는 코드*

### 의사 결정

**✅ OpenFeign**을 선택하여 HTTP 통신을 구현

- **선택 이유**
    - **선언형 HTTP 클라이언트**를 제공하여 API 연동 시 **코드가 간결하고 직관적**임.
    - 기존에 내부 서비스 호출에서도 OpenFeign을 사용 중이어서 **자연스러운 통합**이 가능.
    - **유지보수성과 확장성** 측면에서 RestTemplate와 RestClient보다 더 유리함.
    - **ErrorDecoder**, **Retryer** 등의 설정을 활용하여 **요청 실패 처리 및 안정성을 효과적으로 강화**.
</details>
<details>
  <summary>동시성 제어를 위한 Redis 분산 락 도입</summary>

### 도입 배경

- 대규모 트래픽 상황에서 예약 시 데이터 정합성을 보장하기 위해 동일 자원에 대한 접근 제어가 필요했기 때문에 분산 락 적용

### 기술적 선택지

- **Lettuce**
    - 장점: 가볍고 유연하며, 비동기 및 반응형 지원, 단순한 분산 락 구현 가능
    - 단점: 구현 복잡성, 복제본 일관성 문제, 재진입 락 미지원
- **Lua Script**
    - 장점: 원자성 보장, 효율적 네트워크 통신, 유연한 커스터마이징
    - 단점: 스크립트 관리 복잡, 복제본 일관성 문제, 디버깅 어려움
- **Redisson**
    - 장점: Redlock 지원, 재진입 락 기본 제공, API 편리성, 클러스터 환경 지원
    - 단점: 복잡한 알고리즘, 성능 부담, 라이브러리 종속

### 의사 결정

Redisson을 선택한 이유: Redis의 기본 분산 락 기능보다 자동 만료, 재시도, 공정성 보장 등 고급 기능을 지원하여 구현 복잡도를 낮추고 안정성을 높임.

또한, 다중 인스턴스 및 분산 시스템 환경에서 동작을 보장하여 단일 서버뿐만 아니라 여러 노드 간의 자원 동기화가 필요한 상황에서도 안정적으로 락을 관리할 수 있음.
</details>

<br>

## 트러블슈팅
<details>
  <summary>프로메테우스 매트릭 수집과 인증 처리 문제 해결</summary>

### **문제 정의**

1. **프로메테우스 매트릭 수집 시 인증 문제**
    - Prometheus가 `/actuator/prometheus` 엔드포인트에 요청을 보낼 때, `X-User-Role` 인증 헤더가 포함되지 않아 인증 오류 발생.
    - 필터에서 이를 차단하며 무한 루프가 발생, 매트릭 수집이 중단되는 문제 발생.
2. **외부 악의적 접근 방지 필요**
    - `X-User-Role` 헤더 없이 외부에서 `/actuator/prometheus`에 접근할 경우, 인증 없이 매트릭 데이터에 접근할 가능성 존재.
3. **매트릭 수집 요청과 사용자 접근 로직의 충돌**
    - 동일한 엔드포인트에 대해 매트릭 수집 요청(Prometheus)과 사용자 요청이 혼재되어 필터가 요청을 올바르게 처리하지 못하는 문제 발생.
    
- 동일한 엔드포인트에서 프로메테우스 매트릭 수집 요청과 사용자 요청이 혼재.
- 필터가 요청의 목적에 따라 적절히 처리하지 못해 인증 로직이 충돌.

### **가설**

- `User-Agent` 헤더를 통해 Prometheus 의 요청과 일반 사용자의 요청을 구분할 수 있음.
- Prometheus 요청에 대해 인증을 우회 처리하면 무한 루프 문제를 방지할 수 있음.
- 사용자 요청에는 기존 `X-User-Role` 기반 인증을 유지하면서, 악의적 접근을 방지할 수 있음.

### 해결 방안

1. **프로메테우스 요청 식별**
    - `User-Agent` **헤더**를 활용하여 Prometheus 요청을 식별.
        - `User-Agent`에 Prometheus 문자열이 포함된 요청은 인증을 우회하도록 로직 수정.
2. **악의적 접근 방지**
    - `User-Agent`가 Prometheus가 아닌 요청은 `X-User-Role` 헤더를 필수적으로 요구.
    - 인증 없는 외부 접근 시 매트릭 데이터를 차단.
3. **매트릭 수집 요청과 사용자 요청 분리**
    - 요청별 로직 분리:
        - Prometheus 요청 → 인증 없이 매트릭 데이터 제공.
        - 사용자 요청 → `MASTER` ****역할 인증을 요구하도록 필터 개선.

### 해결 완료

1. **User-Agent 기반 인증 로직 구현**
    - `User-Agent`가 Prometheus 요청은 인증 없이 처리.
    - 그 외 요청은 `X-User-Role` 헤더를 필수적으로 확인하여 인증을 요구.
2. **매트릭 수집과 사용자 요청 분리**
    - Prometheus 요청과 사용자 요청을 구분하여 독립적으로 처리하도록 필터 로직 개선.
3. **문제 해결 결과**
    - Prometheus 매트릭 수집이 정상적으로 동작하면서도 외부의 악의적 접근을 차단.
    - 인증 로직이 강화되어 사용자 요청과 매트릭 수집 요청 간 충돌이 해결.
</details>
<details>
  <summary>Kafka 직렬화 / 역직렬화 문제</summary>

### **문제 정의**

Topic을 통해 메시지로 데이터를 전달하기 위해 JsonSerializer 사용해 직렬화 시 MSA 환경에서는 JsonDeserializer가 메시지를 역직렬화 하지 못하는 문제 발생

### **가설**

JsonSerializer로 메시지 직렬화 시 메시지 헤더의 클래스 정보를 JsonDeserializer가 참조하지 못하기 때문에 역직렬화 시 문제가 발생함

### 해결 방안

1. 다양한 메시지를 직렬화/역직렬화 하기 위해 Object Mapper를 사용해 Custom Serializer/DeSerializer를 구현
2. 직렬화 시 메시지 헤더의 클래스 정보를 참조하지 않도록 설정 및 각각의 메시지 타입을 DeSerializer 설정 시 명시적으로 지정하여 Consumer를 설정
- 보안, 성능, 데이터 간결성 측면에서 2번 방식이 유리하며 서비스에 사용되는 메시지 타입이 많지 않기 때문에 2번 방식을 선택

### 해결 완료

- 메시지 헤더에 클래스 정보를 넣지 않고 Consumer에 각각의 메시지 타입을 명시하는 방식으로 설정하는 것을 통해 메시지 역직렬화 문제 해결

### **회고**

- 위 방식은 보안 및 성능적인 측면에서 장점을 가지지만 추후 메시지 타입이 계속 늘어나는 경우 Consumer 설정을 추가해야 하므로 확장성 측면에서 단점이 있다.
- 프로젝트의 요구사항 및 Trade off에 따라 1번과 2번 방식을 적절하게 선택해서 적용하는 것이 필요하다.
</details>
<details>
  <summary>Redisson 분산 락 Key 적용 시 내부 함수 사용 불가 문제</summary>

### **문제 정의**

Redisson 분산 락 Key 설정 시 내부 함수를 호출할 수 없는 문제 발생

### **가설**

Proxy는 내부 함수(자기 자신)을 호출할 수 없기 때문에 lock key 설정 시 에러가 발생한다.

### 해결 방안

1. Proxy가 내부 함수를 호출할 수 있도록 선언한다.
2. Key 값으로 설정하기 위해 필요한 값을 메서드 파라미터로 전달하여 메서드 파라미터를 key 값으로 설정한다.

Proxy가 내부 함수를 호출할 수 있도록 선언할 경우 순환 참조 문제가 발생할 수 있기 때문에 Key 값으로 설정하려는 값을 미리 조회하여 메서드 파라미터로 전달하는 방식 선택.

### 해결 완료

- 예약의 slot id(예약 시간 ID) 정보를 미리 조회하여 메서드 파라미터로 전달하여 분산 락 적용 시 Key 값으로 설정할 수 있도록 하며 로깅에도 사용할 수 있도록 처리.
(어노테이션에서 내부 함수 호출 시 Proxy 문제 발생)
    
    ![image2.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/83c75a39-3aba-4ba4-a792-7aefe4b07895/d0ea09ca-3583-4ead-8776-922215de18b7/image2.png)
    
    (Slot ID를 Key로 사용하여 분산 락을 획득하고 작업 완료 후 락을 해제)
    
    ![로그.PNG](https://prod-files-secure.s3.us-west-2.amazonaws.com/83c75a39-3aba-4ba4-a792-7aefe4b07895/3e5000fb-7ab0-4225-a957-2de5022c8610/%EB%A1%9C%EA%B7%B8.png)
    

### **회고**

- 위 해결 방식은 메시지에 포함된 예약 ID를 DB에 조회하여 해당 예약에 포함된 slot ID를 파라미터로 전달하기 때문에 조회 작업을 한 번 더 해야 된다는 문제점이 있다.
- 순환 참조 및 중복 조회 없이 필요한 데이터를 사용할 수 있는 설계 방법을 생각해 볼 필요성을 느꼈다.
</details>

<br>

## 역할 분담

### **Detail Role**

| 이름   | 포지션   | 담당(개인별 기여점)                                                                                                            | Github 링크                       |
|--------|----------|-----------------------------------------------------------------------------------------------------------------------------|-----------------------------------|
| 한미수 | 리더     | ▶ **대기열**: <br>▶ **모니터링**: | [https://github.com/HMisu](https://github.com/HMisu) |
| 박용운 | 부리더   | ▶ **예약**: | [https://github.com/eleunadeu](https://github.com/eleunadeu)    |
| 양혜지 | 팀원     | ▶ **인증/인가**: <br>▶ **배포**: | [https://github.com/laira2](https://github.com/laira2)   |
| 이건 | 팀원     | ▶ **리뷰**: <br>▶ **알림**: | [https://github.com/geon8692](https://github.com/geon8692)    |
| 이소현 | 팀원     | ▶ **가게**: <br>▶ **결제**:  | [https://github.com/sohyuneeee](https://github.com/sohyuneeee)    |
