# throw-money - Rest API

## 돈뿌리기(throw-money)
#### 기능 소개
* 사용자는 다수의 친구들이 있는 대화방에서 뿌릴 금액과 받아갈 대상의 숫자를
입력하여 뿌리기 요청을 보낼 수 있습니다.
* 요청 시 자신의 잔액이 감소되고 대화방에는 뿌리기 메세지가 발송됩니다.
* 대화방에 있는 다른 사용자들은 위에 발송된 메세지를 클릭하여 금액을 무작위로
받아가게 됩니다.

#### 제약 사항
* 요청한 사용자의 식별값은 숫자 형태이며 "X-USER-ID" 라는 HTTP Header로
전달됩니다.
* 요청한 사용자가 속한 대화방의 식별값은 문자 형태이며 "X-ROOM-ID" 라는
HTTP Header로 전달됩니다.

#### 개발 사항
* 돈 뿌리기
  * 분배할 인원과 금액을 정한 후 랜덤으로 뿌린다.
  * 3자리 문자열 토큰을 결과로 내려준다.
* 돈 받기
  * 토큰으로 아직 배정되지 않은 뿌린 돈을 가져간다. (동일한 대화방에서만)
  * 중복으로 받을 수 없고, 돈을 뿌린 사람도 받을 수 없다.
  * 10분간만 유효하다.
* 뿌린돈 조회
  * 뿌린 사람 본민만 조회 가능하며 7일만 유효하다

#### 문제 해결 전략
* 3자리 문자열 토큰
  * AlphaNumeric 기준으로 64^3 이기 때문에 최대 238,328 자리 밖에 안되서 중복 발생 확률이 높다.
    * 건수가 작기 때문에 토큰을 랜덤으로 실시간 발행하지 않고, 미리 만들어서 테이블에 세팅하여 뿌리기 ID에 매핑하여 제공한다. 
* 선착순 배정
  * 토큰 기준으로 배정되지 않은 돈을 선착순으로 받아가기 때문에 동시 요청에 대한 제약이 있어야 한다.
    * 배정되지 않은 토큰의 위치를 찾아서 처리하기 까지 lock 걸어서 먼저 온 요청에 대해 처리하도록 함.
    * @Lock(LockModeType.PESSIMISTIC_WRITE) 

#### 기본 데이터 세팅
사용자
``` 
user1 = 송길주
user2 = 라이언
user3 = 어피치
user4 = 무지
user5 = 콘
user6 = 프로도
user7 = 네오
user8 = 튜브
user9 = 제이지
```
대화방
``` 
room-00001 = 대화방1 (user: 1,2,3,4,5,6,7)
room-00002 = 대화방2 (user: 1,7,8,9)
room-00003 = 대화방3 (user: 2,5)

```

#### sample URL (토큰은 API에서 직접 가져와야함.)
돈 뿌리고 토큰 발급
``` 
http POST :8080/api/throwMoney X-USER-ID:1 X-ROOM-ID:room-00001 size==3 money==1000
```

돈 받기
```                                                                                  
// 돈뿌린 사람이 받을려고 해서 에러
http POST :8080/api/receiveMoney X-USER-ID:1 X-ROOM-ID:room-00001 token==OwI
```

뿌린돈 조회
```
http :8080/api/throwMoney X-USER-ID:1 X-ROOM-ID:room-00001 token==aML

http :8080/api/throwMoney X-USER-ID:1 X-ROOM-ID:room-00001 token==a00
```     

#### h2 console
```
http://localhost:8080/console
```
