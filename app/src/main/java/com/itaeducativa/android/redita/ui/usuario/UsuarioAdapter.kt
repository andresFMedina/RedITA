package com.itaeducativa.android.redita.ui.usuario

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.data.modelos.Usuario
import com.itaeducativa.android.redita.databinding.CardviewRolUsuarioBinding

class UsuarioAdapter() : RecyclerView.Adapter<UsuarioAdapter.ViewHolder>() {

    private lateinit var listaUsuarios: List<Usuario>

    class ViewHolder(
        private val binding: CardviewRolUsuarioBinding

        ) : RecyclerView.ViewHolder(binding.root) {
        val checkbox = binding.checkboxRolUsuario
        val usuarioViewModel = UsuarioViewModel()

        fun bind(usuario: Usuario) {
            binding.viewModel = usuarioViewModel
            usuarioViewModel.bindUsuario(usuario)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: CardviewRolUsuarioBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.cardview_rol_usuario,
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int =
        if (::listaUsuarios.isInitialized) listaUsuarios.size else 0


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listaUsuarios[position])
    }
}