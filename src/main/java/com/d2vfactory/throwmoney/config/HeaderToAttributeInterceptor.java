package com.d2vfactory.throwmoney.config;

import com.d2vfactory.throwmoney.domain.user.Room;
import com.d2vfactory.throwmoney.domain.user.User;
import com.d2vfactory.throwmoney.domain.user.repository.RoomRepository;
import com.d2vfactory.throwmoney.domain.user.repository.UserRepository;
import com.d2vfactory.throwmoney.domain.user.repository.UserRoomRepository;
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
    private final UserRoomRepository userRoomRepository;

    public HeaderToAttributeInterceptor(UserRepository userRepository, RoomRepository roomRepository, UserRoomRepository userRoomRepository) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.userRoomRepository = userRoomRepository;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long userId = Long.parseLong(request.getHeader(HEADER_USER_ID), 10);
        String roomId = request.getHeader(HEADER_ROOM_ID);

        User user = userRepository.findById(userId)
                .orElseThrow(RequiredHeaderException::new);

        Room room = roomRepository.findById(roomId)
                .orElseThrow(RequiredHeaderException::new);

        // 사용자 정보가 대화방에 속해 있지 않으면 예외 발생
        if (!userRoomRepository.fetchAllByRoom(room).stream().anyMatch(x -> user.equals(x.getUser())))
            throw new RequiredHeaderException();

        request.setAttribute("user", user);
        request.setAttribute("room", room);
        return true;
    }
}

