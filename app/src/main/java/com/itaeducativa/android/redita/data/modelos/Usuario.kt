package com.itaeducativa.android.redita.data.modelos

import java.io.Serializable

data class Usuario (
    val nombreCompleto: String,
    val rol: String,
    val telefono: String
): Serializable {
    lateinit var email: String
    lateinit var uid: String
    constructor():this("","","")
}