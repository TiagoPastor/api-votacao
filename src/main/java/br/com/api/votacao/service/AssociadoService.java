package br.com.api.votacao.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.api.votacao.component.ValidaCPF;
import br.com.api.votacao.dto.AssociadoDTO;
import br.com.api.votacao.repository.AssociadoRepository;

@Service
public class AssociadoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AssociadoService.class);
    private final AssociadoRepository repository;
    private ValidaCPF validaCPF;

    @Autowired
    public AssociadoService(AssociadoRepository repository, ValidaCPF validaCPF) {
        this.repository = repository;
        this.validaCPF = validaCPF;
    }

    /**
     * Realiza a validacao se o associado ja votou na pauta informada pelo seu CPF.
     * Se nao existir um registro na base, entao e considerado como valido para seu voto ser computado
     *
     * @param cpfAssociado @{@link br.com.api.votacao.entity.Associado} CPF Valido
     * @param idPauta     @{@link br.com.api.votacao.entity.Pauta} ID
     * @return - boolean
     */
    @Transactional(readOnly = true)
    public boolean isValidaVotacaoAssociado(String cpfAssociado, Integer idPauta) {
        LOGGER.debug("Validando participacao do associado na votacao da pauta  id = {}", idPauta);
        if (repository.existsByCpfAssociadoAndIdPauta(cpfAssociado, idPauta)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * @param dto @{@link AssociadoDTO}
     */
    @Transactional
    public void salvarAssociado(AssociadoDTO dto) {
        LOGGER.debug("Registrando participacao do associado na votacao idAssociado = {}, idPauta = {}", dto.getCpfAssociado(), dto.getIdPauta());
        repository.save(AssociadoDTO.toEntity(dto));
    }

    /**
     * faz a chamada para metodo que realiza a consulta em API externa
     * para validar o cpf informado
     *
     * @param cpf - @{@link AssociadoDTO} CPF valido
     * @return - boolean
     */
    public boolean isAssociadoPodeVotar(String cpf) {
        return validaCPF.isVerificaAssociadoHabilitadoVotacao(cpf);
    }
}
