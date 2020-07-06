package com.itaeducativa.android.redita.ui.reaccion

import com.itaeducativa.android.redita.data.modelos.Publicacion
import com.itaeducativa.android.redita.data.modelos.Reaccion

interface ReaccionListener {
    fun onReaccion(reaccionNueva: Reaccion, reaccionVieja: Reaccion?, publicacion: Publicacion)
}