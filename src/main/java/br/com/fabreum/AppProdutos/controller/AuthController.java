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
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Usuario usuario) {
        try {
            // Encrypt password before saving
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            Usuario saved = usuarioService.salvarUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("User created successfully: " + saved.getUsername());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error creating user: " + e.getMessage());
        }
    }
}
