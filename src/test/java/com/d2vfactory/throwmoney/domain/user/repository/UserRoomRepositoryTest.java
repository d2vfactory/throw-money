package com.d2vfactory.throwmoney.domain.user.repository;

import com.d2vfactory.throwmoney.domain.user.Room;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserRoomRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRoomRepository userRoomRepository;

    @Test
    @DisplayName("사용자/대화방 생성후 대화방3 정보 조회")
    public void create_user() {

        Room room3 = roomRepository.findById("room-00003").get();

        log.info("# {}", userRoomRepository.fetchAllByRoom(room3));


    }
}