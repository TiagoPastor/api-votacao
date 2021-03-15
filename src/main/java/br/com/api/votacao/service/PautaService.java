package br.com.api.votacao.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.api.votacao.dto.PautaDTO;
import br.com.api.votacao.entity.Pauta;
import br.com.api.votacao.exception.NotFoundException;
import br.com.api.votacao.repository.PautaRepository;

@Service
public class PautaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PautaService.class);

    private final PautaRepository repository;

    @Autowired
    public PautaService(PautaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public PautaDTO salvar(PautaDTO dto) {
        return PautaDTO.toDTO(repository.save(PautaDTO.toEntity(dto)));
    }


    /**
     * Realiza a busca pela pauta de votacao.
     * Se nao encontrado retorna httpStatus 404 direto para o client da API.
     * Se encontrado faz a conversao para DTO
     *
     * @param id - @{@link Pauta} ID
     * @return - @{@link PautaDTO}
     */
    @Transactional(readOnly = true)
    public PautaDTO buscarPautaPeloID(Integer id) {
        Optional<Pauta> pautaOptional = repository.findById(id);

        if (!pautaOptional.isPresent()) {
            LOGGER.error("Pauta não localizada para id {}", id);
            throw new NotFoundException("Pauta não localizada para o id " + id);
        }

        return PautaDTO.toDTO(pautaOptional.get());
    }

    /**
     * Valida se a pauta existe na base de dados.
     * Se existir  é considerada valida para votacao.
     *
     * @param id - @{@link Pauta} ID
     * @return - boolean
     */
    @Transactional(readOnly = true)
    public boolean isPautaValida(Integer id) {
        if (repository.existsById(id)) {
            return Boolean.TRUE;
        } else {
        	return Boolean.FALSE;
        }
    }
}
