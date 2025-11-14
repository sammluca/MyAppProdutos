package br.com.fabreum.AppProdutos.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemPedidoRequest {
    private Long produtoId;
    private Integer quantidade;
}
