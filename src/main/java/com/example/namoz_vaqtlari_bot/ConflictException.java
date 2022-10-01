package com.example.namoz_vaqtlari_bot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ConflictException extends RuntimeException{
    private String message;
}
