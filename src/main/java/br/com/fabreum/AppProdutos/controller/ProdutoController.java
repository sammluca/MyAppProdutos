package br.com.fabreum.AppProdutos.controller;

import br.com.fabreum.AppProdutos.model.Categoria;
import br.com.fabreum.AppProdutos.model.Produtos;
import br.com.fabreum.AppProdutos.repository.CategoriaRepository;
import br.com.fabreum.AppProdutos.repository.ProdutosRepository;
import br.com.fabreum.AppProdutos.service.ProdutosService;
import br.com.fabreum.AppProdutos.service.dto.ProdutoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/produtos/")
public class ProdutoController {

    private final ProdutosRepository produtosRepository;
    private final ProdutosService produtosService;
    private final CategoriaRepository categoriaRepository;

    @PostMapping("produto")
    public ResponseEntity<Produtos> criaProduto(@RequestBody Produtos produto) {
        if (produto.getCategoria() != null && produto.getCategoria().getId() != null) {
            Categoria categoria = categoriaRepository.findById(produto.getCategoria().getId())
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
            produto.setCategoria(categoria);
        }

        Produtos saved = produtosRepository.save(produto);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<Produtos>> listaProdutos() {
        return ResponseEntity.ok(produtosRepository.findAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<Produtos> listaProdutoPorId(@PathVariable Long id) {
        Produtos produto = produtosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        return ResponseEntity.ok(produto);
    }

    @GetMapping("/dto/{id}")
    public ResponseEntity<ProdutoDto> listaProdutoDtoPorId(@PathVariable Long id) {
        ProdutoDto produtoDto = produtosRepository.findByIdDto(id);
        return ResponseEntity.ok(produtoDto);
    }

    @PutMapping("atualiza")
    public ResponseEntity<Produtos> atualizaProduto(@RequestBody Produtos produto) {
        if (produto.getCategoria() != null && produto.getCategoria().getId() != null) {
            Categoria categoria = categoriaRepository.findById(produto.getCategoria().getId())
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
            produto.setCategoria(categoria);
        }

        Produtos atualizado = produtosService.atualizaProduto(produto)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado para atualizar"));

        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletaProduto(@PathVariable Long id) {
        produtosRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}

