package com.itaeducativa.android.redita.data.modelos

import java.io.Serializable

data class Reaccion(
    val tipoReaccion: String,
    val usuarioUid: String,
    val actividadId: String,
    val timestamp: String
) : Serializable {
    constructor() : this("", "", "", "")
}
