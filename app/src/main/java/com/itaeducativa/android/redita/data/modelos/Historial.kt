package com.itaeducativa.android.redita.data.modelos

import java.io.Serializable

data class Historial(
    var usuarioUid: String,
    var actividadId: String,
    var accion: String,
    var timestampAccion: String
) : Serializable {
    var usuario: Usuario? = null
    var actividad: Actividad? = null

    constructor() : this("", "", "", "")

}