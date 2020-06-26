package com.d2vfactory.throwmoney.domain.money;

import com.d2vfactory.throwmoney.domain.money.repository.ReceiveMoneyRepository;
import com.d2vfactory.throwmoney.domain.money.repository.ThrowMoneyRepository;
import com.d2vfactory.throwmoney.domain.token.repository.TokenRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class MoneyService {

    private final ThrowMoneyRepository throwMoneyRepository;
    private final ReceiveMoneyRepository receiveMoneyRepository;
    private final TokenRepository tokenRepository;

    public MoneyService(ThrowMoneyRepository throwMoneyRepository, ReceiveMoneyRepository receiveMoneyRepository, TokenRepository tokenRepository) {
        this.throwMoneyRepository = throwMoneyRepository;
        this.receiveMoneyRepository = receiveMoneyRepository;
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    public ThrowMoneyDTO doThrowMoney(ThrowMoneyForm throwMoneyForm) {
        ThrowMoney throwMoney = new ThrowMoney(throwMoneyForm);

        // 원하는 수 만큼 정해진 돈 뿌리기
        List<Integer> moneyList = divideMoney(throwMoneyForm.getSize(), throwMoneyForm.getMoney());
        for (int i = 0; i < moneyList.size(); i++)
            throwMoney.getReceivers().add(new ReceiveMoney(throwMoney, moneyList.get(i)));

        // 토큰 값과 매핑할 ID를 가져오기 위해 일단 영속성 저장.
        throwMoneyRepository.save(throwMoney);

        // DB에 저장해둔 랜덤 토큰을 throw money id 에 맞춰서 가져온다.
        String token = tokenRepository.findById(throwMoney.getId())
                .orElseThrow(RuntimeException::new)
                .getToken();

        throwMoney.setToken(token);

        throwMoneyRepository.save(throwMoney);

        return new ThrowMoneyDTO(throwMoney);
    }


    private List<Integer> divideMoney(int size, int money) {
        List<Integer> moneyList = new ArrayList<>();

        if (money < size)
            throw new RuntimeException();

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


