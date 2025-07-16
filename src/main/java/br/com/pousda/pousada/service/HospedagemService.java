package br.com.pousda.pousada.service;

import br.com.pousda.pousada.dto.CheckoutDTO;
import br.com.pousda.pousada.dto.HospedagemDTO;
import br.com.pousda.pousada.dto.HospedagemResponseDTO;
import br.com.pousda.pousada.exception.*;
import br.com.pousda.pousada.model.Hospedagem;
import br.com.pousda.pousada.model.Quarto;
import br.com.pousda.pousada.model.enums.StatusQuarto;
import br.com.pousda.pousada.model.enums.TipoHospedagem;
import br.com.pousda.pousada.repository.HospedagemRepository;
import br.com.pousda.pousada.repository.QuartoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HospedagemService {

    @Autowired
    private HospedagemRepository hospedagemRepository;

    @Autowired
    private QuartoRepository quartoRepository;

    public Hospedagem realizarCheckin(HospedagemDTO hospedagemDTO) {
        if (hospedagemDTO.getNome() == null || hospedagemDTO.getNome().trim().isEmpty())
            throw new CampoObrigatorioException("Nome é obrigatório.");

        if (hospedagemDTO.getFormaPagamento() == null || hospedagemDTO.getFormaPagamento().trim().isEmpty())
            throw new CampoObrigatorioException("Forma de pagamento é obrigatória.");

        if (hospedagemDTO.getNumeroDiarias() == null || hospedagemDTO.getNumeroDiarias() <= 0)
            throw new CampoObrigatorioException("O número de diárias deve ser maior que zero.");

        if (hospedagemDTO.getValorDiaria() == null || hospedagemDTO.getValorDiaria() <= 0) {
            throw new ValorInvalidoException("Valor da diária inválido.");
        }

        Quarto quarto = quartoRepository.findByNumero(hospedagemDTO.getNumeroQuarto())
                .orElseThrow(() -> new QuartoNaoEncontradoException(hospedagemDTO.getNumeroQuarto()));

        if (quarto.getStatus() == StatusQuarto.OCUPADO) {
            throw new QuartoOcupadoException("O quarto " + quarto.getNumero() + " já está ocupado.");
        }
        if (quarto.getStatus() == StatusQuarto.MANUTENCAO) {
            throw new QuartoOcupadoException("O quarto " + quarto.getNumero() + " está em manutenção.");
        }

        LocalDate dataEntrada = LocalDate.now();
        LocalDate dataSaida = dataEntrada.plusDays(hospedagemDTO.getNumeroDiarias());
        double valorTotal = hospedagemDTO.getValorDiaria() * hospedagemDTO.getNumeroDiarias();

        Hospedagem hospedagem = new Hospedagem();
        hospedagem.setNome(hospedagemDTO.getNome());
        hospedagem.setCpf(hospedagemDTO.getCpf());
        hospedagem.setDataEntrada(dataEntrada);
        hospedagem.setDataSaida(dataSaida);
        hospedagem.setValorDiaria(hospedagemDTO.getValorDiaria());
        hospedagem.setValorTotal(valorTotal);
        hospedagem.setFormaPagamento(hospedagemDTO.getFormaPagamento());
        hospedagem.setObservacoes(hospedagemDTO.getObservacoes());
        hospedagem.setQuarto(quarto);

        if (hospedagemDTO.getCpf() != null && !hospedagemDTO.getCpf().isBlank()) {
            hospedagem.setTipo(TipoHospedagem.PREFEITURA);
        } else if (hospedagemDTO.getNumeroDiarias() > 3) {
            hospedagem.setTipo(TipoHospedagem.CORPORATIVO);
        } else {
            hospedagem.setTipo(TipoHospedagem.NORMAL);
        }

        quarto.setStatus(StatusQuarto.OCUPADO);
        quartoRepository.save(quarto);

        return hospedagemRepository.save(hospedagem);
    }

    public Hospedagem realizarCheckoutPorNumero(CheckoutDTO checkoutDTO) {
        if (checkoutDTO.getNumeroQuarto() == null || checkoutDTO.getNumeroQuarto().trim().isEmpty()) {
            throw new CampoObrigatorioException("Número do quarto é obrigatório");
        }
        if (checkoutDTO.getDescricao() == null || checkoutDTO.getDescricao().trim().isEmpty()) {
            throw new CampoObrigatorioException("Descrição do motivo da saída é obrigatória.");
        }

        Quarto quarto = quartoRepository.findByNumero(checkoutDTO.getNumeroQuarto())
                .orElseThrow(() -> new QuartoNaoEncontradoException(checkoutDTO.getNumeroQuarto()));

        if (quarto.getStatus() != StatusQuarto.OCUPADO) {
            throw new QuartoJaLivreException("O quarto " + quarto.getNumero() + " já está livre.");
        }

        Hospedagem hospedagem = hospedagemRepository.findTopByQuartoOrderByIdDesc(quarto)
                .orElseThrow(() -> new HospedagemAtivaNaoEncontradaException("Nenhuma hospedagem ativa encontrada para o quarto: " + quarto.getNumero()));

        LocalDate dataHoje = LocalDate.now();
        LocalDate dataEntrada = hospedagem.getDataEntrada();

        long diasHospedados = ChronoUnit.DAYS.between(dataEntrada, dataHoje);
        if (diasHospedados <= 0) diasHospedados = 1;

        hospedagem.setDataSaida(dataHoje);

        double valorTotal = hospedagem.getValorDiaria() * diasHospedados;
        hospedagem.setValorTotal(valorTotal);

        String novaObs = checkoutDTO.getDescricao();
        hospedagem.setObservacoes(
                (hospedagem.getObservacoes() != null ? hospedagem.getObservacoes() + " | " : "") + novaObs
        );

        quarto.setStatus(StatusQuarto.DISPONIVEL);
        quartoRepository.save(quarto);

        return hospedagemRepository.save(hospedagem);
    }

    public List<Hospedagem> listarHospedagens(String nome, TipoHospedagem tipo, LocalDate dataEntrada) {
        List<Hospedagem> todas = hospedagemRepository.findAll();
        LocalDate hoje = LocalDate.now();

        return todas.stream()
                .filter(h -> !Boolean.TRUE.equals(h.getCancelada()))
                .filter(h -> nome == null || (h.getNome() != null && h.getNome().toLowerCase().contains(nome.toLowerCase())))
                .filter(h -> tipo == null || h.getTipo() == tipo)
                .filter(h -> dataEntrada == null || dataEntrada.equals(h.getDataEntrada()))
                .filter(h -> h.getDataSaida() == null || h.getDataSaida().isAfter(hoje))
                .collect(Collectors.toList());
    }

    public List<Hospedagem> buscarHospedagensFiltradas(
            LocalDate dataInicio,
            LocalDate dataFim,
            TipoHospedagem tipo,
            String nome,
            Boolean ativo,
            Boolean incluirCanceladas
    ) {
        List<Hospedagem> todas = hospedagemRepository.findAll();
        LocalDate hoje = LocalDate.now();

        return todas.stream()
                .filter(h -> Boolean.TRUE.equals(incluirCanceladas) || !Boolean.TRUE.equals(h.getCancelada()))
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
        Integer numeroDiarias = null;
        if (h.getDataEntrada() != null && h.getDataSaida() != null) {
            numeroDiarias = (int) ChronoUnit.DAYS.between(h.getDataEntrada(), h.getDataSaida());
        }
        // Exibe true se status == OCUPADO, false caso contrário
        boolean ocupado = h.getQuarto().getStatus() == StatusQuarto.OCUPADO;
        return new HospedagemResponseDTO(
                h.getId(),
                h.getTipo().toString(),
                h.getNome(),
                h.getCpf(),
                h.getDataEntrada(),
                h.getDataSaida(),
                numeroDiarias,
                h.getValorDiaria(),
                h.getValorTotal(),
                h.getFormaPagamento(),
                h.getObservacoes(),
                h.getQuarto().getNumero(),
                ocupado,
                status
        );
    }

    public Hospedagem editarHospedagem(Long id, HospedagemDTO dto) {
        Hospedagem hospedagem = hospedagemRepository.findById(id)
                .orElseThrow(() -> new HospedagemNaoEncontradaException("Hospedagem não encontrada."));

        if (Boolean.TRUE.equals(hospedagem.getCancelada())) {
            throw new OperacaoNaoPermitidaException("Hospedagem já está cancelada.");
        }
        if (hospedagem.getDataSaida() != null && !hospedagem.getDataSaida().isAfter(LocalDate.now())) {
            throw new OperacaoNaoPermitidaException("Não é possível editar uma hospedagem já finalizada.");
        }

        // Atualiza número de diárias/data de saída
        if (dto.getNumeroDiarias() != null && dto.getNumeroDiarias() > 0) {
            hospedagem.setDataSaida(hospedagem.getDataEntrada().plusDays(dto.getNumeroDiarias()));
            hospedagem.setValorTotal(hospedagem.getValorDiaria() * dto.getNumeroDiarias());
        }

        // Atualiza observações ou forma de pagamento, se informado
        if (dto.getObservacoes() != null) {
            hospedagem.setObservacoes(dto.getObservacoes());
        }
        if (dto.getFormaPagamento() != null) {
            hospedagem.setFormaPagamento(dto.getFormaPagamento());
        }

        // Permite troca de quarto, se informado e for diferente
        if (dto.getNumeroQuarto() != null &&
                !dto.getNumeroQuarto().equals(hospedagem.getQuarto().getNumero())) {

            Quarto novoQuarto = quartoRepository.findByNumero(dto.getNumeroQuarto())
                    .orElseThrow(() -> new QuartoNaoEncontradoException(dto.getNumeroQuarto()));

            if (novoQuarto.getStatus() == StatusQuarto.OCUPADO) {
                throw new QuartoOcupadoException("O quarto " + novoQuarto.getNumero() + " já está ocupado.");
            }
            if (novoQuarto.getStatus() == StatusQuarto.MANUTENCAO) {
                throw new QuartoOcupadoException("O quarto " + novoQuarto.getNumero() + " está em manutenção.");
            }

            // Libera o quarto antigo
            hospedagem.getQuarto().setStatus(StatusQuarto.DISPONIVEL);
            quartoRepository.save(hospedagem.getQuarto());

            // Ocupa o novo quarto
            novoQuarto.setStatus(StatusQuarto.OCUPADO);
            quartoRepository.save(novoQuarto);

            // Atualiza hospedagem
            hospedagem.setQuarto(novoQuarto);
        }

        return hospedagemRepository.save(hospedagem);
    }

    @Transactional
    @Scheduled(cron = "0 0 12 * * *") // Meio-dia, todo dia
    public void checkoutAutomaticoDiario() {
        LocalDate hoje = LocalDate.now();

        // Busca hospedagens onde a data de saída já passou e o quarto ainda está OCUPADO
        List<Hospedagem> vencidas = hospedagemRepository.findAll().stream()
                .filter(h -> h.getDataSaida() != null && h.getDataSaida().isBefore(hoje))
                .filter(h -> h.getQuarto().getStatus() == StatusQuarto.OCUPADO)
                .collect(Collectors.toList());

        for (Hospedagem hospedagem : vencidas) {
            Quarto quarto = hospedagem.getQuarto();
            quarto.setStatus(StatusQuarto.DISPONIVEL);
            quartoRepository.save(quarto);

            String obs = (hospedagem.getObservacoes() != null ? hospedagem.getObservacoes() + " | " : "")
                    + "Checkout automático em " + hoje;
            hospedagem.setObservacoes(obs);
            hospedagemRepository.save(hospedagem);
        }
    }
}
