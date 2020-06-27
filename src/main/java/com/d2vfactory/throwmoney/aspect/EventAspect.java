package com.d2vfactory.throwmoney.aspect;

import com.d2vfactory.throwmoney.domain.event.Event;
import com.d2vfactory.throwmoney.domain.event.repository.EventRepository;
import com.d2vfactory.throwmoney.domain.money.ReceiveMoneyDTO;
import com.d2vfactory.throwmoney.domain.money.ThrowMoneyDTO;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class EventAspect {

    private final EventRepository eventRepository;

    public EventAspect(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @AfterReturning(pointcut = "@annotation(PublishEvent)", returning = "retVal")
    public void saveEvent(Object retVal) throws RuntimeException {
        if (retVal instanceof ThrowMoneyDTO) {
            eventRepository.save(new Event((ThrowMoneyDTO) retVal));
        } else if (retVal instanceof ReceiveMoneyDTO) {
            eventRepository.save(new Event((ReceiveMoneyDTO) retVal));
        }
    }
}
