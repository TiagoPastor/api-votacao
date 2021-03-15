package br.com.api.votacao.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.api.votacao.dto.SessaoVotacaoAbrirDTO;
import br.com.api.votacao.dto.SessaoVotacaoDTO;
import br.com.api.votacao.entity.SessaoVotacao;
import br.com.api.votacao.exception.NotFoundException;
import br.com.api.votacao.repository.SessaoVotacaoRepository;

@Service
public class SessaoVotacaoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessaoVotacaoService.class);
    private static final Integer TEMPO_DEFAULT = 1;

    private final SessaoVotacaoRepository repository;
    private final PautaService pautaService;

    @Autowired
    public SessaoVotacaoService(SessaoVotacaoRepository repository, PautaService pautaService) {
        this.repository = repository;
        this.pautaService = pautaService;
    }

    /**
     * Se a sessao votacao é valida entao persiste os dados na base e inicia
     * a contagem para o encerramento da mesma.
     *
     * @param sessaoVotacaoAbrirDTO - @{@link SessaoVotacaoAbrirDTO}
     * @return - @{@link SessaoVotacaoDTO}
     */
    @Transactional
    public SessaoVotacaoDTO abrirSessaoVotacao(SessaoVotacaoAbrirDTO sessaoVotacaoAbrirDTO) {
        LOGGER.debug("Abrindo a sessao de votacao para a pauta {}", sessaoVotacaoAbrirDTO.getIdPauta());

        isValidaAbrirSessao(sessaoVotacaoAbrirDTO);

        SessaoVotacaoDTO dto = new SessaoVotacaoDTO(
                null,
                LocalDateTime.now(),
                calcularTempo(sessaoVotacaoAbrirDTO.getTempo()),
                Boolean.TRUE,
                sessaoVotacaoAbrirDTO.getIdPauta());
        
        encerraSessaoVotacao(sessaoVotacaoAbrirDTO);
        
        return salvar(dto);
    }
    

    /**
     * valida se os dados para iniciar uma validacao são consistentes
     * e ja estao persistidos na base de dados
     *
     * @param sessaoVotacaoAbrirDTO - @{@link SessaoVotacaoAbrirDTO}
     * @return - boolean
     */
    @Transactional(readOnly = true)
    public boolean isValidaAbrirSessao(SessaoVotacaoAbrirDTO sessaoVotacaoAbrirDTO) {
        if (pautaService.isPautaValida(sessaoVotacaoAbrirDTO.getIdPauta())) {
        	Optional<SessaoVotacao> sessao = repository.buscarPorPautaEdataFinal(sessaoVotacaoAbrirDTO.getIdPauta(), LocalDateTime.now());
        	if(sessao.isPresent()) {
        		throw new NotFoundException("Já existe uma sessão aberta para essa pauta " + sessaoVotacaoAbrirDTO.getIdPauta());
        	}
            return Boolean.TRUE;
        } else {
            throw new NotFoundException("Pauta não localizada idPauta" + sessaoVotacaoAbrirDTO.getIdPauta());
        }
    }
    
    /**
     * Quando houver sessao de votacao com o tempo data hora fim expirado,
     * a flag ativo é setado como FALSE e persistido a alteracao na base de dados.
     * 
     * @param dto - @{@link SessaoVotacaoAbrirDTO}
     */
    @Transactional
    public void encerraSessaoVotacao(SessaoVotacaoAbrirDTO sessaoDTO) {
    	List<SessaoVotacao> sessoes = repository.buscarTodasSessoesEmAndamento(true, LocalDateTime.now(), sessaoDTO.getIdPauta());
    	for(SessaoVotacao sessao : sessoes) {
    		sessao.setAtiva(false);
    		repository.save(sessao);
    	}
    }

    /**
     * Se a sessao existir e a data final for maior que a data atual
     * entao e considerada como valida para votacao.
     *
     * @param id - @{@link SessaoVotacao} ID
     * @return - boolean
     */
    @Transactional(readOnly = true)
    public boolean isSessaoVotacaoAberta(Integer id) {
    	Optional<SessaoVotacao> sessaoVotacao = repository.findByIdAndAtiva(id, true);
    	if(sessaoVotacao.isPresent()) {
    		if(sessaoVotacao.get().getDataHoraFim().isAfter(LocalDateTime.now()) || 
    		   sessaoVotacao.get().getDataHoraFim().equals(LocalDateTime.now())) {
    			
    			return true;   			
    		}  		
    	}
    	return false;
    }

    /**
     * @param id - @{@link SessaoVotacao} ID
     * @return - boolean
     */
    @Transactional(readOnly = true)
    public boolean isSessaoVotacaoExiste(Integer id) {
        if (repository.existsById(id)) {
            return Boolean.TRUE;
        } else {
            LOGGER.error("Sessao de votacao nao localizada para o id {}", id);
            throw new NotFoundException("Sessão de votação não localizada para o id " + id);
        }
    }

    /**
     * Com base no LocalDateTime inicial é calculada a LocalDateTime final somando-se o
     * tempo em minutos informado na chamada do servico.
     * <p>
     * Se o tempo nao for informado ou for informado com valor 0,
     * entao é considerado o tempo de 1 minuto como default.
     *
     * @param tempo - tempo em minutos
     * @return - localDateTime
     */
    private LocalDateTime calcularTempo(Integer tempo) {
        if (tempo != null && tempo != 0) {
        	System.out.println(LocalDateTime.now().plusMinutes(tempo));
            return LocalDateTime.now().plusMinutes(tempo);
        } else {
            return LocalDateTime.now().plusMinutes(TEMPO_DEFAULT);
        }
    }

    /**
     * @param dto - @{@link SessaoVotacaoDTO}
     * @return - @{@link SessaoVotacaoDTO}
     */
    @Transactional
    public SessaoVotacaoDTO salvar(SessaoVotacaoDTO dto) {
        LOGGER.debug("Salvando a sessao de votacao");
        if (Optional.ofNullable(dto).isPresent()) {
            return SessaoVotacaoDTO.toDTO(repository.save(SessaoVotacaoDTO.toEntity(dto)));
        }
        return null;
    }
    
    
    /**
     *Buscar sessao por pauta
     *
     * @param idPauta - id da pauta
     * @return - SessaoVotacao
     */
    @Transactional(readOnly = true)
    public Optional<SessaoVotacao> buscarSessaoPorPauta(Integer idPauta) {
    	return repository.findByIdPautaAndAtiva(idPauta, true);
    }
}
