package br.com.fabreum.AppProdutos.repository;

import br.com.fabreum.AppProdutos.entity.Usuario;
import br.com.fabreum.AppProdutos.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuarioId(Long usuarioId);
    List<Pedido> findAllByUsuario(Usuario usuario);
}
