package com.d2vfactory.throwmoney.domain.money;

import com.d2vfactory.throwmoney.domain.user.Room;
import com.d2vfactory.throwmoney.domain.user.User;
import lombok.Data;

@Data
public class ReceiveMoneyForm {

    private User user;
    private Room room;
    private String token;
}
