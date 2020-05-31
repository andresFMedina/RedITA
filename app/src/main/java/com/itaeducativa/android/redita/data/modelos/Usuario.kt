package com.itaeducativa.android.redita.data.modelos

import java.io.Serializable

data class Usuario (
    val uid: String,
    val nombreCompleto: String,
    val rol: String,
    val telefono: String
): Serializable {
    constructor():this("","","","")
}