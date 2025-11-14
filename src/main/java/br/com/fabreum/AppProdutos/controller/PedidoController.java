package br.com.fabreum.AppProdutos.controller;

import br.com.fabreum.AppProdutos.service.PedidoService;
import br.com.fabreum.AppProdutos.service.dto.PedidoRequest;
import br.com.fabreum.AppProdutos.service.dto.PedidoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    // Criar um novo pedido — apenas CUSTOMER
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PedidoResponse> criarPedido(@RequestBody PedidoRequest pedidoRequest,
                                                      Authentication authentication) throws Exception {
        String username = authentication.getName();
        PedidoResponse response = pedidoService.criarPedido(pedidoRequest, username);
        return ResponseEntity.status(201).body(response);
    }

    // Listar todos os pedidos do usuário logado — apenas CUSTOMER
    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<PedidoResponse>> listarPedidos(Authentication authentication) {
        String username = authentication.getName();
        List<PedidoResponse> pedidos = pedidoService.listarPedidosPorUsuario(username);
        return ResponseEntity.ok(pedidos);
    }

    // Buscar pedido por ID — apenas CUSTOMER
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PedidoResponse> buscarPedidoPorId(@PathVariable Long id,
                                                            Authentication authentication) throws Exception {
        String username = authentication.getName();
        PedidoResponse pedido = pedidoService.buscarPedidoPorId(id, username);
        return ResponseEntity.ok(pedido);
    }
}
