package com.example.namoz_vaqtlari_bot.dto.namoz_vaqti_dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class TimeResponseDto {
    private String region;
    private String date;
    private String weekday;
    private TimeDto times;
}
