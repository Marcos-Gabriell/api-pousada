package br.com.pousda.pousada.dto;



import br.com.pousda.pousada.model.enums.TipoHospedagem;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class HospedagemResponseDTO {
    private Long id;
    private String tipo;
    private String nome;
    private String cpf;
    private LocalDate dataEntrada;
    private LocalDate dataSaida;
    private Double valorDiaria;
    private Double valorTotal;
    private String formaPagamento;
    private String observacoes;
    private String numeroQuarto;
    private Boolean ocupado;
    private String status;
}
