package br.com.fabreum.AppProdutos.service;

import br.com.fabreum.AppProdutos.entity.Usuario;
import br.com.fabreum.AppProdutos.model.InventoryTransaction;
import br.com.fabreum.AppProdutos.model.ItemPedido;
import br.com.fabreum.AppProdutos.model.Pedido;
import br.com.fabreum.AppProdutos.model.Produtos;
import br.com.fabreum.AppProdutos.repository.InventoryTransactionRepository;
import br.com.fabreum.AppProdutos.repository.PedidoRepository;
import br.com.fabreum.AppProdutos.repository.ProdutosRepository;
import br.com.fabreum.AppProdutos.repository.UsuarioRepository;
import br.com.fabreum.AppProdutos.service.dto.PedidoRequest;
import br.com.fabreum.AppProdutos.service.dto.PedidoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final InventoryTransactionRepository transactionRepository;
    private final AuditoriaService auditoriaService;
    private final ObjectMapper objectMapper;

    @Transactional
    public PedidoResponse criarPedido(PedidoRequest pedidoRequest, String username) throws Exception {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);

        List<ItemPedido> itens = pedidoRequest.getItens().stream().map(i -> {
            Produtos produto = produtosRepository.findById(i.getProdutoId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + i.getProdutoId()));

            // Validação estoque
            if (produto.getQuantidadeEstoque() < i.getQuantidade())
                throw new IllegalArgumentException("Estoque insuficiente para o produto " + produto.getNome());

            try {
                String beforeJson = objectMapper.writeValueAsString(produto);
                produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - i.getQuantidade());
                produtosRepository.save(produto);

                InventoryTransaction transaction = new InventoryTransaction();
                transaction.setProductId(produto.getId());
                transaction.setDelta(-i.getQuantidade());
                transaction.setReason("SAIDA");
                transaction.setCreatedBy(username);
                transactionRepository.save(transaction);

                auditoriaService.registrar(username, "REMOVE_STOCK_FROM_ORDER",
                        "Produto ID " + produto.getId() + " removido do estoque pelo pedido",
                        beforeJson,
                        objectMapper.writeValueAsString(produto));

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

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

        auditoriaService.registrar(username, "CREATE_ORDER",
                "Pedido criado ID=" + pedidoSalvo.getId() + " total=" + total,
                null,
                objectMapper.writeValueAsString(pedidoSalvo));

        return mapToResponse(pedidoSalvo);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listarPedidosPorUsuario(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<PedidoResponse> lista = pedidoRepository.findAllByUsuario(usuario).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        auditoriaService.registrar(username, "LIST_ORDERS",
                "Listou " + lista.size() + " pedidos",
                null, null);

        return lista;
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscarPedidoPorId(Long id, String username) throws Exception {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!pedido.getUsuario().getUsername().equals(username))
            throw new IllegalArgumentException("Access denied");

        auditoriaService.registrar(username, "VIEW_ORDER",
                "Visualizou pedido ID=" + id,
                null,
                objectMapper.writeValueAsString(pedido));

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
