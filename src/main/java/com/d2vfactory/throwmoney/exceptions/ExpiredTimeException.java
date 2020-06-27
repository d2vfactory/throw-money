package com.d2vfactory.throwmoney.exceptions;

public class ExpiredTimeException extends RuntimeThrowMoneyException {

    public ExpiredTimeException() {
        super("유효한 시간이 초과되었습니다.");
    }


}
