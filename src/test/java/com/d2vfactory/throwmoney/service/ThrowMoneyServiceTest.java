package com.d2vfactory.throwmoney.service;

import com.d2vfactory.throwmoney.domain.money.*;
import com.d2vfactory.throwmoney.domain.money.repository.ThrowMoneyRepository;
import com.d2vfactory.throwmoney.domain.user.Room;
import com.d2vfactory.throwmoney.domain.user.User;
import com.d2vfactory.throwmoney.domain.user.repository.RoomRepository;
import com.d2vfactory.throwmoney.domain.user.repository.UserRepository;
import com.d2vfactory.throwmoney.exceptions.NotEnoughMoneyException;
import com.d2vfactory.throwmoney.exceptions.NotTargetUserException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
class ThrowMoneyServiceTest {

    @Autowired
    private ThrowMoneyService throwMoneyService;

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

        ThrowMoneyDTO throwMoneyDTO = throwMoneyService.throwMoney(form);

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
    public void receiveMoney_user2_room1() {
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

        ThrowMoneyDTO throwMoneyDTO = throwMoneyService.throwMoney(form);

        // 2번이 돈 받기
        TokenForm receiveForm = TokenForm.builder()
                .user(receiveUser)
                .room(room)
                .token(throwMoneyDTO.getToken())
                .build();

        ReceiveMoneyDTO receiveMoneyDTO = throwMoneyService.receiveMoney(receiveForm);
        log.info("# {}", receiveMoneyDTO);

        // 뿌린돈 조회
        TokenForm throwUserForm = TokenForm.builder()
                .user(user)
                .room(room)
                .token(throwMoneyDTO.getToken())
                .build();

        ThrowMoneyDTO getThrowMoney = throwMoneyService.getThrowMoney(throwUserForm);
        log.info("# getThrowMoney : {}", getThrowMoney);
    }

    @Test
    @DisplayName("돈 뿌리기 exception - 뿌린 돈 보다 지급대상이 많음 : NotEnoughMoneyException")
    public void throwMoney_sizeBiggerThanMoney_NotEnoughMoneyException() {
        int money = 10;
        int size = 11;
        long userId = 1L;
        String roomId = "room-00001";

        User user = userRepository.findById(userId).get();
        Room room = roomRepository.findById(roomId).get();


        // 1번이 돈뿌리기 : 11명에 10원
        ThrowMoneyForm form = ThrowMoneyForm.builder()
                .user(user)
                .room(room)
                .money(money)
                .size(size)
                .build();

        assertThatExceptionOfType(NotEnoughMoneyException.class)
                .isThrownBy(() -> throwMoneyService.throwMoney(form))
                .withMessage("뿌리는 돈이 지급 대상자보다 적습니다.");

    }

    @Test
    @DisplayName("돈 받기 exception - 뿌린 사람이 받음 : NotTargetUserException")
    public void receiveMoney_throwUserSameReceiveUser_NotTargetUserException() {
        int money = 10000;
        int size = 3;
        long userId = 1L;
        String roomId = "room-00001";

        User user = userRepository.findById(userId).get();
        Room room = roomRepository.findById(roomId).get();


        // 1번이 돈뿌리기
        ThrowMoneyForm form = ThrowMoneyForm.builder()
                .user(user)
                .room(room)
                .money(money)
                .size(size)
                .build();

        ThrowMoneyDTO throwMoneyDTO = throwMoneyService.throwMoney(form);

        // 1번이 돈 받기
        TokenForm receiveForm = TokenForm.builder()
                .user(user)
                .room(room)
                .token(throwMoneyDTO.getToken())
                .build();

        assertThatExceptionOfType(NotTargetUserException.class)
                .isThrownBy(() -> throwMoneyService.receiveMoney(receiveForm))
                .withMessage("지급 받을 수 있는 대상이 아닙니다.");

    }

    @Test
    @DisplayName("돈 받기 exception - 받은 사람이 또 받음 : NotTargetUserException")
    public void receiveMoney_sameReceiveUser_NotTargetUserException() {
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

        ThrowMoneyDTO throwMoneyDTO = throwMoneyService.throwMoney(form);

        // 2번이 돈 받기
        TokenForm receiveForm = TokenForm.builder()
                .user(receiveUser)
                .room(room)
                .token(throwMoneyDTO.getToken())
                .build();

        throwMoneyService.receiveMoney(receiveForm);

        // 2번이 또 돈 받기
        assertThatExceptionOfType(NotTargetUserException.class)
                .isThrownBy(() -> throwMoneyService.receiveMoney(receiveForm))
                .withMessage("지급 받을 수 있는 대상이 아닙니다.");

    }


}