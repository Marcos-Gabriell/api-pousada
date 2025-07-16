package br.com.pousda.pousada.controller;

import br.com.pousda.pousada.model.Quarto;
import br.com.pousda.pousada.model.enums.StatusQuarto;
import br.com.pousda.pousada.model.enums.TipoQuarto;
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

    @GetMapping("/{id}")
    public ResponseEntity<Quarto> detalhes(@PathVariable Long id) {
        return quartoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    public List<Quarto> buscar(
            @RequestParam(required = false) StatusQuarto status,
            @RequestParam(required = false) TipoQuarto tipo,
            @RequestParam(required = false) String termo
    ) {
        return quartoService.filtrar(status, tipo, termo);
    }

    @PostMapping
    public ResponseEntity<Quarto> criar(@RequestBody Quarto quarto) {
        return ResponseEntity.ok(quartoService.criar(quarto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Quarto> editar(@PathVariable Long id, @RequestBody Quarto quarto) {
        return ResponseEntity.ok(quartoService.editar(id, quarto));
    }

    @PutMapping("/{id}/liberar")
    public ResponseEntity<Quarto> liberar(@PathVariable Long id) {
        return ResponseEntity.ok(quartoService.liberar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        quartoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

}
