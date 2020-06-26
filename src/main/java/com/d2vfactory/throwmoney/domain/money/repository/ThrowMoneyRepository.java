package com.d2vfactory.throwmoney.domain.money.repository;

import com.d2vfactory.throwmoney.domain.money.ThrowMoney;
import com.d2vfactory.throwmoney.domain.user.Room;
import com.d2vfactory.throwmoney.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ThrowMoneyRepository extends JpaRepository<ThrowMoney, Long> {


    @Query("select distinct t from ThrowMoney t left join fetch t.receivers where t.token = ?1 and t.user = ?2 order by t.id")
    Optional<ThrowMoney> fetchByTokenAndUser(String token, User user);

    @Query("select distinct t from ThrowMoney t left join fetch t.receivers where t.token = ?1 and t.room = ?2 order by t.id")
    Optional<ThrowMoney> fetchByTokenAndRoom(String token, Room room);


}
