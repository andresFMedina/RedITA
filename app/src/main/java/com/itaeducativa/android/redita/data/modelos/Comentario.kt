package com.itaeducativa.android.redita.data.modelos

import com.google.firebase.Timestamp
import java.io.Serializable
import java.util.*

data class Comentario (
    val comentario: String,
    val fecha: Timestamp,
    val usuario: Usuario
): Serializable {
    constructor():this("", Timestamp(Date()), Usuario())
}