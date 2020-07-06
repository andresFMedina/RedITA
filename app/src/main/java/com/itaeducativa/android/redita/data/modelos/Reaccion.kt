package com.itaeducativa.android.redita.data.modelos

import java.io.Serializable

data class Reaccion(
    val tipoReaccion: String,
    val usuarioUid: String,
    val publicacionId: String,
    val timestamp: String,
    val tipoPublicacion: String
) : Serializable {
    constructor() : this("", "", "", "","")
}
