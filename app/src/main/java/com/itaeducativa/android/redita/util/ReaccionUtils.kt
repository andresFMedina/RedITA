package com.itaeducativa.android.redita.util

import android.widget.ImageButton
import com.google.firebase.Timestamp
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.data.modelos.Publicacion
import com.itaeducativa.android.redita.data.modelos.Reaccion
import com.itaeducativa.android.redita.ui.reaccion.ReaccionListener


fun reaccionHandler(
    publicacionId: String,
    publicacion: Publicacion,
    tipoPublicacion: String,
    usuarioUid: String,
    reaccion: Reaccion?,
    imageButtonMeGusta: ImageButton,
    imageButtonNoMeGusta: ImageButton,
    reaccionListener: ReaccionListener?
) {
    if (reaccion != null) {
        when (reaccion.tipoReaccion) {
            "meGusta" -> imageButtonMeGusta.setImageResource(R.drawable.ic_thumb_up_black_filled_24dp)
            "noMeGusta" -> imageButtonNoMeGusta.setImageResource(R.drawable.ic_thumb_down_black_filled_24dp)
        }
    }
        imageButtonMeGusta.setOnClickListener {
            val nuevaReaccion = Reaccion(
                tipoReaccion = "meGusta",
                usuarioUid = usuarioUid,
                publicacionId = publicacionId,
                timestamp = Timestamp.now().seconds.toString(),
                tipoPublicacion = tipoPublicacion
            )
            reaccionListener?.onReaccion(nuevaReaccion, reaccion, publicacion)
        }
        imageButtonNoMeGusta.setOnClickListener {
            val nuevaReaccion = Reaccion(
                tipoReaccion = "noMeGusta",
                usuarioUid = usuarioUid,
                publicacionId = publicacionId,
                timestamp = Timestamp.now().seconds.toString(),
                tipoPublicacion = tipoPublicacion
            )
            reaccionListener?.onReaccion(nuevaReaccion, reaccion, publicacion)
        }


}
    
    
