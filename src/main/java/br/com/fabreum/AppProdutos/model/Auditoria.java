package br.com.fabreum.AppProdutos.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String usuario;      // usuário logado
    private String acao;         // ex.: CREATE_CATEGORY, UPDATE_PRODUCT
    private String detalhe;      // texto livre explicativo

    @Lob
    private String beforeJson;   // estado antes da alteração em JSON

    @Lob
    private String afterJson;    // estado depois da alteração em JSON

    private LocalDateTime dataHora;

    @PrePersist
    public void prePersist() {
        this.dataHora = LocalDateTime.now();
    }
}
