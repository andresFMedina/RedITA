package com.itaeducativa.android.redita.ui.historial

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.data.modelos.Historial
import com.itaeducativa.android.redita.databinding.CardviewHistorialUsuarioBinding

class ListaHistorialAdapter : RecyclerView.Adapter<ListaHistorialAdapter.ViewHolder>() {
    lateinit var listaHistorial: List<Historial>

    class ViewHolder(
        private val binding: CardviewHistorialUsuarioBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private val historialViewModel = HistorialViewModel()
        val textViewHistorial = binding.descripcionHistorial

        fun bind(historial: Historial) {
            historialViewModel.bind(historial)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: CardviewHistorialUsuarioBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.cardview_historial_usuario,
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int =
        if (::listaHistorial.isInitialized) listaHistorial.size else 0


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val historial = listaHistorial[position]
        Log.d("Historial", historial.toString())
        holder.bind(historial)
        if (historial.actividad != null && historial.usuario != null) {

            holder.textViewHistorial.text =
                "${historial.usuario!!.nombreCompleto} ${historial.accion} ${historial.actividad!!.nombre}"
        }
    }

    fun actualizarHistorial(historiales: List<Historial>) {
        listaHistorial = historiales
        notifyDataSetChanged()
    }
}