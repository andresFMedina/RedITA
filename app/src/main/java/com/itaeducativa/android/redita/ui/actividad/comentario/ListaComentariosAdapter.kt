package com.itaeducativa.android.redita.ui.actividad.comentario

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.data.modelos.Comentario
import com.itaeducativa.android.redita.databinding.CardviewComentarioBinding
import com.itaeducativa.android.redita.network.RequestListener

class ListaComentariosAdapter : RecyclerView.Adapter<ListaComentariosAdapter.ViewHolder>(), RequestListener {
    private lateinit var listaComentarios: List<Comentario>

    class ViewHolder(private val binding: CardviewComentarioBinding, private val adapter: ListaComentariosAdapter) :
        RecyclerView.ViewHolder(binding.root) {

        private val viewModel = ComentarioViewModel()

        fun bind(comentario: Comentario) {
            viewModel.requestListener = adapter
            viewModel.bind(comentario)
            binding.viewModel = viewModel
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: CardviewComentarioBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.cardview_comentario,
            parent,
            false
        )
        return ViewHolder(binding, this)
    }

    override fun getItemCount(): Int {
        return if(::listaComentarios.isInitialized) listaComentarios.size else 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listaComentarios[position])
    }

    fun actualizarComentarios(comentarios: List<Comentario>) {
        this.listaComentarios = comentarios
        notifyDataSetChanged()
    }

    override fun onStartRequest() {

    }

    override fun onSuccess() {
        notifyDataSetChanged()
    }

    override fun onFailure(message: String) {
        Log.e("Error", message)
    }
}