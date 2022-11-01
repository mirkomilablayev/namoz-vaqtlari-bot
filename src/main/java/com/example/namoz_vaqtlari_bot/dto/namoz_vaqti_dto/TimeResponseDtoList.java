package com.example.namoz_vaqtlari_bot.dto.namoz_vaqti_dto;

import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class TimeResponseDtoList {
    private List<TimeResponseDto> responseDtos;
}
