package com.d2vfactory.throwmoney.exceptions;

public class NotTargetUserException extends RuntimeThrowMoneyException {

    public NotTargetUserException() {
        super("지급 받을 수 있는 대상이 아닙니다.");
    }


}
