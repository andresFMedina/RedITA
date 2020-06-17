package com.itaeducativa.android.redita.data.modelos

import java.io.Serializable

data class Vista(
    val usuarioUid: String,
    val actividadId: String,
    val timestamp: String,
    val vecesVisto: Int
): Serializable {
}