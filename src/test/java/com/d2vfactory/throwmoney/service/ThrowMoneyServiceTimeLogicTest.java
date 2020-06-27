package com.d2vfactory.throwmoney.service;

import com.d2vfactory.throwmoney.domain.money.*;
import com.d2vfactory.throwmoney.domain.money.repository.ReceiveMoneyRepository;
import com.d2vfactory.throwmoney.domain.money.repository.ThrowMoneyRepository;
import com.d2vfactory.throwmoney.domain.user.Room;
import com.d2vfactory.throwmoney.domain.user.User;
import com.d2vfactory.throwmoney.domain.user.repository.RoomRepository;
import com.d2vfactory.throwmoney.domain.user.repository.UserRepository;
import com.d2vfactory.throwmoney.exceptions.EndReceiveMoneyException;
import com.d2vfactory.throwmoney.exceptions.ExpiredTimeException;
import com.d2vfactory.throwmoney.exceptions.NotFoundThrowMoneyException;
import com.d2vfactory.throwmoney.exceptions.NotTargetUserException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
class ThrowMoneyServiceTimeLogicTest {

    @Autowired
    private ThrowMoneyService throwMoneyService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ThrowMoneyRepository throwMoneyRepository;

    @Autowired
    private ReceiveMoneyRepository receiveMoneyRepository;

    @Test
    @DisplayName("뿌리기 조회하기 exception - 7일 이후 조회하기 : NotFoundThrowMoneyException")
    public void getThrowMoney_after7Days_NotFoundThrowMoneyException() {
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

        // 1번이 7일 지나서 조회하기
        TokenForm tokenForm = TokenForm.builder()
                .user(user)
                .room(room)
                .token(throwMoneyDTO.getToken())
                .build();

        // 7일 이후 조회하는것으로 세팅 (지금 시간보다 7일 미래)
        LocalDateTime testDateTime = LocalDateTime.now().plusDays(7);

        assertThatExceptionOfType(NotFoundThrowMoneyException.class)
                .isThrownBy(() -> getThrowMoney(tokenForm, testDateTime))
                .withMessage("뿌리기 정보가 없습니다.");

    }

    @Test
    @DisplayName("돈 받기 exception - 10000원 3명 뿌리고 1명 10분 지나서 받기 : ExpiredTimeException")
    public void receiveMoney_after10Minutes_NotFoundThrowMoneyException() {
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

        // 10분 이후 받는것으로 세팅 (지금 시간보다 10분 미래)
        LocalDateTime testDateTime = LocalDateTime.now().plusMinutes(10);

        assertThatExceptionOfType(ExpiredTimeException.class)
                .isThrownBy(() -> receiveMoney(receiveForm, testDateTime))
                .withMessage("유효한 시간이 초과되었습니다.");

    }

    private ThrowMoneyDTO getThrowMoney(TokenForm tokenForm, LocalDateTime testDateTime) {
        // token과 User 정보로 throwMoney 조회
        ThrowMoney throwMoney = throwMoneyRepository.fetchByTokenAndUser(tokenForm.getToken(), tokenForm.getUser())
                .filter(x -> x.getCreateDate().plusDays(7).isAfter(testDateTime))
                .orElseThrow(NotFoundThrowMoneyException::new);

        return new ThrowMoneyDTO(throwMoney);

    }


    private ReceiveMoneyDTO receiveMoney(TokenForm tokenForm, LocalDateTime testDateTime) {
        // token 과 room 정보로 throwMoney 정보 조회
        ThrowMoney throwMoney = throwMoneyRepository.fetchByTokenAndRoom(tokenForm.getToken(), tokenForm.getRoom())
                .orElseThrow(NotFoundThrowMoneyException::new);

        // 받을 수 있는 상태인지 체크 (뿌린사람 / 10분이내 / 중복지급 제외)
        validateReceive(tokenForm, throwMoney, testDateTime);

        // 선점 처리
        // select for update
        List<ReceiveMoney> receiveList = receiveMoneyRepository.findAllByThrowMoney(throwMoney);

        // 비어 있는 것 중에 first 정보 조회
        ReceiveMoney anyReceiveMoney = receiveList.stream()
                .filter(x -> x.getUser() == null)
                .findFirst()
                .orElseThrow(RuntimeException::new);

        anyReceiveMoney.setUser(tokenForm.getUser());
        receiveMoneyRepository.save(anyReceiveMoney);

        return new ReceiveMoneyDTO(anyReceiveMoney);
    }

    private void validateReceive(TokenForm tokenForm, ThrowMoney throwMoney, LocalDateTime testDateTime) {
        // 돈뿌린 사람이 받으려고 하면
        if (tokenForm.getUser().equals(throwMoney.getUser()))
            throw new NotTargetUserException();

        List<ReceiveMoney> receivers = throwMoney.getReceivers();

        // 선착순 완료 확인
        receivers.stream()
                .filter(x -> x.getUser() == null)
                .findAny()
                .ifPresentOrElse(
                        // 아직 할당 되지 않은 쀠기 돈이 있으면.
                        r -> {
                            // 10분 지났으면..
                            if (r.getCreateDate().plusMinutes(10).isBefore(testDateTime))
                                throw new ExpiredTimeException();
                        },
                        // 모두 할당 되었으면.
                        () -> {
                            throw new EndReceiveMoneyException();
                        }
                );

        // 받은 이력있으면.
        receivers.stream()
                .filter(x -> tokenForm.getUser().equals(x.getUser()))
                .findAny()
                .ifPresent(x -> {
                    throw new NotTargetUserException();
                });

    }
}