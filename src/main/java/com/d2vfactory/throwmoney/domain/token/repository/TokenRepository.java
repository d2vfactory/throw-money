package com.d2vfactory.throwmoney.domain.token.repository;

import com.d2vfactory.throwmoney.domain.token.Token;
import com.d2vfactory.throwmoney.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {
}
