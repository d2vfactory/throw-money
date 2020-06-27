package com.d2vfactory.throwmoney.domain.money.repository;

import com.d2vfactory.throwmoney.domain.money.ReceiveMoney;
import com.d2vfactory.throwmoney.domain.money.ThrowMoney;
import com.d2vfactory.throwmoney.domain.token.repository.TokenRepository;
import com.d2vfactory.throwmoney.domain.user.Room;
import com.d2vfactory.throwmoney.domain.user.User;
import com.d2vfactory.throwmoney.domain.user.repository.RoomRepository;
import com.d2vfactory.throwmoney.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
class ThrowMoneyRepositoryTest {

    @Autowired
    private ThrowMoneyRepository throwMoneyRepository;

    @Autowired
    private ReceiveMoneyRepository receiveMoneyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private TokenRepository tokenRepository;


    @Test
    @DisplayName("돈 뿌리기 생성 -> 토큰 세팅 -> 선착순 1명")
    @Transactional
    public void throw_money_generate() {
        String roomId = "room-00001";
        User user1 = userRepository.findById(1L).get();
        User user2 = userRepository.findById(2L).get();
        Room room1 = roomRepository.findById(roomId).get();

        ThrowMoney throwMoney = new ThrowMoney();
        throwMoney.setUser(user1);
        throwMoney.setRoom(room1);
        throwMoney.setMoney(3000);

        ReceiveMoney receiveMoney1 = new ReceiveMoney();
        receiveMoney1.setThrowMoney(throwMoney);
        receiveMoney1.setMoney(2000);

        ReceiveMoney receiveMoney2 = new ReceiveMoney();
        receiveMoney2.setThrowMoney(throwMoney);
        receiveMoney2.setMoney(1000);

        throwMoney.getReceivers().add(receiveMoney1);
        throwMoney.getReceivers().add(receiveMoney2);

        throwMoneyRepository.save(throwMoney);


        // 토큰 가져오기
        String token = tokenRepository.findById(throwMoney.getId()).get().getToken();

        throwMoney.setToken(token);
        throwMoneyRepository.save(throwMoney);

        Optional<ThrowMoney> throwMoneyOpt1 = throwMoneyRepository.fetchByTokenAndUser(token, user1);
        log.info("# {}", throwMoneyOpt1);

        // 선점 처리
        // select for update
        List<ReceiveMoney> receiveList = receiveMoneyRepository.findAllByThrowMoney(throwMoney);
        ReceiveMoney anyReceiveMoney = receiveList.stream().filter(x -> x.getUser() == null).findFirst().get();
        anyReceiveMoney.setUser(user2);
        receiveMoneyRepository.save(anyReceiveMoney);

        Optional<ThrowMoney> throwMoneyOpt2 = throwMoneyRepository.fetchByTokenAndUser(token, user1);
        log.info("# {}", throwMoneyOpt2);

    }

}