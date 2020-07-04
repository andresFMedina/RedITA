package com.itaeducativa.android.redita.data.modelos

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import java.io.Serializable
import java.util.*

data class Comentario (
    var comentario: String,
    var fecha: String,
    var usuarioUid: String,
    var publicacionId: String,
    var tipoPublicacion: String
): Serializable {
    constructor():this("", "","","","")

    var usuario: Usuario? = null
    var usuarioReference: DocumentReference? = null

}