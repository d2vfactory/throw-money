package com.d2vfactory.throwmoney.controller;

import com.d2vfactory.throwmoney.domain.money.*;
import com.d2vfactory.throwmoney.service.ThrowMoneyService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequestMapping(value = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@RestController
public class ThrowMoneyController {

    private final ThrowMoneyService throwMoneyService;

    public ThrowMoneyController(ThrowMoneyService throwMoneyService) {
        this.throwMoneyService = throwMoneyService;
    }

    // 뿌린돈 조회
    @GetMapping("/throwMoney")
    public ThrowMoneyDTO getThrowMoney(HttpServletRequest request, String token) {
        TokenForm form = new TokenForm(request, token);
        return throwMoneyService.getThrowMoney(form);
    }

    // 돈 뿌리기
    @PostMapping("/throwMoney")
    public ThrowMoneyDTO throwMoney(HttpServletRequest request, int size, int money) {
        ThrowMoneyForm form = new ThrowMoneyForm(request, size, money);
        return throwMoneyService.throwMoney(form);
    }

    // 뿌린돈 받기
    @PostMapping("/receiveMoney")
    public ReceiveMoneyDTO receiveMoney(HttpServletRequest request, String token) {
        TokenForm form = new TokenForm(request, token);
        return throwMoneyService.receiveMoney(form);
    }
}
