package br.com.pousda.pousada.repository;

import br.com.pousda.pousada.model.Quarto;
import br.com.pousda.pousada.model.enums.StatusQuarto;
import br.com.pousda.pousada.model.enums.TipoQuarto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuartoRepository extends JpaRepository<Quarto, Long> {

    Optional<Quarto> findByNumero(String numero);

    boolean existsByNumero(String numero);
    List<Quarto> findByStatus(StatusQuarto status);
    List<Quarto> findByTipo(TipoQuarto tipo);
    List<Quarto> findByNumeroContainingIgnoreCaseOrNomeContainingIgnoreCase(String numero, String nome);

    // Para buscas combinadas (se quiser mais avançado, pode usar @Query)
    List<Quarto> findByStatusAndTipo(StatusQuarto status, TipoQuarto tipo);

    List<Quarto> findByStatusAndNomeContainingIgnoreCaseOrNumeroContainingIgnoreCase(StatusQuarto status, String nome, String numero);
    List<Quarto> findByTipoAndNomeContainingIgnoreCaseOrNumeroContainingIgnoreCase(TipoQuarto tipo, String nome, String numero);

    List<Quarto> findByStatusAndTipoAndNomeContainingIgnoreCaseOrNumeroContainingIgnoreCase(StatusQuarto status, TipoQuarto tipo, String nome, String numero);

}
