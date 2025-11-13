package br.com.fabreum.AppProdutos.controller;

import br.com.fabreum.AppProdutos.model.Produtos;
import br.com.fabreum.AppProdutos.service.EstoqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/estoque")
public class EstoqueController {

    @Autowired
    private EstoqueService estoqueService;

    @PutMapping("/adicionar/{idProduto}")
    public ResponseEntity<Produtos> adicionarEstoque(@PathVariable Long idProduto, @RequestParam int quantidade) {
        Produtos produtoAtualizado = estoqueService.adicionarEstoque(idProduto, quantidade);
        return ResponseEntity.ok(produtoAtualizado);
    }

    @PutMapping("/remover/{idProduto}")
    public ResponseEntity<Produtos> removerEstoque(@PathVariable Long idProduto, @RequestParam int quantidade) {
        Produtos produtoAtualizado = estoqueService.removerEstoque(idProduto, quantidade);
        return ResponseEntity.ok(produtoAtualizado);
    }
}
