package com.d2vfactory.throwmoney.domain.money;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ThrowMoneyDTO {

    private String token;

    private long userId;

    private String userName;

    private String roomId;

    private String roomName;

    private int throwMoney;

    private int totalReceiveMoney;

    // 돈 받은 정보만 제공
    private List<ReceiveMoneyDTO> receivers;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    public ThrowMoneyDTO(ThrowMoney throwMoney) {
        this.token = throwMoney.getToken();
        this.userId = throwMoney.getUser().getId();
        this.userName = throwMoney.getUser().getName();
        this.roomId = throwMoney.getRoom().getId();
        this.roomName = throwMoney.getRoom().getName();
        this.throwMoney = throwMoney.getMoney();
        this.createdDate = throwMoney.getCreateDate();

        // 돈 받은 정보만 제공
        this.receivers = throwMoney.getReceivers()
                .stream()
                .filter(x -> x.getUser() != null)
                .map(ReceiveMoneyDTO::new)
                .collect(Collectors.toList());

        this.totalReceiveMoney = receivers
                .stream()
                .map(ReceiveMoneyDTO::getReceiveMoney)
                .reduce(0, Integer::sum);
    }


}
