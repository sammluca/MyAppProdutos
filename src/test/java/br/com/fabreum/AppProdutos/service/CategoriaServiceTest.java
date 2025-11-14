package br.com.fabreum.AppProdutos.service;

import br.com.fabreum.AppProdutos.model.Categoria;
import br.com.fabreum.AppProdutos.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private AuditoriaService auditoriaService;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        categoria = Categoria.builder()
                .id(1L)
                .nome("Eletronico")
                .build();
    }

    @Test
    void testListarTodas() throws Exception {
        List<Categoria> lista = List.of(categoria);
        when(categoriaRepository.findAll()).thenReturn(lista);

        List<Categoria> result = categoriaService.listarTodas("admin");

        assertEquals(1, result.size());
        verify(auditoriaService, times(1))
                .registrar(eq("admin"), eq("LIST_CATEGORIES"), anyString(), isNull(), isNull());
    }

    @Test
    void testCriarCategoria_Sucesso() throws Exception {
        when(categoriaRepository.findByNomeAndCategoriaPaiId(anyString(), any())).thenReturn(Optional.empty());
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        Categoria result = categoriaService.criarCategoria(categoria, "admin");

        assertEquals("Eletronico", result.getNome());
        verify(auditoriaService, times(1))
                .registrar(eq("admin"), eq("CREATE_CATEGORY"), contains("Criou categoria"), isNull(), eq(categoria));
    }

    @Test
    void testCriarCategoria_Duplicada() {
        when(categoriaRepository.findByNomeAndCategoriaPaiId(anyString(), any())).thenReturn(Optional.of(categoria));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                categoriaService.criarCategoria(categoria, "admin"));

        assertEquals("Categoria com mesmo nome já existe no mesmo nível", exception.getMessage());
    }

    @Test
    void testAtualizarCategoria_Sucesso() throws Exception {
        Categoria atualizada = Categoria.builder().nome("EletronicoAtualizado").build();

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.findByNomeAndCategoriaPaiId(anyString(), any())).thenReturn(Optional.empty());
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(atualizada);

        Categoria result = categoriaService.atualizarCategoria(1L, atualizada, "admin");

        assertEquals("EletronicoAtualizado", result.getNome());
        verify(auditoriaService, times(1))
                .registrar(eq("admin"), eq("UPDATE_CATEGORY"), contains("Atualizou categoria"), anyString(), eq(atualizada));
    }

    @Test
    void testDeletarCategoria_Sucesso() throws Exception {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        doNothing().when(categoriaRepository).deleteById(1L);

        categoriaService.deletarCategoria(1L, "admin");

        verify(auditoriaService, times(1))
                .registrar(eq("admin"), eq("DELETE_CATEGORY"), contains("Deletou categoria"), eq(categoria), isNull());
    }

    @Test
    void testBuscarPorId_Sucesso() throws Exception {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        Categoria result = categoriaService.buscarPorId(1L, "admin");

        assertEquals(categoria.getNome(), result.getNome());
        verify(auditoriaService, times(1))
                .registrar(eq("admin"), eq("VIEW_CATEGORY"), contains("Visualizou categoria"), isNull(), eq(categoria));
    }

    @Test
    void testBuscarPorId_NaoEncontrada() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                categoriaService.buscarPorId(1L, "admin"));

        assertEquals("Categoria não encontrada", exception.getMessage());
    }
}
