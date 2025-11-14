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

    public Produtos adicionarEstoque(Long produtoId, int quantidade) {
        Optional<Produtos> produtoOpt = produtosRepository.findById(produtoId);

        if (produtoOpt.isEmpty()) {
            throw new RuntimeException("Produto não encontrado");
        }

        Produtos produto = produtoOpt.get();
        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + quantidade);

        Produtos salvo = produtosRepository.save(produto);

        //  Auditoria
        auditoriaService.registrar(
                "sistema",
                "ADICIONAR_ESTOQUE",
                "Adicionado " + quantidade + " unidades ao produto ID " + produtoId
        );

        return salvo;
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

        Produtos salvo = produtosRepository.save(produto);

        //  Auditoria
        auditoriaService.registrar(
                "sistema",
                "REMOVER_ESTOQUE",
                "Removido " + quantidade + " unidades do produto ID " + produtoId
        );

        return salvo;
    }
}
