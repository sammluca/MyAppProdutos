package br.com.fabreum.AppProdutos.service.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class PedidoRequest {
    private List<ItemPedidoRequest> itens;
}

@Getter
@Setter
class ItemPedidoRequest {
    private Long produtoId;
    private Integer quantidade;
}
