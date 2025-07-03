package br.com.pousda.pousada;


import br.com.pousda.pousada.dto.CheckoutDTO;
import br.com.pousda.pousada.dto.HospedagemDTO;
import br.com.pousda.pousada.exception.CampoObrigatorioException;
import br.com.pousda.pousada.exception.QuartoOcupadoException;
import br.com.pousda.pousada.model.Hospedagem;
import br.com.pousda.pousada.model.Quarto;
import br.com.pousda.pousada.repository.HospedagemRepository;
import br.com.pousda.pousada.repository.QuartoRepository;
import br.com.pousda.pousada.service.HospedagemService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HospedagemServiceTest {

    @Autowired
    private HospedagemService hospedagemService;

    @Autowired
    private HospedagemRepository hospedagemRepository;

    @Autowired
    private QuartoRepository quartoRepository;

    private static final String NUMERO_QUARTO1 = "101";
    private static final String NUMERO_QUARTO2 = "102";

    @BeforeEach
    public void setUp() {
        hospedagemRepository.deleteAll();
        quartoRepository.deleteAll();
        Quarto quarto1 = new Quarto();
        quarto1.setNumero(NUMERO_QUARTO1);
        quarto1.setOcupado(false);
        quartoRepository.save(quarto1);

        Quarto quarto2 = new Quarto();
        quarto2.setNumero(NUMERO_QUARTO2);
        quarto2.setOcupado(false);
        quartoRepository.save(quarto2);

    }

    @Test
    @Order(1)
    public void testCheckinValido() {
        HospedagemDTO dto = new HospedagemDTO();
        dto.setNome("João Teste");
        dto.setNumeroDiarias(2);
        dto.setValorDiaria(200.0);
        dto.setFormaPagamento("PIX");
        dto.setNumeroQuarto(NUMERO_QUARTO1);

        Hospedagem hospedagem = hospedagemService.realizarCheckin(dto);

        assertNotNull(hospedagem.getId());
        assertEquals(NUMERO_QUARTO1, hospedagem.getQuarto().getNumero());
        assertTrue(hospedagem.getQuarto().isOcupado());
        assertEquals(LocalDate.now(), hospedagem.getDataEntrada());
        assertEquals(LocalDate.now().plusDays(2), hospedagem.getDataSaida());
        assertEquals(400.0, hospedagem.getValorTotal());
    }

    @Test
    @Order(2)
    public void testCheckinComQuartoOcupadoDeveLancarErro() {
        HospedagemDTO dto1 = new HospedagemDTO();
        dto1.setNome("Primeiro Hóspede");
        dto1.setNumeroDiarias(1);
        dto1.setValorDiaria(150.0);
        dto1.setFormaPagamento("DINHEIRO");
        dto1.setNumeroQuarto(NUMERO_QUARTO1);
        hospedagemService.realizarCheckin(dto1);

        HospedagemDTO dto2 = new HospedagemDTO();
        dto2.setNome("Segundo Hóspede");
        dto2.setNumeroDiarias(1);
        dto2.setValorDiaria(180.0);
        dto2.setFormaPagamento("CARTAO");
        dto2.setNumeroQuarto(NUMERO_QUARTO1);

        assertThrows(QuartoOcupadoException.class, () -> hospedagemService.realizarCheckin(dto2));
    }

    @Test
    @Order(3)
    public void testCheckinCamposObrigatorios() {
        HospedagemDTO dto = new HospedagemDTO();
        // Não preenche nome
        dto.setNumeroDiarias(1);
        dto.setValorDiaria(120.0);
        dto.setFormaPagamento("DINHEIRO");
        dto.setNumeroQuarto(NUMERO_QUARTO1);

        assertThrows(CampoObrigatorioException.class, () -> hospedagemService.realizarCheckin(dto));
    }

    @Test
    @Order(4)
    public void testEditarHospedagem_TrocaQuartoEstendeDiarias() {
        // Cria hospedagem
        HospedagemDTO dto = new HospedagemDTO();
        dto.setNome("Hóspede Editar");
        dto.setNumeroDiarias(2);
        dto.setValorDiaria(120.0);
        dto.setFormaPagamento("DINHEIRO");
        dto.setNumeroQuarto(NUMERO_QUARTO1);
        Hospedagem hospedagem = hospedagemService.realizarCheckin(dto);

        // Edita: troca quarto e estende diárias
        HospedagemDTO editarDTO = new HospedagemDTO();
        editarDTO.setNumeroDiarias(5);
        editarDTO.setNumeroQuarto(NUMERO_QUARTO2);
        editarDTO.setObservacoes("Estendeu a estadia e mudou de quarto");
        Hospedagem editada = hospedagemService.editarHospedagem(hospedagem.getId(), editarDTO);

        assertEquals(NUMERO_QUARTO2, editada.getQuarto().getNumero());
        assertTrue(editada.getQuarto().isOcupado());
        assertFalse(quartoRepository.findByNumero(NUMERO_QUARTO1).orElseThrow().isOcupado());
        assertEquals(5, ChronoUnit.DAYS.between(editada.getDataEntrada(), editada.getDataSaida()));
        assertEquals(600.0, editada.getValorTotal());
        assertEquals("Estendeu a estadia e mudou de quarto", editada.getObservacoes());
    }

    @Test
    @Order(5)
    public void testCheckoutManual() {
        HospedagemDTO dto = new HospedagemDTO();
        dto.setNome("Hóspede Checkout");
        dto.setNumeroDiarias(1);
        dto.setValorDiaria(80.0);
        dto.setFormaPagamento("PIX");
        dto.setNumeroQuarto(NUMERO_QUARTO1);
        hospedagemService.realizarCheckin(dto);

        CheckoutDTO checkoutDTO = new CheckoutDTO();
        checkoutDTO.setNumeroQuarto(NUMERO_QUARTO1);
        checkoutDTO.setDescricao("Saiu normalmente");

        Hospedagem hospedagem = hospedagemService.realizarCheckoutPorNumero(checkoutDTO);

        assertFalse(hospedagem.getQuarto().isOcupado());
        assertEquals(LocalDate.now(), hospedagem.getDataSaida());
        assertTrue(hospedagem.getObservacoes().contains("Saiu normalmente"));
    }

    @Test
    @Order(6)
    public void testCheckoutAutomaticoSimulado() {
        // Cria hospedagem com data de saída ontem (ainda ocupada)
        HospedagemDTO dto = new HospedagemDTO();
        dto.setNome("Checkout Auto");
        dto.setNumeroDiarias(1);
        dto.setValorDiaria(100.0);
        dto.setFormaPagamento("PIX");
        dto.setNumeroQuarto(NUMERO_QUARTO1);
        Hospedagem hospedagem = hospedagemService.realizarCheckin(dto);

        // Força data de saída para ontem
        hospedagem.setDataSaida(LocalDate.now().minusDays(1));
        hospedagem.getQuarto().setOcupado(true);
        hospedagemRepository.save(hospedagem);

        // Simula o checkout automático
        List<Hospedagem> hospedagens = hospedagemRepository.findAll();
        for (Hospedagem h : hospedagens) {
            if (h.getDataSaida() != null && h.getQuarto().isOcupado()) {
                if (LocalDate.now().isAfter(h.getDataSaida())) {
                    h.getQuarto().setOcupado(false);
                    hospedagemRepository.save(h);
                    quartoRepository.save(h.getQuarto());
                }
            }
        }

        Quarto quarto = quartoRepository.findByNumero(NUMERO_QUARTO1).orElseThrow();
        assertFalse(quarto.isOcupado());
    }

    @Test
    @Order(7)
    public void testListarHospedagens() {
        HospedagemDTO dto = new HospedagemDTO();
        dto.setNome("Consulta Hóspede");
        dto.setNumeroDiarias(2);
        dto.setValorDiaria(120.0);
        dto.setFormaPagamento("PIX");
        dto.setNumeroQuarto(NUMERO_QUARTO1);
        hospedagemService.realizarCheckin(dto);

        List<Hospedagem> lista = hospedagemService.listarHospedagens("Consulta Hóspede", null, null);
        assertFalse(lista.isEmpty());
        assertEquals("Consulta Hóspede", lista.get(0).getNome());
    }
}
