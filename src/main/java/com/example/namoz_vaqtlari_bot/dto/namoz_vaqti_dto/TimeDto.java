package com.example.namoz_vaqtlari_bot.dto.namoz_vaqti_dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class TimeDto {
    private String tong_saharlik;
    private String quyosh;
    private String peshin;
    private String asr;
    private String shom_iftor;
    private String hufton;
}
