package br.com.pousda.pousada.config;

import br.com.pousda.pousada.model.FechamentoCaixa;
import br.com.pousda.pousada.model.Hospedagem;
import br.com.pousda.pousada.model.Quarto;
import br.com.pousda.pousada.model.enums.StatusQuarto;
import br.com.pousda.pousada.model.enums.TipoFechamento;
import br.com.pousda.pousada.model.enums.TipoHospedagem;
import br.com.pousda.pousada.model.enums.TipoQuarto;
import br.com.pousda.pousada.repository.FechamentoCaixaRepository;
import br.com.pousda.pousada.repository.HospedagemRepository;
import br.com.pousda.pousada.repository.QuartoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(
            QuartoRepository quartoRepository,
            FechamentoCaixaRepository fechamentoCaixaRepository,
            HospedagemRepository hospedagemRepository) {
        return args -> {

            // Hospedagem exemplo
            Quarto quarto = quartoRepository.findByNumero("2").orElse(null);
            if (quarto != null) {
                Hospedagem antiga = new Hospedagem();
                antiga.setNome("Marcos");
                antiga.setCpf("111.222.333-44");
                antiga.setTipo(TipoHospedagem.NORMAL);
                antiga.setQuarto(quarto);
                antiga.setDataEntrada(LocalDate.of(2024, 12, 20));
                antiga.setDataSaida(LocalDate.of(2024, 12, 25));
                antiga.setValorDiaria(250.0);
                antiga.setValorTotal(1250.0);
                antiga.setFormaPagamento("Dinheiro");
                antiga.setObservacoes("Exemplo de hospedagem finalizada.");
                hospedagemRepository.save(antiga);
            }

            // Fechamentos de caixa (sem alterações)
            if (fechamentoCaixaRepository.count() == 0) {
                FechamentoCaixa semanal = new FechamentoCaixa();
                semanal.setInicioPeriodo(LocalDate.of(2025, 6, 10));
                semanal.setFimPeriodo(LocalDate.of(2025, 6, 16));
                semanal.setCaixaAnterior(1000.0);
                semanal.setTotalRecebimentos(1500.0);
                semanal.setTotalSaidas(500.0);
                semanal.setSaldoFinal(2000.0);
                semanal.setTipo(TipoFechamento.SEMANAL);
                fechamentoCaixaRepository.save(semanal);

                FechamentoCaixa mensal = new FechamentoCaixa();
                mensal.setInicioPeriodo(LocalDate.of(2025, 6, 1));
                mensal.setFimPeriodo(LocalDate.of(2025, 6, 30));
                mensal.setCaixaAnterior(2000.0);
                mensal.setTotalRecebimentos(6000.0);
                mensal.setTotalSaidas(2500.0);
                mensal.setSaldoFinal(5500.0);
                mensal.setTipo(TipoFechamento.MENSAL);
                fechamentoCaixaRepository.save(mensal);

                FechamentoCaixa anual = new FechamentoCaixa();
                anual.setInicioPeriodo(LocalDate.of(2025, 1, 1));
                anual.setFimPeriodo(LocalDate.of(2025, 12, 31));
                anual.setCaixaAnterior(0.0);
                anual.setTotalRecebimentos(30000.0);
                anual.setTotalSaidas(12000.0);
                anual.setSaldoFinal(18000.0);
                anual.setTipo(TipoFechamento.ANUAL);
                fechamentoCaixaRepository.save(anual);
            }
        };
    }
}
