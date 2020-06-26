package com.d2vfactory.throwmoney.domain.user;

import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "USER_ROOM")
@ToString
public class UserRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    @NotFound(action = NotFoundAction.IGNORE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOM_ID")
    @NotFound(action = NotFoundAction.IGNORE)
    private Room room;

    public UserRoom(User user, Room room) {
        this.user = user;
        this.room = room;
    }
}
