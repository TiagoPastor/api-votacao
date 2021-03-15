package br.com.api.votacao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.api.votacao.entity.Pauta;

@Repository
public interface PautaRepository extends JpaRepository<Pauta, Integer> {
	
	boolean existsById(Integer id);


}
