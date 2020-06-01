package com.itaeducativa.android.redita.data.modelos

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import java.io.Serializable
import java.util.*

data class Comentario (
    var comentario: String,
    var fecha: Timestamp

): Serializable {
    constructor():this("", Timestamp(Date()))

    var usuario: Usuario? = null
    var referenciaUsuario: DocumentReference? = null
}