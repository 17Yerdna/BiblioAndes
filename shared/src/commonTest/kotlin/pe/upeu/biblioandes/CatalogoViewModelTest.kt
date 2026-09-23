package pe.upeu.biblioandes

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.usecase.ObtenerCatalogoUseCase
import pe.upeu.biblioandes.presentation.catalogo.CatalogoViewModel
import pe.upeu.biblioandes.presentation.catalogo.FaseCatalogo
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogoViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val librosPrueba = listOf(
        Libro(1, "Kotlin en profundidad", "M. Salazar", 2023, "Programación", "Central", 3),
        Libro(2, "Cálculo aplicado", "L. Ortega", 2019, "Matemática", "Sede Norte", 2),
        Libro(3, "Redes de computadoras", "A. Medina", 2022, "Redes", "Sede Sur", 4)
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun carga_inicial_muestra_libros_y_categorias() {
        val repo = FakeTestRepository(libros = librosPrueba)
        val viewModel = CatalogoViewModel(ObtenerCatalogoUseCase(repo))

        val estado = viewModel.uiState.value
        assertTrue(estado.fase is FaseCatalogo.Contenido)
        assertEquals(3, estado.librosFiltrados.size)
        assertEquals(3, estado.categorias.size)
    }

    @Test
    fun filtro_por_busqueda_es_tolerante_a_tildes_y_mayusculas() {
        val repo = FakeTestRepository(libros = librosPrueba)
        val viewModel = CatalogoViewModel(ObtenerCatalogoUseCase(repo))

        // Búsqueda sin tilde que debe coincidir con "Cálculo aplicado" (RF-05)
        viewModel.actualizarBusqueda("calculo")

        val estado = viewModel.uiState.value
        assertEquals(1, estado.librosFiltrados.size)
        assertEquals("Cálculo aplicado", estado.librosFiltrados.first().titulo)
    }

    @Test
    fun filtro_por_categoria_funciona_correctamente() {
        val repo = FakeTestRepository(libros = librosPrueba)
        val viewModel = CatalogoViewModel(ObtenerCatalogoUseCase(repo))

        viewModel.seleccionarCategoria("Programación")

        val estado = viewModel.uiState.value
        assertEquals(1, estado.librosFiltrados.size)
        assertEquals("Programación", estado.librosFiltrados.first().categoria)
    }
}
