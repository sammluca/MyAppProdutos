package br.com.fabreum.AppProdutos.controller;

import br.com.fabreum.AppProdutos.entity.Usuario;
import br.com.fabreum.AppProdutos.service.UsuarioService;
import br.com.fabreum.AppProdutos.service.AuditoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final AuditoriaService auditoriaService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService, AuditoriaService auditoriaService) {
        this.usuarioService = usuarioService;
        this.auditoriaService = auditoriaService;
    }

    // Endpoint para listar todos os usuários
    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.listarUsuarios();

        // Registrar auditoria com username do JWT
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        auditoriaService.registrar(username, "LIST_USERS", "Listed all users");

        return ResponseEntity.ok(usuarios);
    }

    // Endpoint para cadastrar um novo usuário
    @PostMapping
    public ResponseEntity<Usuario> cadastrarUsuario(@RequestBody Usuario usuario) {
        Usuario salvo = usuarioService.salvarUsuario(usuario);

        // Registrar auditoria com username do JWT
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        auditoriaService.registrar(username, "CREATE_USER", "Created user: " + salvo.getUsername());

        return ResponseEntity.ok(salvo);
    }
}
