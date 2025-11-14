package br.com.fabreum.AppProdutos.controller;

import br.com.fabreum.AppProdutos.model.Produtos;
import br.com.fabreum.AppProdutos.service.EstoqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/estoque")
@RequiredArgsConstructor
public class EstoqueController {

    private final EstoqueService estoqueService;

    @PutMapping("/adicionar/{idProduto}")
    public ResponseEntity<Produtos> adicionarEstoque(@PathVariable Long idProduto,
                                                     @RequestParam int quantidade,
                                                     Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "sistema";
        Produtos produtoAtualizado = estoqueService.adicionarEstoque(idProduto, quantidade, username);
        return ResponseEntity.ok(produtoAtualizado);
    }

    @PutMapping("/remover/{idProduto}")
    public ResponseEntity<Produtos> removerEstoque(@PathVariable Long idProduto,
                                                   @RequestParam int quantidade,
                                                   Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "sistema";
        Produtos produtoAtualizado = estoqueService.removerEstoque(idProduto, quantidade, username);
        return ResponseEntity.ok(produtoAtualizado);
    }
}
