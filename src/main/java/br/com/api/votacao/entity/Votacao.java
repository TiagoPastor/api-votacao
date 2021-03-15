package br.com.api.votacao.entity;

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
@Table(name = "tbl_votacao")
@AllArgsConstructor
@NoArgsConstructor
public class Votacao {

    @Id
    @Column(name = "vt_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    
    @Column(name = "vt_voto")
    private String voto;

    @Column(name = "vt_id_pauta")
    private Integer idPauta;

    @Column(name = "vt_id_sessao_votacao")
    private Integer idSessaoVotacao;
}

