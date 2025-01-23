## 🍽️ QUIT : 대규모 트래픽 처리 식당 예약 서비스

![박람회장_이미지_뒷면_노션](https://github.com/user-attachments/assets/4f5805ab-3227-461a-b910-2e557cbac132)

**QUIT**는 고객을 대기줄에서 '나가게(Quit)' 하여 **더 빠르고 편리한 식사 경험을 제공하겠다는 의미**를 담았습니다.

**대규모 트래픽**을 **안정적으로 처리**할 수 있는 **식당 예약 플랫폼**을 구축하여 사용자에게 빠르고 원활한 예약 서비스를 제공합니다.

<br>

## 목차
🍪 [프로젝트 핵심 목표](#프로젝트-핵심-목표)

🍪 [기술 스택](#기술-스택)

🍪 [KEY Summary](#key-summary)

🍪 [ERD](#erd)

🍪 [인프라 아키텍처](#인프라-아키텍처)

🍪 [주요 기능](#주요-기능)

🍪 [역할 분담](#역할-분담)

<br>

## 프로젝트 핵심 목표

### 🥨 대규모 트래픽 대응

- **MSA 구조**를 통해 서비스의 확장성과 유연성을 확보
- **Redis**, **Kafka**로 **대규모 트래픽 안정적** 처리
- 주요 API 요청 200.0req/sec 이상 처리
- **동시성 문제를 해결**하며 안정적 서비스 제공
- 부하 테스트

### 🥨 배포 및 운영

- **Docker**와 **Github Actions**를 이용한 **CI/CD** 파이프라인 구축

### 🥨 모니터링 시스템 구축

- **Prometheus**와 **Grafana**를 활용한 **실시간 모니터링**으로 시스템 안정성 확보

<br>

## KEY Summary

### 🥐 **Kafka를 통한 비동기 메시징 처리**

- 대기열, 예약, 예약 인원 관리, 결제, 알림 간 비동기 메시지 처리로 서비스 간 독립성과 확장성 확보.
- 대규모 트래픽 환경에서도 안정적인 데이터 전송과 처리 지원.

### 🥐 **Redisson 분산 락을 활용한 동시성 제어**

- 예약 관련 로직에 분산 락을 적용.
- 데이터 정합성 유지하여 동시성 문제를 방지하고 안정적인 데이터 처리 구현

### 🥐 **Redis를 활용한 캐싱 처리**

- 빈번하게 조회되는 자원에 캐싱을 적용하여 데이터베이스 부하를 감소.
- 평균 응답 시간 46.7% 감소, 초당 처리량 약 80% 증가로 성능 최적화 달성.
- 빠른 데이터 응답 속도로 사용자 경험 개선.

### 🥐 **WebFlux 기반 비동기 대기열 서비스**

- WebFlux의 비동기 모델을 도입하여 높은 동시성과 빠른 응답 속도를 제공하여 대규모 트래픽 효율적 처리.
- Redis를 활용하여 실시간 대기열 상태를 관리하고 가게별로 독립적인 대기열을 운영하여 효율적인 대기 관리.
- 놀이동산 방식으로 일정 시간마다 일정 인원이 순차적으로 대기열에서 진입.
- 멀티 인스턴스 환경에서 각 서버에 특정 가게를 할당하여 대기열 서비스를 분배하고 부하를 분산.

<br>

## ERD
<img width="1576" alt="image" src="https://github.com/user-attachments/assets/29210eeb-0237-4dd7-9064-07ae9447c03f" />

<br>

## 인프라 아키텍처

### 아키텍처 다이어그램
<img width="1209" alt="image" src="https://github.com/user-attachments/assets/3067a8f3-bb28-4663-aab2-e90e986121ee" />

위 아키텍처는 **MSA 기반의 서비스** 구조를 나타냅니다.  
각 모듈은 OpenFeign, Kafka를 통해 통신하며, Docker로 컨테이너화되어 CI/CD를 통해 자동 배포됩니다.

<br>

## 주요 기능
🍪 [예약](https://github.com/NBP-Quit/quit/wiki/%5B%EB%8F%84%EB%A9%94%EC%9D%B8-%EB%B3%84-%EC%A3%BC%EC%9A%94-%EA%B8%B0%EB%8A%A5%5D-%EC%98%88%EC%95%BD)

🍪 [대기열](https://github.com/NBP-Quit/quit/wiki/%5B%EB%8F%84%EB%A9%94%EC%9D%B8-%EB%B3%84-%EC%A3%BC%EC%9A%94-%EA%B8%B0%EB%8A%A5%5D-%EB%8C%80%EA%B8%B0%EC%97%B4)

🍪 [가게, 예약스케줄](https://github.com/NBP-Quit/quit/wiki/%5B%EB%8F%84%EB%A9%94%EC%9D%B8-%EB%B3%84-%EC%A3%BC%EC%9A%94-%EA%B8%B0%EB%8A%A5%5D-%EA%B0%80%EA%B2%8C)

🍪 [결제](https://github.com/NBP-Quit/quit/wiki/%5B%EB%8F%84%EB%A9%94%EC%9D%B8-%EB%B3%84-%EC%A3%BC%EC%9A%94-%EA%B8%B0%EB%8A%A5%5D-%EA%B2%B0%EC%A0%9C)

🍪 [유저](https://github.com/NBP-Quit/quit/wiki/%5B%EB%8F%84%EB%A9%94%EC%9D%B8-%EB%B3%84-%EC%A3%BC%EC%9A%94-%EA%B8%B0%EB%8A%A5%5D-%EC%9C%A0%EC%A0%80)

🍪 [리뷰](https://github.com/NBP-Quit/quit/wiki/%5B%EB%8F%84%EB%A9%94%EC%9D%B8-%EB%B3%84-%EC%A3%BC%EC%9A%94-%EA%B8%B0%EB%8A%A5%5D-%EB%A6%AC%EB%B7%B0)

🍪 [알림](https://github.com/NBP-Quit/quit/wiki/%5B%EB%8F%84%EB%A9%94%EC%9D%B8-%EB%B3%84-%EC%A3%BC%EC%9A%94-%EA%B8%B0%EB%8A%A5%5D-%EC%95%8C%EB%A6%BC)

🍪 [모니터링](https://github.com/NBP-Quit/quit/wiki/%5B%EB%8F%84%EB%A9%94%EC%9D%B8-%EB%B3%84-%EC%A3%BC%EC%9A%94-%EA%B8%B0%EB%8A%A5%5D-%EB%AA%A8%EB%8B%88%ED%84%B0%EB%A7%81)

<br>

## 기술적 의사결정
🥨 [대규모 트래픽 처리와 안정성을 위한 Kafka 도입](https://github.com/NBP-Quit/quit/wiki/%5B%EA%B8%B0%EC%88%A0%EC%A0%81-%EC%9D%98%EC%82%AC%EA%B2%B0%EC%A0%95%5D-%EB%8C%80%EA%B7%9C%EB%AA%A8-%ED%8A%B8%EB%9E%98%ED%94%BD-%EC%B2%98%EB%A6%AC%EC%99%80-%EC%95%88%EC%A0%95%EC%84%B1%EC%9D%84-%EC%9C%84%ED%95%9C-Kafka-%EB%8F%84%EC%9E%85)

🥨 [대기열 서비스 WebFlux 기반 비동기 모델 도입](https://github.com/NBP-Quit/quit/wiki/%5B%EA%B8%B0%EC%88%A0%EC%A0%81-%EC%9D%98%EC%82%AC%EA%B2%B0%EC%A0%95%5D-%EB%8C%80%EA%B8%B0%EC%97%B4-%EC%84%9C%EB%B9%84%EC%8A%A4-WebFlux-%EA%B8%B0%EB%B0%98-%EB%B9%84%EB%8F%99%EA%B8%B0-%EB%AA%A8%EB%8D%B8-%EB%8F%84%EC%9E%85)

🥨 [Toss Payments API 연동 방식 선정](https://github.com/NBP-Quit/quit/wiki/%5B%EA%B8%B0%EC%88%A0%EC%A0%81-%EC%9D%98%EC%82%AC%EA%B2%B0%EC%A0%95%5D-Toss-Payments-API-%EC%97%B0%EB%8F%99-%EB%B0%A9%EC%8B%9D-%EC%84%A0%EC%A0%95)

🥨 [동시성 제어 방식 선정](https://github.com/NBP-Quit/quit/wiki/%5B%EA%B8%B0%EC%88%A0%EC%A0%81-%EC%9D%98%EC%82%AC%EA%B2%B0%EC%A0%95%5D-%EB%8F%99%EC%8B%9C%EC%84%B1-%EC%A0%9C%EC%96%B4-%EB%B0%A9%EC%8B%9D-%EC%84%A0%EC%A0%95)

<br>

## 트러블슈팅
🥖 [프로메테우스 매트릭 수집과 인증 처리 문제 해결](https://github.com/NBP-Quit/quit/wiki/%5B%ED%8A%B8%EB%9F%AC%EB%B8%94%EC%8A%88%ED%8C%85%5D-%ED%94%84%EB%A1%9C%EB%A9%94%ED%85%8C%EC%9A%B0%EC%8A%A4-%EB%A7%A4%ED%8A%B8%EB%A6%AD-%EC%88%98%EC%A7%91%EA%B3%BC-%EC%9D%B8%EC%A6%9D-%EC%B2%98%EB%A6%AC-%EB%AC%B8%EC%A0%9C-%ED%95%B4%EA%B2%B0)

🥖 [Kafka 메시지 직렬화/역직렬화 문제 해결](https://github.com/NBP-Quit/quit/wiki/%5B%ED%8A%B8%EB%9F%AC%EB%B8%94-%EC%8A%88%ED%8C%85%5D-Kafka-%EB%A9%94%EC%8B%9C%EC%A7%80-%EC%A7%81%EB%A0%AC%ED%99%94-%EC%97%AD%EC%A7%81%EB%A0%AC%ED%99%94-%EB%AC%B8%EC%A0%9C-%ED%95%B4%EA%B2%B0)

🥖 [Redisson 분산락 적용으로 동시성 문제 해결](https://github.com/NBP-Quit/quit/wiki/%5B%ED%8A%B8%EB%9F%AC%EB%B8%94%EC%8A%88%ED%8C%85%5D-Redisson-%EB%B6%84%EC%82%B0%EB%9D%BD-%EC%A0%81%EC%9A%A9%EC%9C%BC%EB%A1%9C-%EB%8F%99%EC%8B%9C%EC%84%B1-%EB%AC%B8%EC%A0%9C-%ED%95%B4%EA%B2%B0)

🥖 [Redisson 분산락 Key 적용 시 내부 함수 사용 불가 문제 해결](https://github.com/NBP-Quit/quit/wiki/%5B%ED%8A%B8%EB%9F%AC%EB%B8%94%EC%8A%88%ED%8C%85%5D-Redisson-%EB%B6%84%EC%82%B0%EB%9D%BD-Key-%EC%A0%81%EC%9A%A9-%EC%8B%9C-%EB%82%B4%EB%B6%80-%ED%95%A8%EC%88%98-%EC%82%AC%EC%9A%A9-%EB%B6%88%EA%B0%80-%EB%AC%B8%EC%A0%9C)

🥖 [대용량 트래픽 처리가 요구되는 대기열 서버의 과부화 문제 해결](https://github.com/NBP-Quit/quit/wiki/%5B%ED%8A%B8%EB%9F%AC%EB%B8%94%EC%8A%88%ED%8C%85%5D-%EB%8C%80%EC%9A%A9%EB%9F%89-%ED%8A%B8%EB%9E%98%ED%94%BD-%EC%B2%98%EB%A6%AC%EA%B0%80-%EC%9A%94%EA%B5%AC%EB%90%98%EB%8A%94-%EB%8C%80%EA%B8%B0%EC%97%B4-%EC%84%9C%EB%B2%84%EC%9D%98-%EA%B3%BC%EB%B6%80%ED%99%94-%EB%AC%B8%EC%A0%9C)

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

## 역할 분담

### **Detail Role**

|    이름    |      포지션         | 담당(개인별 기여점)                                                                                                           | Github 링크              |
|:---------------:|:-------------------:|---------------------------------------------------------------------------------------------------------------------------|-------------------------|
| 한미수 &nbsp;&nbsp;&nbsp;&nbsp; | 리더 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; | ▶ **대기열**: <br> - Spring WebFlux 활용으로 높은 동시성과 빠른 응답속도 제공 및 적은 리소스로 대규모 트래픽 처리<br>- Redis를 통한 대기열 정보 및 상태 관리<br>- 대규모 트래픽 상황에서 멀티 인스턴스를 통한 부하 방지<br>- 대기열 진입 시 Feign Client 가게 정보 검증<br>- 예약 서비스 진입을 위해 Kafka 메시지 발행<br>▶ **모니터링**<br>- Prometheus을 사용해 서비스 메트릭 수집<br>- Grafana 연동으로 Prometheus에서 수집한 데이터 시각화 및 실시간 서비스 모니터링 가능한 대시보드 생성<br>- Grafana에서 CPU 사용량이 50% 이상일 때 자동으로 Slack에 알림이 전송되도록 설정 | [GitHub](https://github.com/HMisu) |
| 박용운 |부리더  | ▶ **예약**: <br> - Kafka 메시지 기반 비동기 예약 처리 및 권한에 따른 동기 처리 구현 <br> - Redisson 분산 락 적용 예약 작업 동시성 제어 <br> - 예약 시 feign client 사용 예약 가능 여부 확인 및 가게 주인 여부 확인 <br> - 외부 서비스 호출 시 circuit breaker 적용, fallback 처리 구현 <br> - QueryDSL 적용 조회 속도 개선 및 관리자 검색 기능 제공 | [GitHub](https://github.com/eleunadeu)    |
| 양혜지 | 팀원     | ▶ **인증/인가**: <br> ▶ **사용자 관리**: <br> - 관리자 권한 요청을 통해 필요한 권한 획득 <br> - JWT 토큰 기반 로그인 기능 구현 <br> - Redis 활용 Rate Limiting 기능 도입 <br> ▶ **배포**: <br> - GitHub Actions 사용 CI/CD 파이프라인 구축 <br> - Docker Hub 사용 이미지 관리 <br> - 배포 시 사용되는 민감한 환경 변수 AWS Parameter Store로 저장 및 GitHub Actions의 Secrets 기능과 연계해 CI/CD 파이프라인에서 동적으로 불어와 사용해 보안성 강화 | [GitHub](https://github.com/laira2)   |
| 이건 | 팀원     | ▶ **리뷰**: <br> - QueryDSL 활용 검색 기능 구현 <br> - OpneFeign 사용 예약 정보 조회 및 검증, 리뷰 신고 요청 <br> - Redis Set, Hash 자료구조를 사용 좋아요, 리뷰 개수, 가게 평균 평점 관리 <br> - Redis 캐싱 사용 가게 평균 별점, 인기 리뷰 조회 성능 개선 <br> - 좋아요 기능 Write-back 캐싱 전략 적용  <br> ▶ **알림**: <br> - 예약 성공 시 Kafka 비동기 처리로 Slack 개인 DM으로 알림 전송 | [GitHub](https://github.com/geon8692)    |
| 이소현 | 팀원     | ▶ **가게, 예약스케줄**: <br> - QueryDSL 활용하여 검색 기능 구현 <br> - Redis 사용하여 빈번하게 조회 되는 데이터 캐싱 적용 <br> - Kafka 비동기 처리로 예약 슬롯(예약 스케줄)의 현재 예약 인원 감소 및 복구 <br> - 현재 예약 인원 감소 및 복구 시 Redisson 분산 락 적용하여 동시성 제어  <br>▶ **결제**: <br> - OpenFeign 사용하여 토스페이먼츠 연동 (테스트환경) <br> - 멱등키 적용하여 결제 승인 및 취소 요청 시 멱등성 보장 <br> - kafka 비동기 처리 | [GitHub](https://github.com/sohyuneeee)    |
