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

        ThrowMoneyForm form = new ThrowMoneyForm();
        form.setUser(user);
        form.setRoom(room);
        form.setMoney(money);
        form.setSize(size);

        ThrowMoneyDTO throwMoneyDTO = moneyService.throwMoney(form);

        assertThat(throwMoneyDTO)
                .hasFieldOrPropertyWithValue("userId", userId)
                .hasFieldOrPropertyWithValue("roomId", roomId)
                .hasFieldOrPropertyWithValue("throwMoney", money)
                .hasFieldOrPropertyWithValue("size", size);


        ThrowMoney throwMoney = throwMoneyRepository.fetchByTokenAndUser(throwMoneyDTO.getToken(), user).get();

        assertThat(throwMoney)
                .hasFieldOrPropertyWithValue("user.id", userId)
                .hasFieldOrPropertyWithValue("room.id", roomId)
                .hasFieldOrPropertyWithValue("money", money);

        assertThat(throwMoney.getReceivers().size())
                .isEqualTo(size);


    }

    @Test
    public void receive_money_user2_room1(){
        int money = 10000;
        int size = 3;
        long userId = 1L;
        long receiveUserId = 2L;
        String roomId = "room-00001";

        User user = userRepository.findById(userId).get();
        User receiveUser = userRepository.findById(receiveUserId).get();
        Room room = roomRepository.findById(roomId).get();


        // 1번이 돈뿌리기
        ThrowMoneyForm form = new ThrowMoneyForm();
        form.setUser(user);
        form.setRoom(room);
        form.setMoney(money);
        form.setSize(size);

        ThrowMoneyDTO throwMoneyDTO = moneyService.throwMoney(form);

        // 2번이 돈 받기
        ReceiveMoneyForm receiveMoneyForm = new ReceiveMoneyForm();
        receiveMoneyForm.setUser(receiveUser);
        receiveMoneyForm.setRoom(room);
        receiveMoneyForm.setToken(throwMoneyDTO.getToken());

        ReceiveMoneyDTO receiveMoneyDTO = moneyService.receiveMoneyDTO(receiveMoneyForm);
        log.info("# {}", receiveMoneyDTO);

        ThrowMoney throwMoney = throwMoneyRepository.fetchByTokenAndUser(throwMoneyDTO.getToken(), user).get();
        log.info("# {}", throwMoney);

    }

    
}