package com.d2vfactory.throwmoney.domain.money.repository;

import com.d2vfactory.throwmoney.domain.money.ReceiveMoney;
import com.d2vfactory.throwmoney.domain.money.ThrowMoney;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;

public interface ReceiveMoneyRepository extends JpaRepository<ReceiveMoney, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "2")})
    List<ReceiveMoney> findAllByThrowMoney(ThrowMoney throwMoney);
}
