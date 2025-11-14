package br.com.fabreum.AppProdutos.repository;

import br.com.fabreum.AppProdutos.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    // Verifica se já existe categoria com o mesmo nome no mesmo nível
    Optional<Categoria> findByNomeAndCategoriaPaiId(String nome, Long categoriaPaiId);

    Optional<Categoria> findByNome(String nome);
}
