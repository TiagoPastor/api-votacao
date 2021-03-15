package br.com.api.votacao.component;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.api.votacao.exception.NotFoundException;

@Component
public class ValidaCPF {
	
	 private static final String ABLE_TO_VOTE = "ABLE_TO_VOTE";

	    /**
	     * Faz consulta em uma API externa para validar o CPF, verificando se o associado esta habilitado para votacao.
	     *
	     * ABLE_TO_VOTE - Valido para votar
	     *
	     * UNABLE_TO_VOTE - Invalido para votar
	     *
	     * @param cpf - @{@link br.com.api.votacao.entity.Associado} CPF valido
	     * @return - boolea
	     */
	    public boolean isVerificaAssociadoHabilitadoVotacao(String cpf) {
	        try {
	            RestTemplate restTemplate = new RestTemplate();
	            String fooResourceUrl = "https://user-info.herokuapp.com/users/{cpf}";
	            ResponseEntity<String> response = restTemplate.getForEntity(fooResourceUrl, String.class, cpf);

	            ObjectMapper mapper = new ObjectMapper();
	            JsonNode root = mapper.readTree(response.getBody());
	            JsonNode status = root.path("status");

	            if (ABLE_TO_VOTE.equals(status.textValue())) {
	                return Boolean.TRUE;
	            }

	            return Boolean.FALSE;

	        } catch (HttpClientErrorException | IOException ex) {
	            throw new NotFoundException("Não foi possivel localizar o CPF do associado");
	        }
	    }
}


