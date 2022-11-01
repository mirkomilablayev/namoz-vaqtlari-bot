package com.example.namoz_vaqtlari_bot.repository;

import com.example.namoz_vaqtlari_bot.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegionRepo extends JpaRepository<Region, Long> {
    Optional<Region> findByName(String name);

}
