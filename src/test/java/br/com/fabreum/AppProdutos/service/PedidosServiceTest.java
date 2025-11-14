package br.com.fabreum.AppProdutos.service;

import br.com.fabreum.AppProdutos.entity.Usuario;
import br.com.fabreum.AppProdutos.model.ItemPedido;
import br.com.fabreum.AppProdutos.model.Pedido;
import br.com.fabreum.AppProdutos.model.Produtos;
import br.com.fabreum.AppProdutos.repository.InventoryTransactionRepository;
import br.com.fabreum.AppProdutos.repository.PedidoRepository;
import br.com.fabreum.AppProdutos.repository.ProdutosRepository;
import br.com.fabreum.AppProdutos.repository.UsuarioRepository;
import br.com.fabreum.AppProdutos.service.dto.ItemPedidoRequest;
import br.com.fabreum.AppProdutos.service.dto.PedidoRequest;
import br.com.fabreum.AppProdutos.service.dto.PedidoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private ProdutosRepository produtosRepository;
    @Mock
    private InventoryTransactionRepository transactionRepository;
    @Mock
    private AuditoriaService auditoriaService;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PedidoService pedidoService;

    private Usuario usuario;
    private Produtos produto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("user1");

        produto = new Produtos();
        produto.setId(1L);
        produto.setNome("Produto A");
        produto.setQuantidadeEstoque(10);
        produto.setPreco(new BigDecimal("5.00"));
    }

    @Test
    void criarPedidoComEstoqueSuficiente_DeveRetornarPedido() throws Exception {
        when(usuarioRepository.findByUsername("user1")).thenReturn(Optional.of(usuario));
        when(produtosRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArgument(0));

        PedidoRequest request = new PedidoRequest();
        ItemPedidoRequest item = new ItemPedidoRequest();
        item.setProdutoId(1L);
        item.setQuantidade(2);
        request.setItens(List.of(item));

        PedidoResponse response = pedidoService.criarPedido(request, "user1");

        assertEquals("user1", response.getUsuario());
        assertEquals(1, response.getItens().size());
        assertEquals(new BigDecimal("10.00"), response.getTotal());

        // Verifica se estoque foi descontado
        assertEquals(8, produto.getQuantidadeEstoque());

        // Auditoria chamada
        verify(auditoriaService, atLeastOnce()).registrar(eq("user1"), anyString(), anyString(), any(), any());
    }

    @Test
    void criarPedidoComEstoqueInsuficiente_DeveLancarExcecao() {
        when(usuarioRepository.findByUsername("user1")).thenReturn(Optional.of(usuario));
        produto.setQuantidadeEstoque(1);
        when(produtosRepository.findById(1L)).thenReturn(Optional.of(produto));

        PedidoRequest request = new PedidoRequest();
        ItemPedidoRequest item = new ItemPedidoRequest();
        item.setProdutoId(1L);
        item.setQuantidade(2);
        request.setItens(List.of(item));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                pedidoService.criarPedido(request, "user1"));

        assertTrue(exception.getMessage().contains("Estoque insuficiente"));
    }
}

