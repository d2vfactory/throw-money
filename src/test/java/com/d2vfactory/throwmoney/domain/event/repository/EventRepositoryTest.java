package com.d2vfactory.throwmoney.domain.event.repository;

import com.d2vfactory.throwmoney.domain.event.Event;
import com.d2vfactory.throwmoney.domain.event.EventStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Test
    void event_throwMoney() {
        Event event = new Event();
        event.setUserId(1L);
        event.setRoomId("room-00001");
        event.setMoney(10000);
        event.setThrowMoneyId(1L);
        event.setEventStatus(EventStatus.THROW_MONEY);

        eventRepository.save(event);

        log.info("# event : {}", eventRepository.findAll());


    }
}