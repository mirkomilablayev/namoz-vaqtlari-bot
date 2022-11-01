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
@EqualsAndHashCode
@Entity
public class ConChannel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long entity_id;
    private String id;
    private String title;
    private String username;
    private String type;
    private String invite_link;
}
