package br.com.fabreum.AppProdutos.controller;

import br.com.fabreum.AppProdutos.model.Auditoria;
import br.com.fabreum.AppProdutos.repository.AuditoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auditoria")
@RequiredArgsConstructor
public class AuditoriaController {

    private final AuditoriaRepository auditoriaRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // apenas ADMIN pode acessar
    public ResponseEntity<List<Auditoria>> listar() {
        return ResponseEntity.ok(auditoriaRepository.findAll());
    }
}
