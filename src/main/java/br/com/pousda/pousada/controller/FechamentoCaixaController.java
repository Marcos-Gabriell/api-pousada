package br.com.pousda.pousada.controller;

import br.com.pousda.pousada.model.FechamentoCaixa;
import br.com.pousda.pousada.model.enums.TipoFechamento;
import br.com.pousda.pousada.service.FechamentoCaixaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/fechamento-caixa")
public class FechamentoCaixaController {

    @Autowired
    private FechamentoCaixaService fechamentoService;

    @GetMapping("/resumo-atual")
    public ResponseEntity<?> resumoAtual(@RequestParam TipoFechamento tipo) {
        FechamentoCaixa resumo = fechamentoService.gerarResumoAtual(tipo);
        if (resumo == null) {
            return ResponseEntity.ok("Não houve lançamentos neste período.");
        }
        return ResponseEntity.ok(resumo);
    }

    @PostMapping("/manual")
    public ResponseEntity<FechamentoCaixa> fecharManual(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam TipoFechamento tipo
    ) {
        FechamentoCaixa fechado = fechamentoService.fecharCaixa(inicio, fim, tipo);
        return ResponseEntity.ok(fechado);
    }

    @GetMapping
    public List<FechamentoCaixa> listarTodos() {
        return fechamentoService.listarTodos();
    }

    @GetMapping("/periodo")
    public List<FechamentoCaixa> buscarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim
    ) {
        return fechamentoService.listarPorPeriodo(inicio, fim);
    }

    @GetMapping("/tipo")
    public List<FechamentoCaixa> buscarPorTipo(@RequestParam TipoFechamento tipo) {
        return fechamentoService.listarPorTipo(tipo);
    }
}
