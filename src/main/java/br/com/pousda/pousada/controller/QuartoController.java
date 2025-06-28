package br.com.pousda.pousada.controller;

import br.com.pousda.pousada.model.Quarto;
import br.com.pousda.pousada.service.QuartoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quartos")
public class QuartoController {

    @Autowired
    private QuartoService quartoService;

    @GetMapping
    public List<Quarto> listarTodos() {
        return quartoService.listarTodos();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Quarto> atualizarStatus(
            @PathVariable Long id,
            @RequestParam boolean ocupado) {

        Quarto atualizado = quartoService.atualizarStatus(id, ocupado);
        return ResponseEntity.ok(atualizado);
    }

    @PostMapping
    public ResponseEntity<Quarto> criar(@RequestBody Quarto quarto) {
        Quarto novo = quartoService.criar(quarto);
        return ResponseEntity.ok(novo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Quarto> editar(@PathVariable Long id, @RequestBody Quarto quarto) {
        Quarto editado = quartoService.editar(id, quarto);
        return ResponseEntity.ok(editado);
    }
}