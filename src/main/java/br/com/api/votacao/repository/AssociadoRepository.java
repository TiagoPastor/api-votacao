package br.com.api.votacao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.api.votacao.entity.Associado;

@Repository
public interface AssociadoRepository extends JpaRepository<Associado, Integer> {

    Boolean existsByCpfAssociadoAndIdPauta(String cpfAssociado, Integer idPauta);
}
