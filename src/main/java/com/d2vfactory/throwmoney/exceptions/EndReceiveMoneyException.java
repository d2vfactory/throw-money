package com.d2vfactory.throwmoney.exceptions;

public class EndReceiveMoneyException extends RuntimeThrowMoneyException {

    public EndReceiveMoneyException() {
        super("뿌린 금액이 모두 마감되었습니다.");
    }


}
