package br.com.pousda.pousada.service;

import br.com.pousda.pousada.dto.CheckoutDTO;
import br.com.pousda.pousada.dto.HospedagemDTO;
import br.com.pousda.pousada.dto.HospedagemResponseDTO;
import br.com.pousda.pousada.exception.*;
import br.com.pousda.pousada.model.Hospedagem;
import br.com.pousda.pousada.model.Quarto;
import br.com.pousda.pousada.model.enums.TipoHospedagem;
import br.com.pousda.pousada.repository.HospedagemRepository;
import br.com.pousda.pousada.repository.QuartoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HospedagemService {

    @Autowired
    private HospedagemRepository hospedagemRepository;

    @Autowired
    private QuartoRepository quartoRepository;

    public Hospedagem realizarCheckin(HospedagemDTO hospedagemDTO) {

        Quarto quarto = quartoRepository.findByNumero(hospedagemDTO.getNumeroQuarto())
                .orElseThrow(() -> new QuartoNaoEncontradoException(hospedagemDTO.getNumeroQuarto()));

        if (quarto.isOcupado()) {
            throw new QuartoOcupadoException("O quarto " + quarto.getNumero() + " já está ocupado.");
        }

        LocalDate dataEntrada = LocalDate.now();
        LocalDate dataSaida = hospedagemDTO.getDataSaida();

        if (dataSaida == null || !dataSaida.isAfter(dataEntrada)) {
            throw new DataInvalidaException("A data de saída deve ser posterior à data atual.");
        }

        if (hospedagemDTO.getValorDiaria() == null || hospedagemDTO.getValorDiaria() <= 0) {
            throw new ValorInvalidoException("Valor da diária inválido.");
        }

        if (hospedagemDTO.getValorTotal() == null || hospedagemDTO.getValorTotal() <= 0) {
            throw new ValorInvalidoException("Valor total inválido.");
        }


        Hospedagem hospedagem = new Hospedagem();
        hospedagem.setNome(hospedagemDTO.getNome());
        hospedagem.setCpf(hospedagemDTO.getCpf());
        hospedagem.setDataEntrada(dataEntrada);
        hospedagem.setDataSaida(dataSaida);
        hospedagem.setValorDiaria(hospedagemDTO.getValorDiaria());
        hospedagem.setValorTotal(hospedagemDTO.getValorTotal());
        hospedagem.setFormaPagamento(hospedagemDTO.getFormaPagamento());
        hospedagem.setObservacoes(hospedagemDTO.getObservacoes());
        hospedagem.setQuarto(quarto);

        long diasHospedados = Duration.between(dataEntrada.atStartOfDay(), dataSaida.atStartOfDay()).toDays();

        if (diasHospedados <= 3) {
            hospedagem.setTipo(TipoHospedagem.NORMAL);
        } else if (hospedagem.getCpf() == null || hospedagem.getCpf().isBlank()) {
            hospedagem.setTipo(TipoHospedagem.CORPORATIVO);
        } else {
            hospedagem.setTipo(TipoHospedagem.PREFEITURA);
        }

        quarto.setOcupado(true);
        quartoRepository.save(quarto);

        return hospedagemRepository.save(hospedagem);
    }

    public Hospedagem realizarCheckoutPorNumero(CheckoutDTO checkoutDTO) {
        Quarto quarto = quartoRepository.findByNumero(checkoutDTO.getNumeroQuarto())
                .orElseThrow(() -> new QuartoNaoEncontradoException(checkoutDTO.getNumeroQuarto()));

        if (!quarto.isOcupado()) {
            throw new QuartoJaLivreException("O quarto " + quarto.getNumero() + " já está livre.");
        }

        Hospedagem hospedagem = hospedagemRepository.findTopByQuartoOrderByIdDesc(quarto)
                .orElseThrow(() -> new HospedagemAtivaNaoEncontradaException("Nenhuma hospedagem ativa encontrada para o quarto: " + quarto.getNumero()));

        hospedagem.setDataSaida(LocalDate.now());

        String novaObs = checkoutDTO.getDescricao();
        if (novaObs != null && !novaObs.isBlank()) {
            hospedagem.setObservacoes(
                    (hospedagem.getObservacoes() != null ? hospedagem.getObservacoes() + " | " : "") + novaObs
            );
        }

        quarto.setOcupado(false);
        quartoRepository.save(quarto);

        return hospedagemRepository.save(hospedagem);
    }

    public List<Hospedagem> listarHospedagens(String nome, TipoHospedagem tipo, LocalDate dataEntrada, Boolean checkout) {
        List<Hospedagem> todas = hospedagemRepository.findAll();
        LocalDate hoje = LocalDate.now();

        return todas.stream()
                .filter(h -> nome == null || (h.getNome() != null && h.getNome().toLowerCase().contains(nome.toLowerCase())))
                .filter(h -> tipo == null || h.getTipo() == tipo)
                .filter(h -> dataEntrada == null || dataEntrada.equals(h.getDataEntrada()))
                .filter(h -> checkout == null ||
                        (checkout && h.getDataSaida() != null && !h.getDataSaida().isAfter(hoje)) ||
                        (!checkout && (h.getDataSaida() == null || h.getDataSaida().isAfter(hoje)))
                )
                .collect(Collectors.toList());
    }

    public List<Hospedagem> buscarHospedagensFiltradas(LocalDate dataInicio, LocalDate dataFim, TipoHospedagem tipo, String nome, Boolean ativo) {
        List<Hospedagem> todas = hospedagemRepository.findAll();
        LocalDate hoje = LocalDate.now();

        return todas.stream()
                .filter(h -> nome == null || (h.getNome() != null && h.getNome().toLowerCase().contains(nome.toLowerCase())))
                .filter(h -> tipo == null || h.getTipo() == tipo)
                .filter(h -> (dataInicio == null || !h.getDataEntrada().isBefore(dataInicio)) &&
                        (dataFim == null || !h.getDataEntrada().isAfter(dataFim)))
                .filter(h -> ativo == null ||
                        (ativo && (h.getDataSaida() == null || h.getDataSaida().isAfter(hoje))) ||
                        (!ativo && h.getDataSaida() != null && !h.getDataSaida().isAfter(hoje)))
                .collect(Collectors.toList());
    }

    public HospedagemResponseDTO toResponseDTO(Hospedagem h) {
        String status = (h.getDataSaida() == null || h.getDataSaida().isAfter(LocalDate.now())) ? "Ativo" : "Inativo";
        return new HospedagemResponseDTO(
                h.getId(),
                h.getTipo().toString(),
                h.getNome(),
                h.getCpf(),
                h.getDataEntrada(),
                h.getDataSaida(),
                h.getValorDiaria(),
                h.getValorTotal(),
                h.getFormaPagamento(),
                h.getObservacoes(),
                h.getQuarto().getNumero(),
                h.getQuarto().isOcupado(),
                status
        );
    }
}
