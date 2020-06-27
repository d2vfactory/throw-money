package com.d2vfactory.throwmoney.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
class ThrowMoneyControllerTest {

    private static final String HEADER_USER_ID = "X-USER-ID";
    private static final String HEADER_ROOM_ID = "X-ROOM-ID";

    @Autowired
    protected MockMvc mockMvc;

    @Test
    void throwMoney() throws Exception {
        mockMvc.perform(
                post("/api/throwMoney")
                        .header(HEADER_USER_ID, 1)
                        .header(HEADER_ROOM_ID, "room-00001")
                        .param("size", "3")
                        .param("money", "10000"))
                .andDo(print());


    }

    @Test
    void getThrowMoney() {

    }


    @Test
    void receiveMoney() {

    }
}