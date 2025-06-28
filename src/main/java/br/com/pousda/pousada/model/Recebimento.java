package br.com.pousda.pousada.model;

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
public class Recebimento {

    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    private Long id;

    private LocalDate data;

    private Double valor;

    private String formaPagamento;

    private String observacoes;

    @ManyToOne
    private Hospedagem hospedagem;
}
