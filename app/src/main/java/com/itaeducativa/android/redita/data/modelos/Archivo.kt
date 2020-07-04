package com.itaeducativa.android.redita.data.modelos

import java.io.Serializable


data class Archivo(
    val id: String,
    val timestamp: String,
    val url: String,
    val tipo: String,
    val actividadId: String,
    val meGusta: Int,
    val noMeGusta: Int,
    val comentarios: Int
): Serializable {
    constructor() : this("", "", "", "", "", 0, 0, 0)
}