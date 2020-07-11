package com.itaeducativa.android.redita.data.modelos

import java.io.Serializable

private const val URL_IMAGEN_VIDEO = "gs://redita.appspot.com/videoplayerplaybutton.png"
data class Archivo(
    val id: String,
    val timestamp: String,
    val url: String,
    val tipo: String,
    val actividadId: String,
    var urlImagen: String = URL_IMAGEN_VIDEO

): Publicacion(), Serializable {
    constructor() : this("", "", "", "", "")
}