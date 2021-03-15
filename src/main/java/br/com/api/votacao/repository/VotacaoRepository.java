package br.com.api.votacao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.api.votacao.entity.Associado;
import br.com.api.votacao.entity.Votacao;

@Repository
public interface VotacaoRepository extends JpaRepository<Votacao, Integer> {

	Integer countVotacaoByIdPautaAndIdSessaoVotacaoAndVoto(Integer idPauta, Integer idSessaoVotacao, String voto);
	
}
