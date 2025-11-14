package br.com.fabreum.AppProdutos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inventory_transaction")
public class InventoryTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private int delta; // quantidade adicionada (+) ou removida (-)

    @Column(nullable = false)
    private String reason; // "ENTRADA", "SAIDA", "AJUSTE", "DEVOLUCAO"

    private String referenceId; // pode ser id do pedido ou ajuste

    private String createdBy;

    private LocalDateTime createdAt = LocalDateTime.now();
}
