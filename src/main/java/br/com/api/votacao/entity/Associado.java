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
@Table(name = "tbl_associado")
@AllArgsConstructor
@NoArgsConstructor
public class Associado {

    @Id
    @Column(name = "ass_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "ass_cpf_associado")
    private String cpfAssociado;

    @Column(name = "ass_id_pauta")
    private Integer idPauta;
}

