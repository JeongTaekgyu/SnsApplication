# Community Application

### 대용량 트래픽을 고려한 커뮤니티 어플리케이션을 구현했습니다.

개발 환경 : Spring Boot, Gradle, Kafka, Redis, JPA, Spring Security, Github Action, heroku, PostgreSQL
- Redis를 활용하여 데이터 캐싱을 통해 DB 접근 횟수를 줄여 부하를 낮춤
- SSE를 통한 알람 기능을 Kafka를 사용하여 비동기적으로 처리
- 인덱싱을 통하여 테이블 탐색시간을 줄이고 삭제 시 데이터를 불러오지 않고 바로 삭제하여 실행시간을 줄임
- MockMvc를 사용한 테스트 주도 개발
- Spring Security와 JWT 로그인 기능 구현

### Flow Chart
1. 회원가입 <br><br>
![회원가입](https://user-images.githubusercontent.com/32161395/196931615-43add39f-272d-4551-a0b5-c2a526c538ef.png)
<br><br>
2. 로그인<br><br>
![로그인](https://user-images.githubusercontent.com/32161395/196931913-48ae88af-24f7-4174-ac20-db3a0506781c.png)
<br><br>
3. 포스트 작성<br><br>
![3 포스트작성](https://user-images.githubusercontent.com/32161395/196932056-e1e25ec8-2360-42c5-a337-09e3354f5da9.png)
<br><br>
4. 포스트 삭제<br><br>
![4 포스트삭제](https://user-images.githubusercontent.com/32161395/196932206-2ce00c81-ba0c-40d1-bb05-e3c82fb8c5d2.png)
<br><br>
5. 포스트 수정<br><br>
![5 포스트수정](https://user-images.githubusercontent.com/32161395/196932264-6ef9574a-fe79-4bf5-b7de-4ade6b40e611.png)
<br><br>
6. 피드 목록 조회<br><br>
![6 피드목록](https://user-images.githubusercontent.com/32161395/196932367-7592769a-2df2-46fd-bc95-8cba5afc1daa.png)
<br><br>
7. 좋아요 기능 : User A가 B 게시물에 좋아요를 누른 상황<br><br>
![User A가 B 게시물에 좋아요를 누른상황](https://user-images.githubusercontent.com/32161395/196932432-f47719da-89f4-443a-ae02-efba0590fa5f.png)
<br><br>
![7-2](https://user-images.githubusercontent.com/32161395/196933523-505e03d4-da4c-4a15-98f7-aef47eb2e8f6.png)
<br><br>
8. 댓글 기능 : User A가 B 게시물에 댓글을 남긴 상황<br><br>
![8  댓글기능 User A가 B게시물에 댓글을 남긴 상황](https://user-images.githubusercontent.com/32161395/196932937-01c6352b-0ad3-4a66-a383-9669b18cb95d.png)
<br><br>
![8-2](https://user-images.githubusercontent.com/32161395/196932863-8373ee1b-6d7c-4d5b-9f33-7f38ca04bc48.png)
<br><br>
9. 알람 기능 : User A의 알람 목록에 대한 요청을 한 상황<br><br>
![9 알람 기능](https://user-images.githubusercontent.com/32161395/196933375-55de48ae-b358-4ac8-b008-c7d006042ca6.png)
