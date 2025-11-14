package br.com.fabreum.AppProdutos.service;

import br.com.fabreum.AppProdutos.service.dto.LoginRequest;
import br.com.fabreum.AppProdutos.service.dto.LoginResponse;
import br.com.fabreum.AppProdutos.entity.Usuario;
import br.com.fabreum.AppProdutos.security.JwtUtil;
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

    // Autentica usuário e retorna token
    public LoginResponse login(LoginRequest request) throws Exception {
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorUsername(request.getUsername());

        if (usuarioOpt.isEmpty()) {
            throw new Exception("Usuário não encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        // Compara senha usando BCrypt
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new Exception("Senha inválida");
        }


        // Gera token JWT
        String token = jwtUtil.gerarToken(usuario.getUsername());

        return new LoginResponse(usuario.getUsername(), token);
    }
}
