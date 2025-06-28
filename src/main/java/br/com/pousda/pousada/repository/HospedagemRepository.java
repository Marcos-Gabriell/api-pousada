package br.com.pousda.pousada.repository;

import br.com.pousda.pousada.model.Hospedagem;
import br.com.pousda.pousada.model.Quarto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HospedagemRepository extends JpaRepository<Hospedagem, Long> {

    Optional<Hospedagem> findTopByQuartoOrderByIdDesc(Quarto quarto);
}
