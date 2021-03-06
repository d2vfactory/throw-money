package com.d2vfactory.throwmoney.domain.money;

import com.d2vfactory.throwmoney.domain.user.Room;
import com.d2vfactory.throwmoney.domain.user.User;
import lombok.*;

import javax.servlet.http.HttpServletRequest;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class ThrowMoneyForm {
    private User user;
    private Room room;
    private int size;
    private int money;

    public ThrowMoneyForm(HttpServletRequest request, int size, int money) {
        this.user = (User) request.getAttribute("user");
        this.room = (Room) request.getAttribute("room");
        this.size = size;
        this.money = money;

    }

}
