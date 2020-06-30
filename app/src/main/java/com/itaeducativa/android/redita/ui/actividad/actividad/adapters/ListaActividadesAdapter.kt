package com.itaeducativa.android.redita.ui.actividad.actividad.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.data.modelos.Reaccion
import com.itaeducativa.android.redita.databinding.CardviewActividadBinding
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.ui.actividad.actividad.viewmodels.ActividadViewModel
import com.itaeducativa.android.redita.ui.actividad.actividad.viewmodels.ListaActividadesViewModel
import com.itaeducativa.android.redita.util.startActividadActivity
import java.util.*


class ListaActividadesAdapter(
    private val listaActividadesViewModel: ListaActividadesViewModel,
    private val uidUsuarioActual: String
) : RecyclerView.Adapter<ListaActividadesAdapter.ViewHolder>(),
    RequestListener {
    private lateinit var listaActividades: List<Actividad>


    class ViewHolder(
        private val binding: CardviewActividadBinding,
        private val adapter: ListaActividadesAdapter

    ) :
        RecyclerView.ViewHolder(binding.root) {
        val viewModelActividad =
            ActividadViewModel()

        val layout = binding.layoutReacciones
        val imageButtonMeGusta: ImageButton = binding.layoutReacciones.imageButtonMeGusta
        val imageButtonNoMeGusta: ImageButton = binding.layoutReacciones.imageButtonNoMeGusta
        val imageButtonComentarios: ImageButton = binding.layoutReacciones.imageButtonComentarios
        val textViewFechaYHora: TextView = binding.layoutActividad.textViewFechaYHoraActividad
        private val imageViewActividad: ImageView = binding.imagenActividad


        fun bind(actividad: Actividad) {
            //if (actividad.imagenes.isNullOrEmpty())
            imageViewActividad.visibility = View.GONE
            viewModelActividad.requestListener = adapter
            if (actividad.reaccion == null) {
                val query = adapter.listaActividadesViewModel.getReaccionByActividadIdYUsuarioUid(
                    actividad.id,
                    adapter.uidUsuarioActual
                )
                viewModelActividad.getReaccionByActividadIdYUsuarioUid(query)
            }
            viewModelActividad.bind(actividad)

            binding.viewModelActividad = viewModelActividad


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: CardviewActividadBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.cardview_actividad,
            parent,
            false
        )
        return ViewHolder(
            binding,
            this
        )
    }

    override fun getItemCount(): Int {
        return if (::listaActividades.isInitialized) listaActividades.size else 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listaActividades[position])

        val formatoFecha =
            "${listaActividades[position].fechaInicio} a las ${listaActividades[position].horaInicio}"

        holder.textViewFechaYHora.text = formatoFecha

        val reaccion = holder.viewModelActividad.reaccion.value
        if (reaccion != null) {
            when (reaccion.tipoReaccion) {
                "meGusta" -> holder.imageButtonMeGusta.setImageResource(R.drawable.ic_thumb_up_black_filled_24dp)
                "noMeGusta" -> holder.imageButtonNoMeGusta.setImageResource(R.drawable.ic_thumb_down_black_filled_24dp)
            }
        }
        holder.imageButtonMeGusta.setOnClickListener {
            holder.viewModelActividad.reaccion.value = imageButtonsEvent(
                imageButton = holder.imageButtonMeGusta,
                iconoVacio = R.drawable.ic_thumb_up_black_24dp,
                iconoLleno = R.drawable.ic_thumb_up_black_filled_24dp,
                imageButtonReaccionDiferente = holder.imageButtonNoMeGusta,
                iconoVacioDiferente = R.drawable.ic_thumb_down_black_24dp,
                reaccion = holder.viewModelActividad.reaccion.value,
                actividad = listaActividades[position],
                tipoReaccion = "meGusta"
            )
        }
        holder.imageButtonNoMeGusta.setOnClickListener {
            holder.viewModelActividad.reaccion.value = imageButtonsEvent(
                imageButton = holder.imageButtonNoMeGusta,
                iconoVacio = R.drawable.ic_thumb_down_black_24dp,
                iconoLleno = R.drawable.ic_thumb_down_black_filled_24dp,
                imageButtonReaccionDiferente = holder.imageButtonMeGusta,
                iconoVacioDiferente = R.drawable.ic_thumb_up_black_24dp,
                reaccion = holder.viewModelActividad.reaccion.value,
                actividad = listaActividades[position],
                tipoReaccion = "noMeGusta"
            )
        }
        holder.imageButtonComentarios.setOnClickListener {
            it.context.startActividadActivity(
                listaActividades[position],
                holder.viewModelActividad.reaccion.value
            )
        }
    }

    private fun objetoReaccion(tipoReaccion: String, actividadId: String): Reaccion =
        Reaccion(
            usuarioUid = uidUsuarioActual,
            tipoReaccion = tipoReaccion,
            actividadId = actividadId,
            timestamp = Timestamp(Date()).seconds.toString()
        )


    fun actualizarActividades(actividades: List<Actividad>) {
        this.listaActividades = actividades
        notifyDataSetChanged()
    }

    override fun onStartRequest() {

    }

    override fun onSuccessRequest() {
        notifyDataSetChanged()
    }

    override fun onFailureRequest(message: String) {
        Log.e("error", message)
    }

    private fun imageButtonsEvent(
        imageButton: ImageButton,
        iconoVacio: Int,
        iconoLleno: Int,
        imageButtonReaccionDiferente: ImageButton,
        iconoVacioDiferente: Int,
        reaccion: Reaccion?,
        actividad: Actividad,
        tipoReaccion: String
    ): Reaccion? {
        imageButtonReaccionDiferente.setImageResource(iconoVacioDiferente)
        if (reaccion == null) {
            val r: Reaccion =
                objetoReaccion(tipoReaccion, actividad.id)

            listaActividadesViewModel.crearReaccion(r)
            imageButton.setImageResource(iconoLleno)

            return r
        } else {
            listaActividadesViewModel.eliminarReaccion(reaccion)
            imageButton.setImageResource(iconoVacio)
            if (tipoReaccion != reaccion.tipoReaccion) {
                val r: Reaccion =
                    objetoReaccion(tipoReaccion, actividad.id)

                listaActividadesViewModel.crearReaccion(r)
                imageButton.setImageResource(iconoLleno)
                return r
            }

            return null
        }
    }
}