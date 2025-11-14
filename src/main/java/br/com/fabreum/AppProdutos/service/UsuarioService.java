package br.com.fabreum.AppProdutos.service;

import br.com.fabreum.AppProdutos.entity.Usuario;
import br.com.fabreum.AppProdutos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Lista todos os usuários cadastrados
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    // Busca usuário pelo username
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    // Salva um novo usuário (ou atualiza se já existir) com senha criptografada
    public Usuario salvarUsuario(Usuario usuario) {
        // Encrypt password if not already encoded
        if (usuario.getPassword() != null && !usuario.getPassword().startsWith("$2a$")) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        return usuarioRepository.save(usuario);
    }
}

