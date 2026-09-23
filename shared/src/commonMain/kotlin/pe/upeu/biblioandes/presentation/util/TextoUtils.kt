package pe.upeu.biblioandes.presentation.util

/**
 * Normaliza un texto removiendo acentos diacríticos (tildes) y convirtiendo a minúsculas
 * para búsquedas case-insensitive y tolerantes a tildes en Kotlin Multiplatform (RF-05).
 */
fun String.normalizarParaBusqueda(): String {
    val mapaTildes = mapOf(
        'á' to 'a', 'é' to 'e', 'í' to 'i', 'ó' to 'o', 'ú' to 'u', 'ü' to 'u',
        'Á' to 'a', 'É' to 'e', 'Í' to 'i', 'Ó' to 'o', 'Ú' to 'u', 'Ü' to 'u',
        'ñ' to 'n', 'Ñ' to 'n'
    )

    val builder = StringBuilder(this.length)
    for (caracter in this.trim().lowercase()) {
        builder.append(mapaTildes[caracter] ?: caracter)
    }
    return builder.toString()
}

/**
 * Determina si la cadena contiene el término de búsqueda ignorando mayúsculas y tildes.
 */
fun String.contieneTermino(termino: String): Boolean {
    if (termino.isBlank()) return true
    return this.normalizarParaBusqueda().contains(termino.normalizarParaBusqueda())
}
