package br.com.fabreum.AppProdutos.service;

import br.com.fabreum.AppProdutos.model.Categoria;
import br.com.fabreum.AppProdutos.repository.CategoriaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final AuditoriaService auditoriaService;
    private final ObjectMapper objectMapper;

    public List<Categoria> listarTodas(String username) {
        List<Categoria> categorias = categoriaRepository.findAll();
        auditoriaService.registrar(username, "LIST_CATEGORIES", "Listou " + categorias.size() + " categorias", null, null);
        return categorias;
    }

    public Categoria criarCategoria(Categoria categoria, String username) throws Exception {
        Long categoriaPaiId = categoria.getCategoriaPai() != null ? categoria.getCategoriaPai().getId() : null;
        boolean existe = categoriaRepository.findByNomeAndCategoriaPaiId(categoria.getNome(), categoriaPaiId).isPresent();
        if (existe) throw new IllegalArgumentException("Categoria com mesmo nome já existe no mesmo nível");

        Categoria salvo = categoriaRepository.save(categoria);

        auditoriaService.registrar(
                username,
                "CREATE_CATEGORY",
                "Criou categoria: " + salvo.getNome() + " com ID=" + salvo.getId(),
                null,
                objectMapper.writeValueAsString(salvo)
        );

        return salvo;
    }

    public Categoria atualizarCategoria(Long id, Categoria atualizada, String username) throws Exception {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        String beforeJson = objectMapper.writeValueAsString(categoria);

        Long categoriaPaiId = atualizada.getCategoriaPai() != null ? atualizada.getCategoriaPai().getId() : null;
        boolean existe = categoriaRepository.findByNomeAndCategoriaPaiId(atualizada.getNome(), categoriaPaiId).isPresent();
        if (existe && !categoria.getNome().equals(atualizada.getNome())) {
            throw new IllegalArgumentException("Categoria com mesmo nome já existe no mesmo nível");
        }

        categoria.setNome(atualizada.getNome());
        categoria.setCategoriaPai(atualizada.getCategoriaPai());
        Categoria salvo = categoriaRepository.save(categoria);

        auditoriaService.registrar(
                username,
                "UPDATE_CATEGORY",
                "Atualizou categoria ID=" + id,
                beforeJson,
                objectMapper.writeValueAsString(salvo)
        );

        return salvo;
    }

    public void deletarCategoria(Long id, String username) throws Exception {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        String beforeJson = objectMapper.writeValueAsString(categoria);
        categoriaRepository.deleteById(id);

        auditoriaService.registrar(
                username,
                "DELETE_CATEGORY",
                "Deletou categoria ID=" + id,
                beforeJson,
                null
        );
    }

    public Categoria buscarPorId(Long id, String username) throws Exception {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        auditoriaService.registrar(
                username,
                "VIEW_CATEGORY",
                "Visualizou categoria ID=" + id,
                null,
                objectMapper.writeValueAsString(categoria)
        );

        return categoria;
    }
}
