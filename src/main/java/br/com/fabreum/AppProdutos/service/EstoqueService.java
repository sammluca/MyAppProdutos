package br.com.fabreum.AppProdutos.service;

import br.com.fabreum.AppProdutos.model.Produtos;
import br.com.fabreum.AppProdutos.repository.ProdutosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EstoqueService {

    @Autowired
    private ProdutosRepository produtosRepository;

    public Produtos adicionarEstoque(Long produtoId, int quantidade) {
        Optional<Produtos> produtoOpt = produtosRepository.findById(produtoId);

        if (produtoOpt.isEmpty()) {
            throw new RuntimeException("Produto não encontrado");
        }

        Produtos produto = produtoOpt.get();
        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + quantidade);

        return produtosRepository.save(produto);
    }

    public Produtos removerEstoque(Long produtoId, int quantidade) {
        Optional<Produtos> produtoOpt = produtosRepository.findById(produtoId);

        if (produtoOpt.isEmpty()) {
            throw new RuntimeException("Produto não encontrado");
        }

        Produtos produto = produtoOpt.get();

        if (produto.getQuantidadeEstoque() < quantidade) {
            throw new RuntimeException("Estoque insuficiente");
        }

        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidade);

        return produtosRepository.save(produto);
    }
}
