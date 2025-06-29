package br.com.pousda.pousada.controller;

import br.com.pousda.pousada.dto.CheckoutDTO;
import br.com.pousda.pousada.dto.HospedagemDTO;
import br.com.pousda.pousada.dto.HospedagemResponseDTO;
import br.com.pousda.pousada.model.Hospedagem;
import br.com.pousda.pousada.model.enums.TipoHospedagem;
import br.com.pousda.pousada.service.HospedagemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hospedagens")
public class HospedagemController {

    @Autowired
    private HospedagemService hospedagemService;

    @PostMapping("/Checkin")
    public ResponseEntity<Hospedagem> realizarCheckin(@RequestBody HospedagemDTO hospedagemDTO) {
        Hospedagem hospedagem = hospedagemService.realizarCheckin(hospedagemDTO);
        return ResponseEntity.ok(hospedagem);
    }

    @PostMapping("/checkout")
    public ResponseEntity<Hospedagem> checkoutPorNumero(@RequestBody CheckoutDTO checkoutDTO) {
        Hospedagem hospedagem = hospedagemService.realizarCheckoutPorNumero(checkoutDTO);
        return ResponseEntity.ok(hospedagem);
    }

    @GetMapping
    public ResponseEntity<?> listarHospedagens(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) TipoHospedagem tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataEntrada
    ) {
        List<HospedagemResponseDTO> lista = hospedagemService.listarHospedagens(nome, tipo, dataEntrada)
                .stream()
                .map(hospedagemService::toResponseDTO)
                .collect(Collectors.toList());
        if (lista.isEmpty()) {
            return ResponseEntity.ok(Collections.singletonMap("mensagem", "Nenhuma hospedagem ativa encontrada."));
        }
        return ResponseEntity.ok(lista);
    }


    @GetMapping("/relatorios/hospedagens")
    public ResponseEntity<?> relatorioFiltrado(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) TipoHospedagem tipo,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Boolean ativo
    ) {
        List<HospedagemResponseDTO> lista = hospedagemService.buscarHospedagensFiltradas(dataInicio, dataFim, tipo, nome, ativo)
                .stream()
                .map(hospedagemService::toResponseDTO)
                .collect(Collectors.toList());

        if (lista.isEmpty()) {
            StringBuilder msg = new StringBuilder("Nenhuma hospedagem encontrada para o filtro: ");
            boolean temFiltro = false;
            if (tipo != null)   { msg.append("tipo=").append(tipo).append(";"); temFiltro = true; }
            if (nome != null && !nome.isBlank()) { msg.append("nome=").append(nome).append("; "); temFiltro = true; }
            if (dataInicio != null) { msg.append("dataInicio=").append(dataInicio).append("; "); temFiltro = true; }
            if (dataFim != null)    { msg.append("dataFim=").append(dataFim).append("; "); temFiltro = true; }
            if (ativo != null)      { msg.append("ativo=").append(ativo).append("; "); temFiltro = true; }
            if (!temFiltro) msg = new StringBuilder("Nenhuma hospedagem encontrada.");
            return ResponseEntity.ok(Collections.singletonMap("mensagem", msg.toString()));
        }
        return ResponseEntity.ok(lista);
    }



}
