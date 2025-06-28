package br.com.pousda.pousada.repository;

import br.com.pousda.pousada.model.Recebimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecebimentoRepository extends JpaRepository<Recebimento, Long> {
    List<Recebimento> findByDataBetween(LocalDate inicio, LocalDate fim);
}
