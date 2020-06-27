package com.d2vfactory.throwmoney.config;

import com.d2vfactory.throwmoney.domain.user.Room;
import com.d2vfactory.throwmoney.domain.user.User;
import com.d2vfactory.throwmoney.domain.user.repository.RoomRepository;
import com.d2vfactory.throwmoney.domain.user.repository.UserRepository;
import com.d2vfactory.throwmoney.exceptions.RequiredHeaderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class HeaderToAttributeInterceptor extends HandlerInterceptorAdapter {

    private static final String HEADER_USER_ID = "X-USER-ID";
    private static final String HEADER_ROOM_ID = "X-ROOM-ID";

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    public HeaderToAttributeInterceptor(UserRepository userRepository, RoomRepository roomRepository) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long userId = Long.parseLong(request.getHeader(HEADER_USER_ID), 10);
        String roomId = request.getHeader(HEADER_ROOM_ID);

        User user = userRepository.findById(userId)
                .orElseThrow(RequiredHeaderException::new);

        Room room = roomRepository.findById(roomId)
                .orElseThrow(RequiredHeaderException::new);

        request.setAttribute("user", user);
        request.setAttribute("room", room);
        return true;
    }
}

