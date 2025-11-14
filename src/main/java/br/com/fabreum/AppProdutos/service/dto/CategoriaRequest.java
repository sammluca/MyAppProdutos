package br.com.fabreum.AppProdutos.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaRequest {
    private String nome;
    private Long categoriaPaiId;
}
