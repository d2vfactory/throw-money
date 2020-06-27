package com.d2vfactory.throwmoney.exceptions;

public class NotEnoughMoneyException extends RuntimeThrowMoneyException {

    public NotEnoughMoneyException() {
        super("뿌리는 돈이 지급 대상자보다 적습니다.");
    }


}
