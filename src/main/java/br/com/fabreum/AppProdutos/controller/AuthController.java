package br.com.fabreum.AppProdutos.controller;

import br.com.fabreum.AppProdutos.entity.Usuario;
import br.com.fabreum.AppProdutos.service.AuthService;
import br.com.fabreum.AppProdutos.service.UsuarioService;
import br.com.fabreum.AppProdutos.service.dto.LoginRequest;
import br.com.fabreum.AppProdutos.service.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(AuthService authService,
                          UsuarioService usuarioService,
                          PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario usuario) {
        try {
            // criptografa a senha
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            usuarioService.salvarUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuário criado com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao criar usuário: " + e.getMessage());
        }
    }
}
