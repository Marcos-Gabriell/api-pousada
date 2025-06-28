package br.com.pousda.pousada.dto;


import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
public class ReceitaPorDiaDTO {
    private LocalDate data;
    private Double total;

    public ReceitaPorDiaDTO(LocalDate data, Double total) {
        this.data = data;
        this.total = total;
    }

}
