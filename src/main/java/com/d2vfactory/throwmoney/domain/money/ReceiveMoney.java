package com.d2vfactory.throwmoney.domain.money;

import com.d2vfactory.throwmoney.domain.user.User;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "RECEIVE_MONEY")
@ToString(exclude = "throwMoney")
public class ReceiveMoney extends TimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RECEIVE_ID")
    private Long id;


    @JoinColumn(name = "THROW_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private ThrowMoney throwMoney;

    @OneToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private User user;

    private int money;

    // 돈 뿌리기 할때 생성
    public ReceiveMoney(ThrowMoney throwMoney, int money) {
        this.throwMoney = throwMoney;
        this.money = money;
    }
}
