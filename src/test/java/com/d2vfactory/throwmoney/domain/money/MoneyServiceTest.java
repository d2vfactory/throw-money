package com.d2vfactory.throwmoney.domain.money;

import com.d2vfactory.throwmoney.domain.money.repository.ThrowMoneyRepository;
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

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
class MoneyServiceTest {

    @Autowired
    private MoneyService moneyService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ThrowMoneyRepository throwMoneyRepository;

    @Test
    @DisplayName("돈 뿌려 보자 - 10000원 3명")
    void throwMoney_user1_room1() {
        int money = 10000;
        int size = 3;
        long userId = 1L;
        String roomId = "room-00001";

        User user = userRepository.findById(userId).get();
        Room room = roomRepository.findById(roomId).get();

        ThrowMoneyForm form = ThrowMoneyForm.builder()
                .user(user)
                .room(room)
                .money(money)
                .size(size)
                .build();

        ThrowMoneyDTO throwMoneyDTO = moneyService.throwMoney(form);

        assertThat(throwMoneyDTO)
                .hasFieldOrPropertyWithValue("userId", userId)
                .hasFieldOrPropertyWithValue("roomId", roomId)
                .hasFieldOrPropertyWithValue("throwMoney", money);


        ThrowMoney throwMoney = throwMoneyRepository.fetchByTokenAndUser(throwMoneyDTO.getToken(), user).get();

        assertThat(throwMoney)
                .hasFieldOrPropertyWithValue("user.id", userId)
                .hasFieldOrPropertyWithValue("room.id", roomId)
                .hasFieldOrPropertyWithValue("money", money);

        assertThat(throwMoney.getReceivers().size())
                .isEqualTo(size);


    }

    @Test
    @DisplayName("돈 받기 - 10000원 3명 뿌리고 1명 받기")
    public void receive_money_user2_room1() {
        int money = 10000;
        int size = 3;
        long userId = 1L;
        long receiveUserId = 2L;
        String roomId = "room-00001";

        User user = userRepository.findById(userId).get();
        User receiveUser = userRepository.findById(receiveUserId).get();
        Room room = roomRepository.findById(roomId).get();


        // 1번이 돈뿌리기
        ThrowMoneyForm form = ThrowMoneyForm.builder()
                .user(user)
                .room(room)
                .money(money)
                .size(size)
                .build();

        ThrowMoneyDTO throwMoneyDTO = moneyService.throwMoney(form);

        // 2번이 돈 받기
        TokenForm receiveForm = TokenForm.builder()
                .user(receiveUser)
                .room(room)
                .token(throwMoneyDTO.getToken())
                .build();
        
        ReceiveMoneyDTO receiveMoneyDTO = moneyService.receiveMoney(receiveForm);
        log.info("# {}", receiveMoneyDTO);

        // 뿌린돈 조회
        TokenForm throwUserForm = TokenForm.builder()
                .user(user)
                .room(room)
                .token(throwMoneyDTO.getToken())
                .build();

        ThrowMoneyDTO getThrowMoney = moneyService.getThrowMoney(throwUserForm);
        log.info("# getThrowMoney : {}", getThrowMoney);

    }


}