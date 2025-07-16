package br.com.pousda.pousada.service;

import br.com.pousda.pousada.model.Quarto;
import br.com.pousda.pousada.model.enums.StatusQuarto;
import br.com.pousda.pousada.model.enums.TipoQuarto;
import br.com.pousda.pousada.repository.QuartoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuartoService {

    @Autowired
    private QuartoRepository quartoRepository;

    public List<Quarto> listarTodos() {
        return quartoRepository.findAll();
    }

    public Optional<Quarto> buscarPorId(Long id) {
        return quartoRepository.findById(id);
    }

    public List<Quarto> listarPorStatus(StatusQuarto status) {
        return quartoRepository.findByStatus(status);
    }

    public List<Quarto> listarPorTipo(TipoQuarto tipo) {
        return quartoRepository.findByTipo(tipo);
    }

    public List<Quarto> buscarPorNumeroOuNome(String termo) {
        return quartoRepository.findByNumeroContainingIgnoreCaseOrNomeContainingIgnoreCase(termo, termo);
    }

    public List<Quarto> filtrar(StatusQuarto status, TipoQuarto tipo, String termo) {
        if (status != null && tipo != null && termo != null && !termo.isEmpty()) {
            return quartoRepository.findByStatusAndTipoAndNomeContainingIgnoreCaseOrNumeroContainingIgnoreCase(
                    status, tipo, termo, termo);
        } else if (status != null && tipo != null) {
            return quartoRepository.findByStatusAndTipo(status, tipo);
        } else if (status != null && termo != null && !termo.isEmpty()) {
            return quartoRepository.findByStatusAndNomeContainingIgnoreCaseOrNumeroContainingIgnoreCase(
                    status, termo, termo);
        } else if (tipo != null && termo != null && !termo.isEmpty()) {
            return quartoRepository.findByTipoAndNomeContainingIgnoreCaseOrNumeroContainingIgnoreCase(
                    tipo, termo, termo);
        } else if (status != null) {
            return quartoRepository.findByStatus(status);
        } else if (tipo != null) {
            return quartoRepository.findByTipo(tipo);
        } else if (termo != null && !termo.isEmpty()) {
            return quartoRepository.findByNumeroContainingIgnoreCaseOrNomeContainingIgnoreCase(termo, termo);
        } else {
            return quartoRepository.findAll();
        }
    }

    public Quarto criar(Quarto quarto) {
        if (quartoRepository.existsByNumero(quarto.getNumero())) {
            throw new IllegalArgumentException("Já existe um quarto com este número");
        }
        quarto.setStatus(StatusQuarto.DISPONIVEL);
        return quartoRepository.save(quarto);
    }

    public Quarto editar(Long id, Quarto atualizado) {
        Quarto existente = quartoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quarto não encontrado"));

        // Não pode alterar o status para OCUPADO via endpoint de quartos!
        if (atualizado.getStatus() == StatusQuarto.OCUPADO) {
            throw new IllegalStateException("Não é permitido definir o status OCUPADO pelo gerenciamento de quartos.");
        }

        // Só pode editar se NÃO está OCUPADO
        if (existente.getStatus() == StatusQuarto.OCUPADO) {
            throw new IllegalStateException("Não é possível editar um quarto ocupado.");
        }

        // Só pode colocar em manutenção se está DISPONIVEL
        if (atualizado.getStatus() == StatusQuarto.MANUTENCAO
                && existente.getStatus() != StatusQuarto.DISPONIVEL) {
            throw new IllegalStateException("Só é possível colocar em manutenção um quarto disponível.");
        }

        existente.setNumero(atualizado.getNumero());
        existente.setNome(atualizado.getNome());
        existente.setTipo(atualizado.getTipo());
        existente.setValorDiaria(atualizado.getValorDiaria());
        existente.setStatus(atualizado.getStatus());
        return quartoRepository.save(existente);
    }



    public Quarto liberar(Long id) {
        Quarto quarto = quartoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quarto não encontrado"));

        if (quarto.getStatus() != StatusQuarto.MANUTENCAO) {
            throw new IllegalStateException("Só é possível liberar um quarto que está em manutenção.");
        }
        quarto.setStatus(StatusQuarto.DISPONIVEL);
        return quartoRepository.save(quarto);
    }

    public void excluir(Long id) {
        Quarto quarto = quartoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quarto não encontrado"));
        quartoRepository.delete(quarto);
    }


}
