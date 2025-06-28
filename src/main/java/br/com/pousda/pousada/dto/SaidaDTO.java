package br.com.pousda.pousada.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;


@Data
@Getter
@Setter
public class SaidaDTO {

    private LocalDate data;

    @NotNull(message = "O valor da saída é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor da saída deve ser maior que zero.")
    private Double valor;

    @NotBlank(message = "A descrição da saída é obrigatória.")
    private String motivo;
}
