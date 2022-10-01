package com.example.namoz_vaqtlari_bot.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

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
    private String chatId;
    private String realId;
    private String fullName;
    private Boolean isActive = true;
    @ManyToMany
    private List<UserRole> role;
    private Boolean isOnUserPage = true;
    private String stepOn;
    private int stepOnNumber;
}
