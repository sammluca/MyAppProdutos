package br.com.fabreum.AppProdutos.service.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PedidoResponse {
    private Long id;
    private String usuario;
    private LocalDateTime dataCriacao;
    private BigDecimal total;
    private List<ItemResponse> itens;

    @Getter
    @Builder
    public static class ItemResponse {
        private String produtoNome;
        private Integer quantidade;
        private BigDecimal precoUnitario;
    }
}
