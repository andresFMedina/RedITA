package com.itaeducativa.android.redita.ui.actividad

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.databinding.CardviewActividadBinding
import com.itaeducativa.android.redita.network.RequestListener


class ListaActividadesAdapter : RecyclerView.Adapter<ListaActividadesAdapter.ViewHolder>(),
    RequestListener {
    private lateinit var listaActividades: List<Actividad>


    class ViewHolder(
        private val binding: CardviewActividadBinding,
        private val adapter: ListaActividadesAdapter
    ) :
        RecyclerView.ViewHolder(binding.root) {
        private val viewModelActividad = ActividadViewModel()


        fun bind(actividad: Actividad) {
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
        return ViewHolder(binding, this)
    }

    override fun getItemCount(): Int {
        return if (::listaActividades.isInitialized) listaActividades.size else 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listaActividades[position])
    }

    fun actualizarActividades(actividades: List<Actividad>) {
        this.listaActividades = actividades
        notifyDataSetChanged()
    }

    override fun onStartRequest() {

    }

    override fun onSuccess() {
        notifyDataSetChanged()
    }

    override fun onFailure(message: String) {
        Log.e("error", message)
    }
}