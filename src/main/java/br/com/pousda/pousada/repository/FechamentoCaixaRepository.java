package br.com.pousda.pousada.repository;

import br.com.pousda.pousada.model.FechamentoCaixa;
import br.com.pousda.pousada.model.enums.TipoFechamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FechamentoCaixaRepository extends JpaRepository<FechamentoCaixa, Long> {

    List<FechamentoCaixa> findByTipo(TipoFechamento tipo);
    List<FechamentoCaixa> findByInicioPeriodoBetween(LocalDate inicio, LocalDate fim);

}
