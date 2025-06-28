package br.com.pousda.pousada.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FechamentoCaixaDTO {

    private LocalDate inicioPeriodo;
    private LocalDate fimPeriodo;
    private Double caixaAnterior;
    private Double totalRecebimentos;
    private Double totalSaidas;
    private Double saldoFinal;
    private boolean mensal;
}
