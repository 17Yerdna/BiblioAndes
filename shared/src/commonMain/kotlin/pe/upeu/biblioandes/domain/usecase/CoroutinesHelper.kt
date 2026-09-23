package pe.upeu.biblioandes.domain.usecase

import kotlin.coroutines.cancellation.CancellationException

/**
 * Ejecuta un bloque de código y encapsula su resultado en un [Result].
 * Si ocurre una [CancellationException], se relanza para preservar
 * la cancelación cooperativa de corrutinas en los ViewModels y Compose.
 */
inline fun <T> resultadoDe(bloque: () -> T): Result<T> = try {
    Result.success(bloque())
} catch (e: CancellationException) {
    throw e
} catch (e: Throwable) {
    Result.failure(e)
}
