package com.example.namoz_vaqtlari_bot.entity;

import lombok.*;

import javax.persistence.*;

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
    private boolean ok;
    @ManyToOne
    private ConChannel result;
}
