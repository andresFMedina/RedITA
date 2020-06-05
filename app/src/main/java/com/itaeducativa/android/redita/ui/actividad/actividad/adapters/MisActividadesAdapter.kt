package com.itaeducativa.android.redita.ui.actividad.actividad.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.databinding.CardviewMisActividadesBinding
import com.itaeducativa.android.redita.ui.actividad.actividad.viewmodels.ActividadViewModel
import com.itaeducativa.android.redita.ui.actividad.actividad.viewmodels.ListaActividadesViewModel

class MisActividadesAdapter() : RecyclerView.Adapter<MisActividadesAdapter.ViewHolder>() {

    private lateinit var listaActividades: List<Actividad>

    class ViewHolder(
        private val binding: CardviewMisActividadesBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private val viewModel = ActividadViewModel()


        fun bind(actividad: Actividad) {
            binding.viewModel = viewModel
            viewModel.bind(actividad)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: CardviewMisActividadesBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.cardview_mis_actividades, parent, false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int =
        if (::listaActividades.isInitialized) listaActividades.size else 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listaActividades[position])
    }

    fun actualizarActividades(actividades: List<Actividad>) {
        listaActividades = actividades
        notifyDataSetChanged()
    }


}