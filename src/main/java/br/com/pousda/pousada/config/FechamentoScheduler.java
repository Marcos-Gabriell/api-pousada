package br.com.pousda.pousada.config;

import br.com.pousda.pousada.model.enums.TipoFechamento;
import br.com.pousda.pousada.service.FechamentoCaixaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.DayOfWeek;
import java.time.LocalDate;

@EnableScheduling
@Configuration
public class FechamentoScheduler {

    @Autowired
    private FechamentoCaixaService fechamentoCaixaService;

    // Fecha automaticamente toda sexta-feira às 23:59
    @Scheduled(cron = "0 59 23 ? * FRI")
    public void fecharCaixaSemanalAutomatico() {
        LocalDate fim = LocalDate.now(); // Sexta-feira
        // Sábado anterior (primeiro dia da semana para a pousada)
        LocalDate inicio = fim.minusDays(6);
        fechamentoCaixaService.fecharCaixa(inicio, fim, TipoFechamento.SEMANAL);
        System.out.println("Caixa semanal fechado automaticamente: " + inicio + " até " + fim);
    }

    // Fecha automaticamente todo último dia do mês às 23:59
    @Scheduled(cron = "0 59 23 L * ?")
    public void fecharCaixaMensalAutomatico() {
        LocalDate fim = LocalDate.now(); // Último dia do mês
        LocalDate inicio = fim.withDayOfMonth(1);
        fechamentoCaixaService.fecharCaixa(inicio, fim, TipoFechamento.MENSAL);
        System.out.println("Caixa mensal fechado automaticamente: " + inicio + " até " + fim);
    }

    // Fecha automaticamente em 31 de dezembro às 23:59
    @Scheduled(cron = "0 59 23 31 12 ?")
    public void fecharCaixaAnualAutomatico() {
        LocalDate fim = LocalDate.now(); // 31/12
        LocalDate inicio = fim.withDayOfYear(1);
        fechamentoCaixaService.fecharCaixa(inicio, fim, TipoFechamento.ANUAL);
        System.out.println("Caixa anual fechado automaticamente: " + inicio + " até " + fim);
    }
}
