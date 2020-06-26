package com.itaeducativa.android.redita.data.modelos

import java.io.Serializable

data class Usuario (
    val nombreCompleto: String,
    var rol: String,
    val telefono: String,
    val imagenPerfilUrl: String,
    var email: String,
    val uid: String,
    var nombreEstudiante: String,
    var meGusta: Int = 0,
    var noMeGusta: Int = 0,
    var comentarios: Int = 0,
    var gradoEstudiante: String = "4B"
): Serializable {


    constructor():this("","","","","","","")
}