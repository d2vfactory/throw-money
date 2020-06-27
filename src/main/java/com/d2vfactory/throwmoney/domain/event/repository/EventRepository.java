package com.d2vfactory.throwmoney.domain.event.repository;

import com.d2vfactory.throwmoney.domain.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
