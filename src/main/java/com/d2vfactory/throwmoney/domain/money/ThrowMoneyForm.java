package com.d2vfactory.throwmoney.domain.money;

import com.d2vfactory.throwmoney.domain.user.Room;
import com.d2vfactory.throwmoney.domain.user.User;
import lombok.Data;

@Data
public class ThrowMoneyForm {

    private User user;
    private Room room;
    private int size;
    private int money;
}
