package br.com.fabreum.AppProdutos.service;

import br.com.fabreum.AppProdutos.model.Produtos;
import br.com.fabreum.AppProdutos.repository.ProdutosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final ProdutosRepository produtosRepository;
    private final AuditoriaService auditoriaService;

    public Produtos adicionarEstoque(Long produtoId, int quantidade, String username) {
        Produtos produto = produtosRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + quantidade);
        Produtos salvo = produtosRepository.save(produto);

        // Auditoria
        auditoriaService.registrar(
                username != null ? username : "sistema",
                "ADD_STOCK",
                "Added " + quantidade + " units to product ID " + produtoId
        );

        return salvo;
    }

    public Produtos removerEstoque(Long produtoId, int quantidade, String username) {
        Produtos produto = produtosRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (produto.getQuantidadeEstoque() < quantidade) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidade);
        Produtos salvo = produtosRepository.save(produto);

        // Auditoria
        auditoriaService.registrar(
                username != null ? username : "sistema",
                "REMOVE_STOCK",
                "Removed " + quantidade + " units from product ID " + produtoId
        );

        return salvo;
    }
}
