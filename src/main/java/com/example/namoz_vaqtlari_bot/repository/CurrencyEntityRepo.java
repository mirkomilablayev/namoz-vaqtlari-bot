package com.example.namoz_vaqtlari_bot.repository;

import com.example.namoz_vaqtlari_bot.entity.CurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CurrencyEntityRepo extends JpaRepository<CurrencyEntity, String> {
    Optional<CurrencyEntity> findByCcyName(String ccyName);
}
