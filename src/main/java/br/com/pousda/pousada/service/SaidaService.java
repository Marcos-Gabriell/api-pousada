package br.com.pousda.pousada.service;

import br.com.pousda.pousada.dto.SaidaDTO;
import br.com.pousda.pousada.exception.SaidaNaoEncontradaException;
import br.com.pousda.pousada.exception.ValorInvalidoException;
import br.com.pousda.pousada.model.Saida;
import br.com.pousda.pousada.repository.SaidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SaidaService {

    @Autowired
    private SaidaRepository saidaRepository;

    public Saida salvarDTO(SaidaDTO dto) {
        if (dto.getValor() == null || dto.getValor() <= 0) {
            throw new ValorInvalidoException("O valor da saída deve ser maior que zero.");
        }

        if (dto.getMotivo() == null || dto.getMotivo().isBlank()) {
            throw new ValorInvalidoException("O motivo da saída deve ser informado.");
        }

        Saida saida = new Saida();
        saida.setData(LocalDate.now());
        saida.setValor(dto.getValor());
        saida.setMotivo(dto.getMotivo());

        return saidaRepository.save(saida);
    }

    public List<Saida> listarTodas() {
        return saidaRepository.findAll();
    }

    public Saida atualizar(Long id, Saida novaSaida) {
        Saida existente = saidaRepository.findById(id)
                .orElseThrow(() -> new SaidaNaoEncontradaException(id));

        if (novaSaida.getValor() == null || novaSaida.getValor() <= 0) {
            throw new ValorInvalidoException("O valor da saída deve ser maior que zero.");
        }

        if (novaSaida.getMotivo() == null || novaSaida.getMotivo().isBlank()) {
            throw new ValorInvalidoException("O motivo da saída deve ser informado.");
        }

        existente.setMotivo(novaSaida.getMotivo());
        existente.setValor(novaSaida.getValor());
        existente.setData(novaSaida.getData() != null ? novaSaida.getData() : existente.getData());

        return saidaRepository.save(existente);
    }

    public void excluir(Long id) {
        if (!saidaRepository.existsById(id)) {
            throw new SaidaNaoEncontradaException(id);
        }
        saidaRepository.deleteById(id);
    }

    public List<Saida> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return saidaRepository.findByDataBetween(dataInicio, dataFim);
    }

    public Double somarSaidasPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return saidaRepository.findByDataBetween(dataInicio, dataFim).stream()
                .mapToDouble(Saida::getValor)
                .sum();
    }
}
