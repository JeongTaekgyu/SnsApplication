# Community Application

### 대용량 트래픽을 고려한 커뮤니티 어플리케이션을 구현했습니다.

개발 환경 : Spring Boot, Gradle, Kafka, Redis, JPA, Spring Security, Github Action, heroku, PostgreSQL
- Redis를 활용하여 데이터 캐싱을 통해 DB 접근 횟수를 줄여 부하를 낮춤
- SSE를 통한 알람 기능을 Kafka를 사용하여 비동기적으로 처리
- 인덱싱을 통하여 테이블 탐색시간을 줄이고 삭제 시 데이터를 불러오지 않고 바로 삭제하여 실행시간을 줄임
- MockMvc를 사용한 테스트 주도 개발
- Spring Security와 JWT 로그인 기능 구현
