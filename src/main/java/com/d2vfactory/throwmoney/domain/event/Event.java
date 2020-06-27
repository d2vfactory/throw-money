package com.d2vfactory.throwmoney.domain.event;

import com.d2vfactory.throwmoney.domain.money.ReceiveMoneyDTO;
import com.d2vfactory.throwmoney.domain.money.ThrowMoneyDTO;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "EVENT")
@ToString
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EVENT_ID")
    private Long id;

    private Long userId;

    private String roomId;

    private Long throwMoneyId;

    private int money;

    @Enumerated(value = EnumType.STRING)
    private EventStatus eventStatus;

    public Event(ThrowMoneyDTO throwMoneyDTO) {
        this.userId = throwMoneyDTO.getUserId();
        this.roomId = throwMoneyDTO.getRoomId();
        this.throwMoneyId = throwMoneyDTO.getId();
        this.money = throwMoneyDTO.getThrowMoney();
        this.eventStatus = EventStatus.THROW_MONEY;
    }

    public Event(ReceiveMoneyDTO receiveMoneyDTO) {
        this.userId = receiveMoneyDTO.getReceiveUserId();
        this.roomId = receiveMoneyDTO.getReceiveRoomId();
        this.throwMoneyId = receiveMoneyDTO.getThrowMoneyId();
        this.money = receiveMoneyDTO.getReceiveMoney();
        this.eventStatus = EventStatus.RECEIVE_MONEY;
    }

}
