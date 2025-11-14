package br.com.fabreum.AppProdutos.repository;

import br.com.fabreum.AppProdutos.model.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
}
