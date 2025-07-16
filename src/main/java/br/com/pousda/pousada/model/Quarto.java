package br.com.pousda.pousada.model;

import br.com.pousda.pousada.model.enums.StatusQuarto;
import br.com.pousda.pousada.model.enums.TipoQuarto;
import jdk.jfr.Enabled;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class    Quarto {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numero;
    private String nome;
    private Double valorDiaria;

    @Enumerated(EnumType.STRING)
    private TipoQuarto tipo;

    @Enumerated(EnumType.STRING)
    private StatusQuarto status;
}
