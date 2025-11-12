package br.com.fabreum.AppProdutos.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    // Chave secreta simples para testes
    private final String SECRET_KEY = "MinhaChaveSecreta123";

    // Expiração do token  (1 hora)
    private final long EXPIRATION_TIME = 3600000;

    // Gera token para um usuário
    public String gerarToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // Extrai username do token
    public String extrairUsername(String token) {
        return obterClaims(token).getSubject();
    }

    // Valida se o token é válido
    public boolean validarToken(String token, String username) {
        String usuarioToken = extrairUsername(token);
        return (usuarioToken.equals(username) && !tokenExpirado(token));
    }

    // Verifica se expirou
    private boolean tokenExpirado(String token) {
        return obterClaims(token).getExpiration().before(new Date());
    }

    // Obtém os claims do token
    private Claims obterClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}
