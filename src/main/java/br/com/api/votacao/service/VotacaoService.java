package br.com.api.votacao.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.api.votacao.dto.AssociadoDTO;
import br.com.api.votacao.dto.PautaDTO;
import br.com.api.votacao.dto.ResultadoDTO;
import br.com.api.votacao.dto.VotacaoDTO;
import br.com.api.votacao.dto.VotoDTO;
import br.com.api.votacao.entity.SessaoVotacao;
import br.com.api.votacao.exception.NotFoundException;
import br.com.api.votacao.exception.SessaoEncerradaException;
import br.com.api.votacao.exception.VotoInvalidoException;
import br.com.api.votacao.repository.VotacaoRepository;

@Service
public class VotacaoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VotacaoService.class);

    private final VotacaoRepository repository;
    private final PautaService pautaService;
    private final SessaoVotacaoService sessaoVotacaoService;
    private final AssociadoService associadoService;

    @Autowired
    public VotacaoService(VotacaoRepository repository, PautaService pautaService, SessaoVotacaoService sessaoVotacaoService, AssociadoService associadoService) {
        this.repository = repository;
        this.pautaService = pautaService;
        this.sessaoVotacaoService = sessaoVotacaoService;
        this.associadoService = associadoService;
    }

    /**
     * metodo responsavel por realizar as validacoes antes do voto ser computado
     * e persistido na base de dados
     *
     * @param dto - @{@link VotoDTO}
     * @return - boolean
     */
    @Transactional(readOnly = true)
    public boolean isValidaVoto(String cpfAssociado,  Integer idPauta, Integer idSessaoVotacao, String voto) {
        LOGGER.debug("Validando os dados para voto idPauta = {}, idAssociado = {}", idPauta, cpfAssociado);

        sessaoVotacaoService.buscarSessaoPorPauta(idPauta);
        
        if (!pautaService.isPautaValida(idPauta)) {

            LOGGER.error("Pauta nao localizada para votacao idPauta {}", idPauta);
            throw new NotFoundException("Pauta nao localizada para votacao id " + idPauta);

        } else if (!sessaoVotacaoService.isSessaoVotacaoAberta(idSessaoVotacao)) {

            LOGGER.error("Tentativa de voto para sessao encerrada idSessaoVotacao {}", idSessaoVotacao);
            throw new SessaoEncerradaException("Sessão de votação já encerrada");

        } else if (!associadoService.isAssociadoPodeVotar(cpfAssociado)) {

            LOGGER.error("Associado nao autorizado a votar {}", cpfAssociado);
            throw new VotoInvalidoException("Associado nao autorizado a votar");

        } else if (!associadoService.isValidaVotacaoAssociado(cpfAssociado, idPauta)) {

            LOGGER.error("Associado tentou votar mais de 1 vez idAssociado {}", cpfAssociado);
            throw new VotoInvalidoException("Não é possível votar mais de 1 vez na mesma pauta");
        
        } else if (!voto.equalsIgnoreCase("sim") && !voto.equalsIgnoreCase("não")) {
        	
        	LOGGER.error("So é possível votar sim ou não {}", voto);
            throw new VotoInvalidoException("So é possível votar sim ou não");
        }

        return Boolean.TRUE;
    }

    /**
     * Se os dados informados para o voto, forem considerados validos
     * entao o voto é computado e persistido na base de dados.
     *
     * @param dto - @{@link VotoDTO}
     * @return - String
     */
    @Transactional
    public String votar(VotoDTO dto) {
    	 Optional<SessaoVotacao> sessao = sessaoVotacaoService.buscarSessaoPorPauta(dto.getIdPauta());
    	 if(sessao.isPresent()) {
    		 if (isValidaVoto(dto.getCpfAssociado(), dto.getIdPauta(), sessao.get().getId(), dto.getVoto())) {
    	            LOGGER.debug("Dados validos para voto idPauta = {}, idAssociado = {}", 
    	                    dto.getIdPauta(), dto.getCpfAssociado());
    	            
    	            VotacaoDTO votacaoDTO = new VotacaoDTO(null,
    	                    dto.getIdPauta(),
    	                    sessao.get().getId(),
    	                    dto.getVoto().toUpperCase(),
    	                    null,
    	                    null);

    	            registrarVoto(votacaoDTO);

    	            registrarAssociadoVotou(dto);

    	            return "Voto validado";
    	        }
    		 
    	 }
   
        return "Nenhuma sessão foi aberta para essa pauta";
    }

    /**
     * Apos voto ser computado. O associado e registrado na base de dados a fim de
     * evitar que o mesmo possa votar novamente na mesma sessao de votacao e na mesma pauta.
     *
     * @param dto - @{@link VotoDTO}
     */
    @Transactional
    public void registrarAssociadoVotou(VotoDTO dto) {
        AssociadoDTO associadoDTO = new AssociadoDTO(null, dto.getCpfAssociado(), dto.getIdPauta());
        associadoService.salvarAssociado(associadoDTO);
    }

    /**
     * @param dto - @{@link VotacaoDTO}
     */
    @Transactional
    public void registrarVoto(VotacaoDTO dto) {
        LOGGER.debug("Salvando o voto para idPauta {}", dto.getIdPauta());
        repository.save(VotacaoDTO.toEntity(dto));
    }

    /**
     * Realiza a busca e contagem dos votos Sim e Não para determinada sessao e pauta de votacao.
     *
     * @param idPauta         - @{@link br.com.api.votacao.entity.Pauta} ID
     * @param idSessaoVotacao - @{@link br.com.api.votacao.entity.SessaoVotacao} ID
     * @return - @{@link VotacaoDTO}
     */
    @Transactional(readOnly = true)
    public VotacaoDTO buscarResultadoVotacao(Integer idPauta, Integer idSessaoVotacao) {
        LOGGER.debug("Contabilizando os votos para idPauta = {}, idSessaoVotacao = {}", idPauta, idSessaoVotacao);
        VotacaoDTO dto = new VotacaoDTO();

        dto.setIdPauta(idPauta);
        dto.setIdSessaoVotacao(idSessaoVotacao);

        dto.setQuantidadeVotosSim(repository.countVotacaoByIdPautaAndIdSessaoVotacaoAndVoto(idPauta, idSessaoVotacao, "SIM"));
        dto.setQuantidadeVotosNao(repository.countVotacaoByIdPautaAndIdSessaoVotacaoAndVoto(idPauta, idSessaoVotacao, "NÃO"));

        return dto;
    }

    /**
     * Realiza a montagem dos objetos referente ao resultado de determinada sessao e pauta de votacao.
     * <p>
     * Contagem somente e realizada apos a finalizacao da sessao.
     *
     * @param idPauta         - @{@link br.com.api.votacao.entity.Pauta} ID
     * @param idSessaoVotacao - @{@link br.com.api.votacao.entity.SessaoVotacao} ID
     * @return - @{@link ResultadoDTO}
     */
    @Transactional(readOnly = true)
    public ResultadoDTO buscarDadosResultadoVotacao(Integer idPauta, Integer idSessaoVotacao) {    	
    	if(sessaoVotacaoService.isSessaoVotacaoExiste(idSessaoVotacao)) { 		
    		if(!sessaoVotacaoService.isSessaoVotacaoAberta(idSessaoVotacao)) {		
                LOGGER.debug("Construindo o objeto de retorno do resultado para idPauta = {}, idSessaoVotacao = {}", idPauta, idSessaoVotacao);
                PautaDTO pautaDTO = pautaService.buscarPautaPeloID(idPauta);
                VotacaoDTO votacaoDTO = buscarResultadoVotacao(idPauta, idSessaoVotacao);
                return new ResultadoDTO(pautaDTO, votacaoDTO);		
    		}else {
    		   throw new NotFoundException("Sessão de votação ainda está aberta, não é possível obter a contagem do resultado.");
    		}
    	}
    	
    	throw new NotFoundException("Sessão de votação não encontrada.");
    }
    
}
