package com.d2vfactory.throwmoney.domain.money;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ReceiveMoneyDTO {

    private long receiveUserId;

    private String receiveRoomId;

    private String receiveUserName;

    private long throwMoneyId;
    
    private int receiveMoney;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime receiveDate;

    public ReceiveMoneyDTO(ReceiveMoney receiveMoney) {
        this.receiveUserId = receiveMoney.getUser().getId();
        this.receiveRoomId = receiveMoney.getThrowMoney().getRoom().getId();
        this.throwMoneyId = receiveMoney.getThrowMoney().getId();
        this.receiveUserName = receiveMoney.getUser().getName();
        this.receiveMoney = receiveMoney.getMoney();

        // 업데이트 날짜가 돈 받은 날짜
        this.receiveDate = receiveMoney.getUpdateDate();
    }

}
