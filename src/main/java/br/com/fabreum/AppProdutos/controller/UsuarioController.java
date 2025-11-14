package br.com.fabreum.AppProdutos.controller;

import br.com.fabreum.AppProdutos.entity.Usuario;
import br.com.fabreum.AppProdutos.service.AuditoriaService;
import br.com.fabreum.AppProdutos.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final AuditoriaService auditoriaService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Usuario>> listarUsuarios(Authentication authentication) {
        List<Usuario> usuarios = usuarioService.listarUsuarios();

        // Registrar auditoria
        String username = authentication.getName();
        auditoriaService.registrar(username, "LIST_USERS", "Listed all users");

        return ResponseEntity.ok(usuarios);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Usuario> cadastrarUsuario(@RequestBody Usuario usuario,
                                                    Authentication authentication) {
        Usuario salvo = usuarioService.salvarUsuario(usuario);

        // Registrar auditoria
        String username = authentication.getName();
        auditoriaService.registrar(username, "CREATE_USER", "Created user: " + salvo.getUsername());

        return ResponseEntity.ok(salvo);
    }
}
