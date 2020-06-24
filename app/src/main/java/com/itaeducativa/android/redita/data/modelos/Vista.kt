package com.itaeducativa.android.redita.data.modelos

import java.io.Serializable

data class Vista(
    val usuarioUid: String,
    val actividadId: String,
    val timestamp: String,
    val vecesVisto: Int
): Serializable {
    constructor() : this("","","",0)

    var usuario: Usuario? = null

    override fun toString(): String {
        return "Vista(usuarioUid='$usuarioUid', actividadId='$actividadId', timestamp='$timestamp', vecesVisto=$vecesVisto, usuario=$usuario)"
    }


}