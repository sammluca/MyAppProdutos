package br.com.fabreum.AppProdutos.controller;

import br.com.fabreum.AppProdutos.model.Categoria;
import br.com.fabreum.AppProdutos.service.CategoriaService;
import br.com.fabreum.AppProdutos.service.dto.CategoriaRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoriaControllerTest {

    @InjectMocks
    private CategoriaController categoriaController;

    @Mock
    private CategoriaService categoriaService;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(authentication.getName()).thenReturn("admin");
    }

    @Test
    void testListarCategorias() {
        Categoria cat1 = new Categoria();
        cat1.setId(1L);
        cat1.setNome("Eletronico");

        Categoria cat2 = new Categoria();
        cat2.setId(2L);
        cat2.setNome("Informatica");

        when(categoriaService.listarTodas("admin")).thenReturn(Arrays.asList(cat1, cat2));

        ResponseEntity<List<Categoria>> response = categoriaController.listarCategorias(authentication);

        assertNotNull(response);
        assertEquals(2, response.getBody().size());
        verify(categoriaService, times(1)).listarTodas("admin");
    }

    @Test
    void testCriarCategoria() throws Exception {
        CategoriaRequest request = new CategoriaRequest();
        request.setNome("Eletronico");

        Categoria categoriaSalva = new Categoria();
        categoriaSalva.setId(1L);
        categoriaSalva.setNome("Eletronico");

        when(categoriaService.criarCategoria(request, "admin")).thenReturn(categoriaSalva);

        ResponseEntity<Categoria> response = categoriaController.criarCategoria(request, authentication);

        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("Eletronico", response.getBody().getNome());
        verify(categoriaService, times(1)).criarCategoria(request, "admin");
    }

    @Test
    void testBuscarPorId() throws Exception {
        Categoria cat = new Categoria();
        cat.setId(1L);
        cat.setNome("Eletronico");

        when(categoriaService.buscarPorId(1L, "admin")).thenReturn(cat);

        ResponseEntity<Categoria> response = categoriaController.buscarPorId(1L, authentication);

        assertNotNull(response);
        assertEquals("Eletronico", response.getBody().getNome());
        verify(categoriaService, times(1)).buscarPorId(1L, "admin");
    }

    @Test
    void testAtualizarCategoria() throws Exception {
        CategoriaRequest request = new CategoriaRequest();
        request.setNome("Informatica");

        Categoria catAtualizada = new Categoria();
        catAtualizada.setId(1L);
        catAtualizada.setNome("Informatica");

        when(categoriaService.atualizarCategoria(1L, request, "admin")).thenReturn(catAtualizada);

        ResponseEntity<Categoria> response = categoriaController.atualizarCategoria(1L, request, authentication);

        assertNotNull(response);
        assertEquals("Informatica", response.getBody().getNome());
        verify(categoriaService, times(1)).atualizarCategoria(1L, request, "admin");
    }

    @Test
    void testDeletarCategoria() throws Exception {
        doNothing().when(categoriaService).deletarCategoria(1L, "admin");

        ResponseEntity<Void> response = categoriaController.deletarCategoria(1L, authentication);

        assertNotNull(response);
        assertEquals(204, response.getStatusCodeValue());
        verify(categoriaService, times(1)).deletarCategoria(1L, "admin");
    }
}
