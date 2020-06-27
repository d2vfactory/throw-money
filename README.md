# throw-money




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