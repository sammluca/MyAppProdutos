package br.com.fabreum.AppProdutos.service;

import br.com.fabreum.AppProdutos.model.InventoryTransaction;
import br.com.fabreum.AppProdutos.model.Produtos;
import br.com.fabreum.AppProdutos.repository.InventoryTransactionRepository;
import br.com.fabreum.AppProdutos.repository.ProdutosRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final ProdutosRepository produtosRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final AuditoriaService auditoriaService;
    private final ObjectMapper objectMapper;

    public Produtos adicionarEstoque(Long produtoId, int quantidade, String username) throws Exception {
        Produtos produto = produtosRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

        String beforeJson = objectMapper.writeValueAsString(produto);

        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + quantidade);
        Produtos salvo = produtosRepository.save(produto);

        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setProductId(produtoId);
        transaction.setDelta(quantidade);
        transaction.setReason("ENTRADA");
        transaction.setCreatedBy(username != null ? username : "sistema");
        transactionRepository.save(transaction);

        auditoriaService.registrar(username, "ADD_STOCK",
                "Adicionou " + quantidade + " unidades ao produto ID " + produtoId,
                beforeJson,
                objectMapper.writeValueAsString(salvo)
        );

        return salvo;
    }

    public Produtos removerEstoque(Long produtoId, int quantidade, String username) throws Exception {
        Produtos produto = produtosRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

        if (produto.getQuantidadeEstoque() < quantidade)
            throw new IllegalArgumentException("Estoque insuficiente");

        String beforeJson = objectMapper.writeValueAsString(produto);

        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidade);
        Produtos salvo = produtosRepository.save(produto);

        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setProductId(produtoId);
        transaction.setDelta(-quantidade);
        transaction.setReason("SAIDA");
        transaction.setCreatedBy(username != null ? username : "sistema");
        transactionRepository.save(transaction);

        auditoriaService.registrar(username, "REMOVE_STOCK",
                "Removeu " + quantidade + " unidades do produto ID " + produtoId,
                beforeJson,
                objectMapper.writeValueAsString(salvo)
        );

        return salvo;
    }

    public List<InventoryTransaction> listarTransacoes(Long produtoId) {
        return transactionRepository.findByProductId(produtoId);
    }
}
