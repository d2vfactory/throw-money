package com.d2vfactory.throwmoney.exceptions;

public class RequiredHeaderException extends RuntimeThrowMoneyException {

    public RequiredHeaderException() {
        super("필수 헤더값이 없습니다.");
    }


}
