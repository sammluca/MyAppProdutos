package br.com.fabreum.AppProdutos.service;

import br.com.fabreum.AppProdutos.entity.Usuario;
import br.com.fabreum.AppProdutos.model.ItemPedido;
import br.com.fabreum.AppProdutos.model.Pedido;
import br.com.fabreum.AppProdutos.model.Produtos;
import br.com.fabreum.AppProdutos.repository.PedidoRepository;
import br.com.fabreum.AppProdutos.repository.ProdutosRepository;
import br.com.fabreum.AppProdutos.repository.UsuarioRepository;
import br.com.fabreum.AppProdutos.service.dto.PedidoRequest;
import br.com.fabreum.AppProdutos.service.dto.PedidoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutosRepository produtosRepository;
    private final AuditoriaService auditoriaService;

    @Transactional
    public PedidoResponse criarPedido(PedidoRequest pedidoRequest, String username) {

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);

        List<ItemPedido> itens = pedidoRequest.getItens().stream().map(i -> {
            Produtos produto = produtosRepository.findById(i.getProdutoId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + i.getProdutoId()));

            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setPedido(pedido);
            itemPedido.setProduto(produto);
            itemPedido.setQuantidade(i.getQuantidade());
            itemPedido.setPrecoUnitario(produto.getPreco());

            return itemPedido;
        }).collect(Collectors.toList());

        pedido.setItens(itens);

        BigDecimal total = itens.stream()
                .map(i -> i.getPrecoUnitario().multiply(BigDecimal.valueOf(i.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        pedido.setTotal(total);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        // AUDITORIA — pedido criado
        auditoriaService.registrar(
                username,
                "CREATE_ORDER",
                "Order ID=" + pedidoSalvo.getId() + " created with total R$" + total
        );

        return mapToResponse(pedidoSalvo);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listarPedidosPorUsuario(String username) {

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<PedidoResponse> lista = pedidoRepository.findAllByUsuario(usuario).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        // AUDITORIA — listagem
        auditoriaService.registrar(
                username,
                "LIST_ORDERS",
                "Listed " + lista.size() + " orders"
        );

        return lista;
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscarPedidoPorId(Long id, String username) {

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!pedido.getUsuario().getUsername().equals(username)) {
            throw new IllegalArgumentException("Access denied");
        }

        // AUDITORIA — consulta pedido
        auditoriaService.registrar(
                username,
                "VIEW_ORDER",
                "Viewed order ID=" + id
        );

        return mapToResponse(pedido);
    }

    private PedidoResponse mapToResponse(Pedido pedido) {
        List<PedidoResponse.ItemResponse> itensResponse = pedido.getItens().stream()
                .map(i -> PedidoResponse.ItemResponse.builder()
                        .produtoNome(i.getProduto().getNome())
                        .quantidade(i.getQuantidade())
                        .precoUnitario(i.getPrecoUnitario())
                        .build())
                .collect(Collectors.toList());

        return PedidoResponse.builder()
                .id(pedido.getId())
                .usuario(pedido.getUsuario().getUsername())
                .dataCriacao(pedido.getDataCriacao())
                .total(pedido.getTotal())
                .itens(itensResponse)
                .build();
    }
}
