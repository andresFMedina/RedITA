package com.itaeducativa.android.redita.ui.actividad.actividad.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.databinding.CardviewActividadBinding
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.ui.actividad.actividad.viewmodels.ActividadViewModel
import com.itaeducativa.android.redita.ui.actividad.actividad.viewmodels.ListaActividadesViewModel
import com.itaeducativa.android.redita.ui.actividad.reaccion.ReaccionListener
import com.itaeducativa.android.redita.util.startActividadActivity


class ListaActividadesAdapter(
    listaActividadesViewModel: ListaActividadesViewModel
) : RecyclerView.Adapter<ListaActividadesAdapter.ViewHolder>(),
    RequestListener {
    private lateinit var listaActividades: List<Actividad>

    private var reaccionListener: ReaccionListener = listaActividadesViewModel


    class ViewHolder(
        private val binding: CardviewActividadBinding,
        private val adapter: ListaActividadesAdapter

    ) :
        RecyclerView.ViewHolder(binding.root) {
        private val viewModelActividad =
            ActividadViewModel()
        val layout = binding.layoutReacciones
        val imageButtonMeGusta: ImageButton = binding.layoutReacciones.imageButtonMeGusta
        val imageButtonNoMeGusta: ImageButton = binding.layoutReacciones.imageButtonNoMeGusta
        val imageButtonComentarios: ImageButton = binding.layoutReacciones.imageButtonComentarios
        val imageViewActividad: ImageView = binding.imagenActividad


        fun bind(actividad: Actividad) {
            if(actividad.imagenes == null || actividad.imagenes!!.size == 0)
                imageViewActividad.visibility = View.GONE
            viewModelActividad.requestListener = adapter
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
        holder.imageButtonMeGusta.setOnClickListener {
            reaccionListener.onMeGusta(listaActividades[position])
            holder.imageButtonMeGusta.setImageResource(R.drawable.ic_thumb_up_black_filled_24dp)
        }
        holder.imageButtonNoMeGusta.setOnClickListener {
            reaccionListener.onNoMeGusta(listaActividades[position])
            holder.imageButtonNoMeGusta.setImageResource(R.drawable.ic_thumb_down_black_filled_24dp)
        }
        holder.imageButtonComentarios.setOnClickListener {
            it.context.startActividadActivity(listaActividades[position])
        }
    }

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
}