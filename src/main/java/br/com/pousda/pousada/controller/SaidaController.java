package br.com.pousda.pousada.controller;

import br.com.pousda.pousada.dto.SaidaDTO;
import br.com.pousda.pousada.model.Saida;
import br.com.pousda.pousada.service.SaidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/saidas")
public class SaidaController {

    @Autowired
    private SaidaService saidaService;

    @PostMapping
    public ResponseEntity<Saida> registrar(@Valid @RequestBody SaidaDTO saidaDTO) {
        Saida saida = saidaService.salvarDTO(saidaDTO);
        return ResponseEntity.ok(saida);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Saida> atualizar(@PathVariable Long id, @RequestBody Saida saida) {
        Saida atualizada = saidaService.atualizar(id, saida);
        return ResponseEntity.ok(atualizada);
    }

    @GetMapping
    public List<Saida> listar() {
        return saidaService.listarTodas();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> excluir(@PathVariable Long id) {
        saidaService.excluir(id);
        return ResponseEntity.ok("Saída excluída com sucesso.");
    }

    @GetMapping("/periodo")
    public List<Saida> buscarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim
    ) {
        return saidaService.buscarPorPeriodo(inicio, fim);
    }

    @GetMapping("/total")
    public ResponseEntity<Double> somarSaidasPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim
    ) {
        Double total = saidaService.somarSaidasPorPeriodo(inicio, fim);
        return ResponseEntity.ok(total);
    }
}
