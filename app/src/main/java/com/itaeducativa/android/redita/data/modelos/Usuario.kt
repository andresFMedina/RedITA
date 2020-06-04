package com.itaeducativa.android.redita.data.modelos

import java.io.Serializable

data class Usuario (
    val nombreCompleto: String,
    val rol: String,
    val telefono: String,
    val imagenPerfilUrl: String,
    var email: String,
    val uid: String
): Serializable {


    constructor():this("","","","","","")
}