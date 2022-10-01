package com.example.namoz_vaqtlari_bot.dto.getChatMember;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Result {
    private  TelegramUser user;
    private String status;
    private Boolean is_anonymous;
}
