package com.example.namoz_vaqtlari_bot.entity;

import lombok.*;

import javax.persistence.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Column(unique = true)
    private String chatId;
    private Boolean isAdmin = false;
    private Long regionId;
    private Boolean isSendRegionList = false;
}
