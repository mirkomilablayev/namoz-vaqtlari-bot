package com.example.namoz_vaqtlari_bot.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CompulsoryChannel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String channel_username;
    private String channel_name;

    public CompulsoryChannel(String channel_name, String channel_username){
        this.channel_username = channel_username;
        this.channel_name = channel_name;
    }
}
