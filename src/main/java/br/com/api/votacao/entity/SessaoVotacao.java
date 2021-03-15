package br.com.api.votacao.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@Table(name = "tbl_sessao_votacao")
@AllArgsConstructor
@NoArgsConstructor
public class SessaoVotacao {

    @Id
    @Column(name = "sv_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "sv_data_hora_inicio")
    private LocalDateTime dataHoraInicio;

    @Column(name = "sv_data_hora_fim")
    private LocalDateTime dataHoraFim;

    @Column(name = "sv_status")
    private Boolean ativa;
    
    @Column(name = "sv_id_pauta")
    private Integer idPauta;


}
