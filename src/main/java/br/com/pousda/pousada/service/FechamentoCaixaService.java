package br.com.pousda.pousada.service;

import br.com.pousda.pousada.model.FechamentoCaixa;
import br.com.pousda.pousada.model.enums.TipoFechamento;
import br.com.pousda.pousada.repository.FechamentoCaixaRepository;
import br.com.pousda.pousada.repository.RecebimentoRepository;
import br.com.pousda.pousada.repository.SaidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class FechamentoCaixaService {

    @Autowired
    private FechamentoCaixaRepository fechamentoRepository;
    @Autowired
    private RecebimentoRepository recebimentoRepository;
    @Autowired
    private SaidaRepository saidaRepository;

    public LocalDate[] calcularPeriodo(TipoFechamento tipo) {
        LocalDate hoje = LocalDate.now();
        LocalDate inicio, fim;

        switch (tipo) {
            case SEMANAL:
                fim = hoje.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
                inicio = fim.minusDays(6);
                break;
            case MENSAL:
                inicio = hoje.withDayOfMonth(1);
                fim = hoje.withDayOfMonth(hoje.lengthOfMonth());
                break;
            case ANUAL:
                inicio = hoje.withDayOfYear(1);
                fim = hoje.withDayOfYear(hoje.lengthOfYear());
                break;
            default:
                inicio = hoje;
                fim = hoje;
        }
        return new LocalDate[]{inicio, fim};
    }

    public FechamentoCaixa resumoAtual(TipoFechamento tipo) {
        LocalDate[] periodo = calcularPeriodo(tipo);
        return gerarResumoSemFechamento(periodo[0], periodo[1], tipo);
    }

    public FechamentoCaixa gerarResumoSemFechamento(LocalDate inicio, LocalDate fim, TipoFechamento tipo) {
        Double totalRecebimentos = recebimentoRepository.findByDataBetween(inicio, fim)
                .stream().mapToDouble(r -> r.getValor()).sum();

        Double totalSaidas = saidaRepository.findByDataBetween(inicio, fim)
                .stream().mapToDouble(s -> s.getValor()).sum();

        Double caixaAnterior = fechamentoRepository.findAll().stream()
                .filter(f -> f.getFimPeriodo().isBefore(inicio))
                .sorted((a, b) -> b.getFimPeriodo().compareTo(a.getFimPeriodo()))
                .findFirst()
                .map(FechamentoCaixa::getSaldoFinal)
                .orElse(0.0);

        if (totalRecebimentos == 0.0 && totalSaidas == 0.0) {
            return null;
        }

        Double saldoFinal = caixaAnterior + totalRecebimentos - totalSaidas;

        FechamentoCaixa resumo = new FechamentoCaixa();
        resumo.setInicioPeriodo(inicio);
        resumo.setFimPeriodo(fim);
        resumo.setTipo(tipo);
        resumo.setCaixaAnterior(caixaAnterior);
        resumo.setTotalRecebimentos(totalRecebimentos);
        resumo.setTotalSaidas(totalSaidas);
        resumo.setSaldoFinal(saldoFinal);

        return resumo;
    }

    public FechamentoCaixa fecharCaixa(TipoFechamento tipo) {
        LocalDate[] periodo = calcularPeriodo(tipo);
        return fecharCaixa(periodo[0], periodo[1], tipo);
    }

    public FechamentoCaixa fecharCaixa(LocalDate inicio, LocalDate fim, TipoFechamento tipo) {
        Double totalRecebimentos = recebimentoRepository.findByDataBetween(inicio, fim)
                .stream().mapToDouble(r -> r.getValor()).sum();

        Double totalSaidas = saidaRepository.findByDataBetween(inicio, fim)
                .stream().mapToDouble(s -> s.getValor()).sum();

        Double caixaAnterior = fechamentoRepository.findAll().stream()
                .filter(f -> f.getFimPeriodo().isBefore(inicio))
                .sorted((a, b) -> b.getFimPeriodo().compareTo(a.getFimPeriodo()))
                .findFirst()
                .map(FechamentoCaixa::getSaldoFinal)
                .orElse(0.0);

        Double saldoFinal = caixaAnterior + totalRecebimentos - totalSaidas;

        FechamentoCaixa fechamento = new FechamentoCaixa();
        fechamento.setInicioPeriodo(inicio);
        fechamento.setFimPeriodo(fim);
        fechamento.setTipo(tipo);
        fechamento.setCaixaAnterior(caixaAnterior);
        fechamento.setTotalRecebimentos(totalRecebimentos);
        fechamento.setTotalSaidas(totalSaidas);
        fechamento.setSaldoFinal(saldoFinal);

        return fechamentoRepository.save(fechamento);
    }

    public List<FechamentoCaixa> listarTodos() {
        return fechamentoRepository.findAll();
    }

    public List<FechamentoCaixa> listarPorTipo(TipoFechamento tipo) {
        return fechamentoRepository.findByTipo(tipo);
    }

    public List<FechamentoCaixa> listarPorPeriodo(LocalDate inicio, LocalDate fim) {
        return fechamentoRepository.findByInicioPeriodoBetween(inicio, fim);
    }

    public FechamentoCaixa gerarResumoAtual(TipoFechamento tipo) {
        LocalDate hoje = LocalDate.now();
        LocalDate inicio;
        LocalDate fim = hoje;

        switch (tipo) {
            case SEMANAL:
                int daysSinceSaturday = hoje.getDayOfWeek().getValue() % 7;
                inicio = hoje.minusDays(daysSinceSaturday);
                break;
            case MENSAL:
                inicio = hoje.withDayOfMonth(1);
                break;
            case ANUAL:
                inicio = hoje.withDayOfYear(1);
                break;
            default:
                inicio = hoje;
        }

        Double totalRecebimentos = recebimentoRepository.findByDataBetween(inicio, fim)
                .stream().mapToDouble(r -> r.getValor()).sum();

        Double totalSaidas = saidaRepository.findByDataBetween(inicio, fim)
                .stream().mapToDouble(s -> s.getValor()).sum();

        Double caixaAnterior = fechamentoRepository.findAll().stream()
                .sorted((a, b) -> b.getFimPeriodo().compareTo(a.getFimPeriodo()))
                .findFirst()
                .map(FechamentoCaixa::getSaldoFinal)
                .orElse(0.0);

        if (totalRecebimentos == 0.0 && totalSaidas == 0.0) {
            return null;
        }

        Double saldoFinal = caixaAnterior + totalRecebimentos - totalSaidas;

        FechamentoCaixa resumo = new FechamentoCaixa();
        resumo.setInicioPeriodo(inicio);
        resumo.setFimPeriodo(fim);
        resumo.setTipo(tipo);
        resumo.setCaixaAnterior(caixaAnterior);
        resumo.setTotalRecebimentos(totalRecebimentos);
        resumo.setTotalSaidas(totalSaidas);
        resumo.setSaldoFinal(saldoFinal);

        return resumo;
    }

}
