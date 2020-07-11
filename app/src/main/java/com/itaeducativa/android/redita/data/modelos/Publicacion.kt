package com.itaeducativa.android.redita.data.modelos

import java.io.Serializable

open class Publicacion(
    var reaccion: Reaccion? = null,
    var comentarios: Int = 0,
    var meGusta: Int = 0,
    var noMeGusta: Int = 0
): Serializable {
}