package com.d2vfactory.throwmoney.domain.money;

import com.d2vfactory.throwmoney.domain.user.Room;
import com.d2vfactory.throwmoney.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.servlet.http.HttpServletRequest;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class TokenForm {

    private User user;
    private Room room;
    private String token;

    public TokenForm(HttpServletRequest request, String token) {
        this.user = (User) request.getAttribute("user");
        this.room = (Room) request.getAttribute("room");
        this.token = token;
    }
}
