package br.com.pousda.pousada.model;

import br.com.pousda.pousada.model.enums.TipoHospedagem;
import jdk.jfr.Enabled;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Hospedagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoHospedagem tipo;

    private String nome;

    private String cpf;

    private LocalDate dataEntrada;

    private LocalDate dataSaida;

    private Double valorDiaria;

    private Double valorTotal;

    private String formaPagamento;

    private String observacoes;

    @ManyToOne
    private Quarto quarto;

    @Column(nullable = false)
    private Boolean cancelada = false;

    private String motivoCancelamento;
}
