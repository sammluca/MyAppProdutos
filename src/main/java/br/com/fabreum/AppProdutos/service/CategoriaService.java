package br.com.fabreum.AppProdutos.service;

import br.com.fabreum.AppProdutos.model.Categoria;
import br.com.fabreum.AppProdutos.repository.CategoriaRepository;
import br.com.fabreum.AppProdutos.service.dto.CategoriaRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final AuditoriaService auditoriaService;
    private final ObjectMapper objectMapper;

    public List<Categoria> listarTodas(String username) {
        List<Categoria> categorias = categoriaRepository.findAll();
        auditoriaService.registrar(username, "LIST_CATEGORIES",
                "Listou " + categorias.size() + " categorias", null, null);
        return categorias;
    }

    @Transactional
    public Categoria criarCategoria(CategoriaRequest request, String username) throws Exception {
        if (categoriaRepository.findByNome(request.getNome()).isPresent()) {
            throw new IllegalArgumentException("Categoria já existe");
        }

        Categoria categoria = Categoria.builder()
                .nome(request.getNome())
                .categoriaPai(request.getCategoriaPaiId() != null
                        ? categoriaRepository.findById(request.getCategoriaPaiId())
                        .orElseThrow(() -> new RuntimeException("Categoria pai não encontrada"))
                        : null)
                .build();

        Categoria salvo = categoriaRepository.save(categoria);

        auditoriaService.registrar(username, "CREATE_CATEGORY",
                "Categoria criada ID=" + salvo.getId(),
                null,
                objectMapper.writeValueAsString(salvo));

        return salvo;
    }

    @Transactional
    public Categoria atualizarCategoria(Long id, CategoriaRequest request, String username) throws Exception {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        String beforeJson = objectMapper.writeValueAsString(categoria);

        Long categoriaPaiId = request.getCategoriaPaiId();
        boolean existe = categoriaRepository.findByNomeAndCategoriaPaiId(request.getNome(), categoriaPaiId).isPresent();
        if (existe && !categoria.getNome().equals(request.getNome())) {
            throw new IllegalArgumentException("Categoria com mesmo nome já existe no mesmo nível");
        }

        categoria.setNome(request.getNome());
        categoria.setCategoriaPai(categoriaPaiId != null
                ? categoriaRepository.findById(categoriaPaiId).orElse(null)
                : null);

        Categoria salvo = categoriaRepository.save(categoria);

        auditoriaService.registrar(username, "UPDATE_CATEGORY",
                "Atualizou categoria ID=" + id,
                beforeJson,
                objectMapper.writeValueAsString(salvo));

        return salvo;
    }

    @Transactional
    public void deletarCategoria(Long id, String username) throws Exception {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        String beforeJson = objectMapper.writeValueAsString(categoria);

        categoriaRepository.deleteById(id);

        auditoriaService.registrar(username, "DELETE_CATEGORY",
                "Deletou categoria ID=" + id,
                beforeJson,
                null);
    }

    public Categoria buscarPorId(Long id, String username) throws Exception {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        auditoriaService.registrar(username, "VIEW_CATEGORY",
                "Visualizou categoria ID=" + id,
                null,
                objectMapper.writeValueAsString(categoria));

        return categoria;
    }
}
