package br.com.pousda.pousada.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AuthRequest {

    private String username;
    private String password;
}
