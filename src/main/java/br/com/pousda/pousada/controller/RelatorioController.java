package br.com.pousda.pousada.controller;

import br.com.pousda.pousada.model.Hospedagem;
import br.com.pousda.pousada.model.enums.TipoHospedagem;
import br.com.pousda.pousada.service.HospedagemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    @Autowired
    private HospedagemService hospedagemService;


    public List<Hospedagem> relatorioFiltrado(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) TipoHospedagem tipo,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Boolean ativo
    ) {
        return hospedagemService.buscarHospedagensFiltradas(dataInicio, dataFim, tipo, nome, ativo);
    }
}