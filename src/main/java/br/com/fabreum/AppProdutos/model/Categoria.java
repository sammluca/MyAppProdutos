package br.com.fabreum.AppProdutos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_categoria", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"nome", "categoria_pai_id"})
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    // Categoria pai
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_pai_id")
    @JsonIgnoreProperties({"categoriaPai", "subCategorias", "produtos"})
    private Categoria categoriaPai;

    // Subcategorias - NÃO mostrar no JSON para evitar loop
    @OneToMany(mappedBy = "categoriaPai")
    @JsonIgnore
    private List<Categoria> subCategorias;

    // Produtos desta categoria - também não mostrar no JSON
    @OneToMany(mappedBy = "categoria")
    @JsonIgnore
    private List<Produtos> produtos;
}
