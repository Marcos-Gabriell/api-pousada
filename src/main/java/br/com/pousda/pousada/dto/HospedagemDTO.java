package br.com.pousda.pousada.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HospedagemDTO {

    private String nome;
    private String cpf;
    private LocalDate dataSaida;
    private Double valorDiaria;
    private Double valorTotal;
    private String formaPagamento;
    private String observacoes;
    private String numeroQuarto;
}
