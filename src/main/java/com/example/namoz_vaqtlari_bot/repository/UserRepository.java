package com.example.namoz_vaqtlari_bot.repository;

import com.example.namoz_vaqtlari_bot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByChatIdAndIsActive(String chatId, Boolean isActive);
}
