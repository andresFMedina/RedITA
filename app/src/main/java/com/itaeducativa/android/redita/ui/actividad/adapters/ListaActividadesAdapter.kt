package com.itaeducativa.android.redita.ui.actividad.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.databinding.CardviewActividadBinding
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.ui.actividad.viewmodels.ActividadViewModel
import com.itaeducativa.android.redita.ui.reaccion.ReaccionListener
import com.itaeducativa.android.redita.util.reaccionHandler
import com.itaeducativa.android.redita.util.startActividadActivity


class ListaActividadesAdapter(
    private val uidUsuarioActual: String
) : RecyclerView.Adapter<ListaActividadesAdapter.ViewHolder>()
     {
    private lateinit var listaActividades: List<Actividad>

    var reaccionListener: ReaccionListener? = null


    class ViewHolder(
        private val binding: CardviewActividadBinding,
        private val adapter: ListaActividadesAdapter

    ) :
        RecyclerView.ViewHolder(binding.root) {
        val viewModelActividad =
            ActividadViewModel()

        val imageButtonMeGusta: ImageButton = binding.layoutReacciones.imageButtonMeGusta
        val imageButtonNoMeGusta: ImageButton = binding.layoutReacciones.imageButtonNoMeGusta
        val imageButtonComentarios: ImageButton = binding.layoutReacciones.imageButtonComentarios
        val textViewFechaYHora: TextView = binding.layoutActividad.textViewFechaYHoraActividad
        private val imageViewActividad: ImageView = binding.imagenActividad


        fun bind(actividad: Actividad) {
            if (actividad.archivos.isNullOrEmpty())
                imageViewActividad.visibility = View.GONE
            else
                imageViewActividad.visibility = View.VISIBLE
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
        val actividad = listaActividades[position]
        holder.bind(actividad)

        if (!actividad.fechaInicio.isNullOrBlank()) {
            val formatoFecha =
                "${actividad.fechaInicio} a las ${actividad.horaInicio}"

            holder.textViewFechaYHora.text = formatoFecha
        } else {
            holder.textViewFechaYHora.visibility = View.GONE
        }

        val reaccion = actividad.reaccion

        reaccionHandler(
            publicacionId = actividad.id,
            publicacion = actividad,
            tipoPublicacion = "actividades",
            usuarioUid = uidUsuarioActual,
            reaccion = reaccion,
            imageButtonMeGusta = holder.imageButtonMeGusta,
            imageButtonNoMeGusta = holder.imageButtonNoMeGusta,
            reaccionListener = reaccionListener
        )

        holder.imageButtonComentarios.setOnClickListener {
            it.context.startActividadActivity(
                actividad
            )
        }
    }


    fun actualizarActividades(actividades: List<Actividad>) {
        this.listaActividades = actividades
        notifyDataSetChanged()
    }

}