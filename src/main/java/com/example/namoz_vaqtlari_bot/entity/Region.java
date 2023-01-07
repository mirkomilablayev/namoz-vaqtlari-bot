package com.example.namoz_vaqtlari_bot.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "regions")
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String regionName;
    private String regionShowName;

    public Region(String regionName, String regionShowName) {
        this.regionName = regionName;
        this.regionShowName = regionShowName;
    }
}
