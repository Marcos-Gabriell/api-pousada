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
    public List<HospedagemResponseDTO> buscarHospedagens(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) TipoHospedagem tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataEntrada,
            @RequestParam(required = false) Boolean checkout
    ) {
        return hospedagemService.listarHospedagens(nome, tipo, dataEntrada, checkout)
                .stream()
                .map(hospedagemService::toResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/relatorios/hospedagens")
    public List<HospedagemResponseDTO> relatorioFiltrado(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) TipoHospedagem tipo,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Boolean ativo
    ) {
        return hospedagemService.buscarHospedagensFiltradas(dataInicio, dataFim, tipo, nome, ativo)
                .stream()
                .map(hospedagemService::toResponseDTO)
                .collect(Collectors.toList());
    }
}
