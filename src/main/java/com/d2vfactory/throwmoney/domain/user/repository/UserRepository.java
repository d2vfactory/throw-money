package com.d2vfactory.throwmoney.domain.user.repository;

import com.d2vfactory.throwmoney.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
