package com.itaeducativa.android.redita.data.modelos


data class Archivo(
    val id: String,
    val timestamp: String,
    val url: String,
    val tipo: String,
    val acitividadId: String,
    val meGusta: Int,
    val noMeGusta: Int,
    val comentarios: Int
) {
    constructor() : this("", "", "", "", "", 0, 0, 0)
}