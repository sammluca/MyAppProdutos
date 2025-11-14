package br.com.fabreum.AppProdutos.service;

import br.com.fabreum.AppProdutos.model.Categoria;
import br.com.fabreum.AppProdutos.repository.CategoriaRepository;
import br.com.fabreum.AppProdutos.service.dto.CategoriaRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoriaServiceTest {

    @InjectMocks
    private CategoriaService categoriaService;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private AuditoriaService auditoriaService;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCriarCategoria_Sucesso() throws Exception {
        String usuario = "admin";
        CategoriaRequest request = new CategoriaRequest();
        request.setNome("Eletronico");
        request.setCategoriaPaiId(null);

        Categoria categoriaSalva = Categoria.builder().id(1L).nome("Eletronico").build();

        when(categoriaRepository.findByNome("Eletronico")).thenReturn(Optional.empty());
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaSalva);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        Categoria result = categoriaService.criarCategoria(request, usuario);

        assertNotNull(result);
        assertEquals("Eletronico", result.getNome());
        verify(auditoriaService, times(1))
                .registrar(eq(usuario), eq("CREATE_CATEGORY"), anyString(), isNull(), anyString());
    }

    @Test
    void testCriarCategoria_Duplicada() {
        String usuario = "admin";
        CategoriaRequest request = new CategoriaRequest();
        request.setNome("Eletronico");

        Categoria existente = Categoria.builder().id(1L).nome("Eletronico").build();

        when(categoriaRepository.findByNome("Eletronico")).thenReturn(Optional.of(existente));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                categoriaService.criarCategoria(request, usuario));

        assertEquals("Categoria já existe", exception.getMessage());
    }

    @Test
    void testAtualizarCategoria_Sucesso() throws Exception {
        Long id = 1L;
        String usuario = "admin";

        CategoriaRequest request = new CategoriaRequest();
        request.setNome("Informática");
        request.setCategoriaPaiId(null);

        Categoria existente = Categoria.builder().id(id).nome("Eletronico").build();

        when(categoriaRepository.findById(id)).thenReturn(Optional.of(existente));
        when(categoriaRepository.findByNomeAndCategoriaPaiId("Informática", null)).thenReturn(Optional.empty());
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(existente);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        Categoria result = categoriaService.atualizarCategoria(id, request, usuario);

        assertNotNull(result);
        assertEquals("Informática", result.getNome());
        verify(auditoriaService, times(1))
                .registrar(eq(usuario), eq("UPDATE_CATEGORY"), anyString(), anyString(), anyString());
    }

    @Test
    void testDeletarCategoria_Sucesso() throws Exception {
        Long id = 1L;
        String usuario = "admin";

        Categoria existente = Categoria.builder().id(id).nome("Eletronico").build();

        when(categoriaRepository.findById(id)).thenReturn(Optional.of(existente));
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        categoriaService.deletarCategoria(id, usuario);

        verify(categoriaRepository, times(1)).deleteById(id);
        verify(auditoriaService, times(1))
                .registrar(eq(usuario), eq("DELETE_CATEGORY"), anyString(), anyString(), isNull());
    }

    @Test
    void testBuscarPorId_Sucesso() throws Exception {
        Long id = 1L;
        String usuario = "admin";

        Categoria existente = Categoria.builder().id(id).nome("Eletronico").build();

        when(categoriaRepository.findById(id)).thenReturn(Optional.of(existente));
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        Categoria result = categoriaService.buscarPorId(id, usuario);

        assertNotNull(result);
        assertEquals("Eletronico", result.getNome());
        verify(auditoriaService, times(1))
                .registrar(eq(usuario), eq("VIEW_CATEGORY"), anyString(), isNull(), anyString());
    }
}
