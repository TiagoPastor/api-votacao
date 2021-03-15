package br.com.api.votacao.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.api.votacao.dto.PautaDTO;
import br.com.api.votacao.service.PautaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/api/v1/pauta")
@Api(value = "Pauta", tags = "Pauta")
public class PautaController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PautaController.class);

    private final PautaService service;

    @Autowired
    public PautaController(PautaService service) {
        this.service = service;
    }

    @ApiOperation(value = "Criar uma pauta para ser votada")
    @PostMapping
    public ResponseEntity<PautaDTO> salvarPauta(@Valid @RequestBody PautaDTO dto) {
        LOGGER.debug("Salvando a pauta  = {}", dto.getDescricao());
        dto = service.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @ApiOperation(value = "Buscar a pauta utilizando ID")
    @GetMapping(value = "/{id}")
    public ResponseEntity<PautaDTO> buscarPautaPeloOID(@PathVariable("id") Integer id) {
        LOGGER.debug("Buscando a pauta pelo ID = {}", id);
        return ResponseEntity.ok(service.buscarPautaPeloID(id));
    }
}
