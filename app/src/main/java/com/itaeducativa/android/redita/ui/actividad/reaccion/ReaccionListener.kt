package com.itaeducativa.android.redita.ui.actividad.reaccion

import com.itaeducativa.android.redita.data.modelos.Actividad

interface ReaccionListener {
    fun onMeGusta(actividad: Actividad)
    fun onNoMeGusta(actividad: Actividad)
}