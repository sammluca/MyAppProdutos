package br.com.fabreum.AppProdutos.service;

import br.com.fabreum.AppProdutos.model.Auditoria;
import br.com.fabreum.AppProdutos.repository.AuditoriaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final ObjectMapper objectMapper; // para converter objetos em JSON

    public void registrar(String usuario, String acao, String detalhe) {
        registrar(usuario, acao, detalhe, null, null);
    }

    public void registrar(String usuario, String acao, String detalhe, Object before, Object after) {
        Auditoria audit = new Auditoria();
        audit.setUsuario(usuario);
        audit.setAcao(acao);
        audit.setDetalhe(detalhe);

        try {
            if (before != null) audit.setBeforeJson(objectMapper.writeValueAsString(before));
            if (after != null) audit.setAfterJson(objectMapper.writeValueAsString(after));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        auditoriaRepository.save(audit);
    }
}
