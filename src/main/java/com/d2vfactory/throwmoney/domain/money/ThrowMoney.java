package com.d2vfactory.throwmoney.domain.money;

import com.d2vfactory.throwmoney.domain.user.Room;
import com.d2vfactory.throwmoney.domain.user.User;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "THROW_MONEY")
@ToString
public class ThrowMoney  extends TimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "THROW_ID")
    private Long id;

    private String token;

    @OneToOne
    private User user;

    @OneToOne
    private Room room;

    private int money;

    @OneToMany(
            mappedBy = "throwMoney",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ReceiveMoney> receivers = new ArrayList<>();

    public ThrowMoney(ThrowMoneyForm form) {
        this.user = form.getUser();
        this.room = form.getRoom();
        this.money = form.getMoney();
    }
}
