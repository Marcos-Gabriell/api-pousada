package br.com.pousda.pousada.service;

import br.com.pousda.pousada.model.Quarto;
import br.com.pousda.pousada.repository.QuartoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuartoService {

    @Autowired
    private QuartoRepository quartoRepository;

    public List<Quarto> listarTodos() {
        return quartoRepository.findAll();
    }

    public Quarto atualizarStatus(Long id, boolean ocupado) {
        Quarto quarto = quartoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quarto não encontrado"));
        quarto.setOcupado(ocupado);
        return quartoRepository.save(quarto);
    }

    public Quarto criar(Quarto quarto) {
        if (quartoRepository.existsByNumero(quarto.getNumero())) {
            throw new IllegalArgumentException("Quarto já existe com este número");
        }
        quarto.setOcupado(false);
        return quartoRepository.save(quarto);
    }

    public Quarto editar(Long id, Quarto atualizado) {
        Quarto existente = quartoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quarto não encontrado"));
        existente.setNumero(atualizado.getNumero());
        return quartoRepository.save(existente);
    }
}