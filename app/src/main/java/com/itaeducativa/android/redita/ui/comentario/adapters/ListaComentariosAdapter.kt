package com.itaeducativa.android.redita.ui.comentario.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.data.modelos.Comentario
import com.itaeducativa.android.redita.data.modelos.Publicacion
import com.itaeducativa.android.redita.databinding.CardviewComentarioBinding
import com.itaeducativa.android.redita.ui.comentario.viewmodels.ComentarioViewModel
import com.itaeducativa.android.redita.ui.comentario.viewmodels.ListaComentariosViewModel

class ListaComentariosAdapter(
    private val uidUsuario: String,
    private val listaComentariosViewModel: ListaComentariosViewModel
) : RecyclerView.Adapter<ListaComentariosAdapter.ViewHolder>() {
    private lateinit var listaComentarios: List<Comentario>

    lateinit var publicacion: Publicacion

    class ViewHolder(
        private val binding: CardviewComentarioBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        val buttonEliminarComentario = binding.imageButtonEliminarComentario

        private val viewModel =
            ComentarioViewModel()

        fun bind(comentario: Comentario) {
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
        return ViewHolder(
            binding
        )
    }

    override fun getItemCount(): Int {
        return if (::listaComentarios.isInitialized) listaComentarios.size else 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comentario = listaComentarios[position]
        if (uidUsuario == comentario.usuarioUid) {
            holder.buttonEliminarComentario.visibility = View.VISIBLE

            holder.buttonEliminarComentario.setOnClickListener {
                AlertDialog.Builder(it.context)
                    .setTitle(it.resources.getString(R.string.titulo_dialogo_comentario))
                    .setMessage(it.resources.getString(R.string.descripcion_dialogo_comentario))
                    .setNegativeButton(it.resources.getString(R.string.cancelar_dialogo)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(it.resources.getString(R.string.aceptar_dialogo)) { _, _ ->
                        listaComentariosViewModel
                            .eliminarComentario(comentario, publicacion)
                    }
                    .show()
            }
        }
        holder.bind(comentario)
    }

    fun actualizarComentarios(comentarios: List<Comentario>) {
        this.listaComentarios = comentarios
        notifyDataSetChanged()
    }


}