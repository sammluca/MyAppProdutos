package br.com.fabreum.AppProdutos.service;

import br.com.fabreum.AppProdutos.entity.Usuario;
import br.com.fabreum.AppProdutos.security.JwtUtil;
import br.com.fabreum.AppProdutos.service.dto.LoginRequest;
import br.com.fabreum.AppProdutos.service.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UsuarioService usuarioService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest request) {
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorUsername(request.getUsername());

        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        Usuario usuario = usuarioOpt.get();

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        // Passa role ao gerar token
        String token = jwtUtil.gerarToken(usuario.getUsername(), usuario.getRole());

        return new LoginResponse(usuario.getUsername(), token);
    }
}
