package com.example.namoz_vaqtlari_bot.dto.getChatMember;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TelegramUser {
    private Long id;
    private Boolean is_bot;
    private String first_name;
    private String last_name;
    private String language_code;
}
