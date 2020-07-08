package com.itaeducativa.android.redita.data.modelos

import java.io.Serializable


data class Archivo(
    val id: String,
    val timestamp: String,
    val url: String,
    val tipo: String,
    val actividadId: String
): Publicacion(), Serializable {
    constructor() : this("", "", "", "", "")
}