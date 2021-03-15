package br.com.api.votacao.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.br.CPF;

import br.com.api.votacao.entity.Associado;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "AssociadoDTO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssociadoDTO {

    private Integer id;

    @ApiModelProperty(value = "CPF válido referente ao associado")
    @CPF(message = "Não é um CPF valido")
    @NotBlank(message = "CPF do associado deve ser preenchido")
    private String cpfAssociado;

    @ApiModelProperty(value = "ID da pauta a ser votada")
    @NotNull(message = "IdPauta deve ser preenchido")
    private Integer idPauta;

    public static Associado toEntity(AssociadoDTO dto) {
        return Associado.builder()
                .id(dto.getId())
                .cpfAssociado(dto.getCpfAssociado())
                .idPauta(dto.getIdPauta())
                .build();
    }
}
