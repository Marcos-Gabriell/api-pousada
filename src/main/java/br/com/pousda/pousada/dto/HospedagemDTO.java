package br.com.pousda.pousada.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HospedagemRequestDTO {

    private String nome;
    private String cpf;
    private LocalDate dataSaida;
    private Double valorDiaria;
    private Double valorTotal;
    private String formaPagamento;
    private String observacoes;
    private Long quartoId;
}
