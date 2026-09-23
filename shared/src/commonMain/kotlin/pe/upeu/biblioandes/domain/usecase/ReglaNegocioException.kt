package pe.upeu.biblioandes.domain.usecase

/**
 * Jerarquía de excepciones de dominio para las Reglas de Negocio (RN-01 a RN-04).
 */
sealed class ReglaNegocioException(override val message: String) : IllegalStateException(message) {

    /** RN-01: Límite estricto de máximo 3 préstamos en estado Activo simultáneos */
    class LimitePrestamosAlcanzadoException : ReglaNegocioException(
        "No puedes tener más de 3 préstamos en estado Activo de forma simultánea (RN-01)."
    )

    /** RN-02: Prohibido solicitar un libro cuyos ejemplaresDisponibles sean igual a cero */
    class SinEjemplaresDisponiblesException : ReglaNegocioException(
        "No se puede solicitar este libro porque no tiene ejemplares disponibles (RN-02)."
    )

    /** RN-04: Bloqueo de solicitudes si el estudiante posee al menos un préstamo en estado Vencido */
    class EstudianteConPrestamosVencidosException : ReglaNegocioException(
        "Tienes al menos un préstamo en estado Vencido. No puedes solicitar nuevos libros hasta regularizarlo (RN-04)."
    )

    /** Validación de existencia */
    class LibroNoEncontradoException : ReglaNegocioException(
        "El libro seleccionado no se encuentra en el catálogo."
    )
}
