package br.com.pousda.pousada.model;


import br.com.pousda.pousada.model.enums.TipoFechamento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

import br.com.pousda.pousada.model.enums.TipoFechamento;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class FechamentoCaixa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate inicioPeriodo;
    private LocalDate fimPeriodo;

    private Double caixaAnterior;
    private Double totalRecebimentos;
    private Double totalSaidas;
    private Double saldoFinal;

    @Enumerated(EnumType.STRING)
    private TipoFechamento tipo;

}
