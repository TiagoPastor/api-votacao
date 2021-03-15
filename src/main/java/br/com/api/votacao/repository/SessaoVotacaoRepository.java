package br.com.api.votacao.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.api.votacao.entity.SessaoVotacao;

@Repository
public interface SessaoVotacaoRepository extends JpaRepository<SessaoVotacao, Integer> {
	
	@Query("select s from SessaoVotacao s where s.ativa=:ativo and s.dataHoraFim < :dataAtual and idPauta = :idPauta")
    List<SessaoVotacao> buscarTodasSessoesEmAndamento(Boolean ativo, LocalDateTime dataAtual, Integer idPauta);

    boolean existsByIdAndAtiva(Integer id, Boolean ativa);

    boolean existsById(Integer id);
    
    Optional<SessaoVotacao> findByIdAndAtiva(Integer id, Boolean ativa);
    
    Optional<SessaoVotacao> findByIdPautaAndAtiva(Integer idPauta, Boolean ativa);
    
    @Query("select s from SessaoVotacao s where s.idPauta=:idPauta and s.dataHoraFim >= :dataAtual")
    Optional<SessaoVotacao> buscarPorPautaEdataFinal(Integer idPauta, LocalDateTime dataAtual);
    

   
}
