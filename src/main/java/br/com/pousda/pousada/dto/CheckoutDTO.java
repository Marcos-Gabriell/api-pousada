package br.com.pousda.pousada.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutDTO {

    private String numeroQuarto;
    private String descricao;

}
