package com.d2vfactory.throwmoney.controller;

import com.d2vfactory.throwmoney.domain.money.ReceiveMoney;
import com.d2vfactory.throwmoney.domain.money.ReceiveMoneyDTO;
import com.d2vfactory.throwmoney.domain.money.ThrowMoneyDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ThrowMoneyControllerTest {

    private static final String HEADER_USER_ID = "X-USER-ID";
    private static final String HEADER_ROOM_ID = "X-ROOM-ID";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Test
    void throwMoney() throws Exception {
        // 1번이 1번방에 뿌림
        mockMvc.perform(
                post("/api/throwMoney")
                        .header(HEADER_USER_ID, 1)
                        .header(HEADER_ROOM_ID, "room-00001")
                        .param("size", "3")
                        .param("money", "10000"))
                .andDo(print())
                .andExpect(status().isOk());


    }

    @Test
    void throwMoney_user1_room3() throws Exception {
        // 1번이 3번방에 뿌림 : RequiredHeaderException => 1번은 3번방에 없음
        mockMvc.perform(
                post("/api/throwMoney")
                        .header(HEADER_USER_ID, 1)
                        .header(HEADER_ROOM_ID, "room-00003")
                        .param("size", "3")
                        .param("money", "10000"))
                .andDo(print())
                .andExpect(status().is(400));
    }

    @Test
    void receiveMoney() throws Exception {
        // 1번이 1번 방에 뿌리기함
        MvcResult throwResult = mockMvc.perform(
                post("/api/throwMoney")
                        .header(HEADER_USER_ID, 1)
                        .header(HEADER_ROOM_ID, "room-00001")
                        .param("size", "3")
                        .param("money", "10000"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = throwResult.getResponse().getContentAsString();
        ThrowMoneyDTO throwMoneyDTO = objectMapper.readValue(content, ThrowMoneyDTO.class);


        // 2번이 뿌린돈 받음
        mockMvc.perform(
                post("/api/receiveMoney")
                        .header(HEADER_USER_ID, 2)
                        .header(HEADER_ROOM_ID, "room-00001")
                        .param("token", throwMoneyDTO.getToken()))
                .andDo(print());

    }

    @Test
    void receiveMoney_뿌린사람이받기() throws Exception {
        // 1번이 1번 방에 뿌리기함
        MvcResult throwResult = mockMvc.perform(
                post("/api/throwMoney")
                        .header(HEADER_USER_ID, 1)
                        .header(HEADER_ROOM_ID, "room-00001")
                        .param("size", "3")
                        .param("money", "10000"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = throwResult.getResponse().getContentAsString();
        ThrowMoneyDTO throwMoneyDTO = objectMapper.readValue(content, ThrowMoneyDTO.class);


        // 1번이 뿌린돈 받을라고 함
        mockMvc.perform(
                post("/api/receiveMoney")
                        .header(HEADER_USER_ID, 1)
                        .header(HEADER_ROOM_ID, "room-00001")
                        .param("token", throwMoneyDTO.getToken()))
                .andDo(print());

    }

    @Test
    void getThrowMoney() throws Exception {
        // 1번이 1번 방에 뿌리기함
        MvcResult throwResult = mockMvc.perform(
                post("/api/throwMoney")
                        .header(HEADER_USER_ID, 1)
                        .header(HEADER_ROOM_ID, "room-00001")
                        .param("size", "3")
                        .param("money", "10000"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = throwResult.getResponse().getContentAsString();
        ThrowMoneyDTO throwMoneyDTO = objectMapper.readValue(content, ThrowMoneyDTO.class);

        // 1번이 조회
        mockMvc.perform(
                get("/api/throwMoney")
                        .header(HEADER_USER_ID, 1)
                        .header(HEADER_ROOM_ID, "room-00001")
                        .param("token", throwMoneyDTO.getToken()))
                .andDo(print());
    }


}