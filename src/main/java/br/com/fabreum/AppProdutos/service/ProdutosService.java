package br.com.fabreum.AppProdutos.service;

import br.com.fabreum.AppProdutos.model.Produtos;
import br.com.fabreum.AppProdutos.repository.ProdutosRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProdutosService {

    private final ProdutosRepository produtosRepository;

    public Optional<Produtos> atualizaProduto(Produtos produto) {
        log.info("Atualizando produto: {}", produto);

        return produtosRepository.findById(produto.getId())
                .map(p -> {
                    // mantém dados que NÃO devem ser alterados pelo cliente
                    produto.setCodigoBarras(p.getCodigoBarras());

                    // mantém a categoria existente se a nova vier null
                    if (produto.getCategoria() == null) {
                        produto.setCategoria(p.getCategoria());
                    }

                    return produtosRepository.saveAndFlush(produto);
                });
    }

}
