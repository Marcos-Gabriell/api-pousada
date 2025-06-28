package br.com.pousda.pousada.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardRelatorioDTO {

    private Map<String, Long> totalPorTipo;
    private List<ReceitaPorDiaDTO> receitaPorDia;
    private Long hospedesAtivos;
    private Long hospedesInativos;


}
