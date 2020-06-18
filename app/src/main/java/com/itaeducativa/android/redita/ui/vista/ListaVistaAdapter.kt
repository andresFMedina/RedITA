package com.itaeducativa.android.redita.ui.vista

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.data.modelos.Vista
import com.itaeducativa.android.redita.databinding.CardviewVistasActividadBinding

class ListaVistaAdapter : RecyclerView.Adapter<ListaVistaAdapter.ViewHolder>() {

    private lateinit var listaVistas: List<Vista>

    class ViewHolder(
        private val binding: CardviewVistasActividadBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private val vistaViewModel = VistaViewModel()
        val textViewMensajeVista = binding.mensajeVista

        fun bind(vista: Vista) {
            vistaViewModel.bind(vista)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: CardviewVistasActividadBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.cardview_vistas_actividad,
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int =
        if (::listaVistas.isInitialized) listaVistas.size else 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vista = listaVistas[position]
        if (vista.usuario != null) {
            holder.textViewMensajeVista.text =
                "${vista.usuario!!.nombreCompleto} ha visto esta actividad ${vista.vecesVisto} veces"
        }
    }

    fun actualizarVistas(vistas: List<Vista>){
        listaVistas = vistas
        notifyDataSetChanged()
    }
}