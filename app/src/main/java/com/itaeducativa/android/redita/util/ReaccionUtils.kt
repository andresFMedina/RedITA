package com.itaeducativa.android.redita.util

import android.widget.ImageButton
import com.google.firebase.Timestamp
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.data.modelos.Reaccion
import com.itaeducativa.android.redita.ui.reaccion.ReaccionViewModel
import java.util.*

/*fun imageButtonsEvent(
    imageButton: ImageButton,
    iconoVacio: Int,
    iconoLleno: Int,
    imageButtonReaccionDiferente: ImageButton,
    iconoVacioDiferente: Int,
    reaccion: Reaccion?,
    actividad: Actividad,
    tipoReaccion: String,
    reaccionViewModel: ReaccionViewModel,
    usuarioUid: String
): Reaccion? {
    imageButtonReaccionDiferente.setImageResource(iconoVacioDiferente)
    if (reaccion == null) {
        val r: Reaccion =
            objetoReaccion(tipoReaccion, actividad.id, usuarioUid)

        reaccionViewModel.crearReaccion(r)
        imageButton.setImageResource(iconoLleno)

        return r
    } else {
        reaccionViewModel.eliminarReaccion(reaccion)
        imageButton.setImageResource(iconoVacio)
        if (tipoReaccion != reaccion.tipoReaccion) {
            val r: Reaccion =
                objetoReaccion(tipoReaccion, actividad.id, usuarioUid)

            reaccionViewModel.crearReaccion(r)
            imageButton.setImageResource(iconoLleno)
            return r
        }

        return null
    }


}

private fun objetoReaccion(tipoReaccion: String, actividadId: String, usuarioUid: String): Reaccion =
    Reaccion(
        usuarioUid = usuarioUid,
        tipoReaccion = tipoReaccion,
        actividadId = actividadId,
        timestamp = Timestamp(Date()).seconds.toString()
    )
    */
