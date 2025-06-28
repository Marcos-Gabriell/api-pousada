package br.com.pousda.pousada.repository;

import br.com.pousda.pousada.model.Saida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SaidaRepository extends JpaRepository<Saida, Long> {
    List<Saida> findByDataBetween(LocalDate inicio, LocalDate fim);
}
