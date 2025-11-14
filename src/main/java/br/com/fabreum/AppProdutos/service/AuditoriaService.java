package br.com.fabreum.AppProdutos.service;

import br.com.fabreum.AppProdutos.model.Auditoria;
import br.com.fabreum.AppProdutos.repository.AuditoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public void registrar(String usuario, String acao, String detalhe) {
        Auditoria audit = Auditoria.builder()
                .usuario(usuario)
                .acao(acao)
                .detalhe(detalhe)
                .build();

        auditoriaRepository.save(audit);
    }
}
