package com.example.namoz_vaqtlari_bot.repository;

import com.example.namoz_vaqtlari_bot.entity.User;
import com.example.namoz_vaqtlari_bot.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<UserRole, Long> {
    Optional<UserRole> findByRoleName(String roleName);
}
