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

    private String usuario;          // usuário logado que fez a ação
    private String acao;             // nome da ação: "CRIAR_PEDIDO", "ATUALIZAR_PRODUTO", etc.
    private String detalhe;          // texto livre descrevendo o que aconteceu
    @PrePersist
    public void prePersist() {
        this.dataHora = LocalDateTime.now();
    }
    private LocalDateTime dataHora = LocalDateTime.now();
}
