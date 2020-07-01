package com.itaeducativa.android.redita.ui.archivo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.data.modelos.Archivo
import com.itaeducativa.android.redita.databinding.CardviewArchivosBinding

class ListaArchivoAdapter : RecyclerView.Adapter<ListaArchivoAdapter.ViewHolder>() {
    private lateinit var listaArchivos: List<Archivo>

    class ViewHolder(private val binding: CardviewArchivosBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val viewModel = ArchivoViewModel()

        fun bind(archivo: Archivo) {
            viewModel.bind(archivo)
            binding.viewModel = viewModel
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: CardviewArchivosBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.cardview_archivos,
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int =
        if (::listaArchivos.isInitialized) listaArchivos.size else 0


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listaArchivos[position])
    }

    fun actualizarArchivos(archivos: List<Archivo>){
        listaArchivos = archivos
        notifyDataSetChanged()
    }
}