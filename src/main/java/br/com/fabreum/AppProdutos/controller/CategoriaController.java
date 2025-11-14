package br.com.fabreum.AppProdutos.controller;

import br.com.fabreum.AppProdutos.model.Categoria;
import br.com.fabreum.AppProdutos.service.CategoriaService;
import br.com.fabreum.AppProdutos.service.dto.CategoriaRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    // Listar todas as categorias
    @GetMapping
    public ResponseEntity<List<Categoria>> listarCategorias(Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "sistema";
        return ResponseEntity.ok(categoriaService.listarTodas(username));
    }

    // Criar categoria — ADMIN
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Categoria> criarCategoria(@RequestBody CategoriaRequest request,
                                                    Authentication authentication) throws Exception {
        String username = authentication != null ? authentication.getName() : "sistema";
        Categoria saved = categoriaService.criarCategoria(request, username);
        return ResponseEntity.status(201).body(saved);
    }

    // Buscar categoria por ID
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> buscarPorId(@PathVariable Long id,
                                                 Authentication authentication) throws Exception {
        String username = authentication != null ? authentication.getName() : "sistema";
        Categoria categoria = categoriaService.buscarPorId(id, username);
        return ResponseEntity.ok(categoria);
    }

    // Atualizar categoria — ADMIN
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Categoria> atualizarCategoria(@PathVariable Long id,
                                                        @RequestBody CategoriaRequest request,
                                                        Authentication authentication) throws Exception {
        String username = authentication != null ? authentication.getName() : "sistema";
        Categoria updated = categoriaService.atualizarCategoria(id, request, username);
        return ResponseEntity.ok(updated);
    }

    // Deletar categoria — ADMIN
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarCategoria(@PathVariable Long id,
                                                 Authentication authentication) throws Exception {
        String username = authentication != null ? authentication.getName() : "sistema";
        categoriaService.deletarCategoria(id, username);
        return ResponseEntity.noContent().build();
    }
}
