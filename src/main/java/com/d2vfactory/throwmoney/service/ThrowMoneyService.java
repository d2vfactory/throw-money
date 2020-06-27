package com.d2vfactory.throwmoney.service;

import com.d2vfactory.throwmoney.domain.money.*;
import com.d2vfactory.throwmoney.domain.money.repository.ReceiveMoneyRepository;
import com.d2vfactory.throwmoney.domain.money.repository.ThrowMoneyRepository;
import com.d2vfactory.throwmoney.domain.token.repository.TokenRepository;
import com.d2vfactory.throwmoney.exceptions.*;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class ThrowMoneyService {

    private final ThrowMoneyRepository throwMoneyRepository;
    private final ReceiveMoneyRepository receiveMoneyRepository;
    private final TokenRepository tokenRepository;

    public ThrowMoneyService(ThrowMoneyRepository throwMoneyRepository, ReceiveMoneyRepository receiveMoneyRepository, TokenRepository tokenRepository) {
        this.throwMoneyRepository = throwMoneyRepository;
        this.receiveMoneyRepository = receiveMoneyRepository;
        this.tokenRepository = tokenRepository;
    }

    // 뿌린돈 조회
    public ThrowMoneyDTO getThrowMoney(TokenForm tokenForm) {
        // token과 User 정보로 throwMoney 조회
        ThrowMoney throwMoney = throwMoneyRepository.fetchByTokenAndUser(tokenForm.getToken(), tokenForm.getUser())
                .filter(x -> x.getCreateDate().plusDays(7).isAfter(LocalDateTime.now()))
                .orElseThrow(NotFoundThrowMoneyException::new);

        return new ThrowMoneyDTO(throwMoney);

    }

    // 돈뿌리기
    @Transactional
    public ThrowMoneyDTO throwMoney(ThrowMoneyForm throwMoneyForm) {
        ThrowMoney throwMoney = new ThrowMoney(throwMoneyForm);

        // 랜덤 분배 
        List<Integer> moneyList = divideMoney(throwMoneyForm.getSize(), throwMoneyForm.getMoney());

        // 원하는 수 만큼 정해진 돈 뿌리기
        moneyList.stream()
                .map(x -> new ReceiveMoney(throwMoney, x))
                .forEach(x -> throwMoney.getReceivers().add(x));

        // 토큰 값과 매핑할 ID를 가져오기 위해 먼저 영속성 저장.
        throwMoneyRepository.save(throwMoney);

        // DB에 저장해둔 랜덤 토큰을 throwMoney id 에 맞춰서 가져온다.
        String token = getToken(throwMoney);

        throwMoney.setToken(token);

        throwMoneyRepository.save(throwMoney);

        return new ThrowMoneyDTO(throwMoney);
    }

    // 뿌린돈 받기
    @Transactional
    public ReceiveMoneyDTO receiveMoney(TokenForm tokenForm) {
        // token 과 room 정보로 throwMoney 정보 조회
        ThrowMoney throwMoney = throwMoneyRepository.fetchByTokenAndRoom(tokenForm.getToken(), tokenForm.getRoom())
                .orElseThrow(NotFoundThrowMoneyException::new);

        // 받을 수 있는 상태인지 체크 (뿌린사람 / 10분이내 / 중복지급 제외)
        validateReceive(tokenForm, throwMoney);

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

    private void validateReceive(TokenForm tokenForm, ThrowMoney throwMoney) {
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
                            if (r.getCreateDate().plusMinutes(10).isBefore(LocalDateTime.now()))
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

    private String getToken(ThrowMoney throwMoney) {
        return tokenRepository.findById(throwMoney.getId())
                .orElseThrow(AssignTokenException::new)
                .getToken();
    }

    private List<Integer> divideMoney(int size, int money) {
        List<Integer> moneyList = new ArrayList<>();

        if (money < size)
            throw new NotEnoughMoneyException();

        // 기본 분배 금액 : 사이즈 만큼 나누기
        int basicMoney = money / size;

        // 골고루 나눈다
        for (int i = 0; i < size; i++) {
            moneyList.add(basicMoney);
        }

        // 소숫점 차이로 인하여 잘라진 부분은 첫번째에 추가
        int dum = money - moneyList.stream().reduce(0, Integer::sum);
        moneyList.set(0, moneyList.get(0) + dum);

        // 최소 금액 1은 보존하고, 랜덤으로 자기금액 -1원을 빼고 다른사람에게 넣어줌
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            // random(10) -> 0~9
            // random(1) -> 0 , 해당 금액 제외해도 최소 1원은 보존
            int destinyMoney = random.nextInt(basicMoney);
            int luckyIdx = random.nextInt(size);

            moneyList.set(i, moneyList.get(i) - destinyMoney);
            moneyList.set(luckyIdx, moneyList.get(luckyIdx) + destinyMoney);
        }

        return moneyList;
    }
}


