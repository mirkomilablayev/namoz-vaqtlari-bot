package com.example.namoz_vaqtlari_bot.entity;


import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "Currency_entities")
public class CurrencyEntity {
    @Id
    private String ccyName;
    private String currencyRate;
    private String currencyDate;
}
