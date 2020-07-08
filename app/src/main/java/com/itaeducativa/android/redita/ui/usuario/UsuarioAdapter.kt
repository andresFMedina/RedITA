package com.itaeducativa.android.redita.ui.usuario

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.data.modelos.Usuario
import com.itaeducativa.android.redita.databinding.CardviewRolUsuarioBinding
import com.itaeducativa.android.redita.util.startListaHistorialActividad

class UsuarioAdapter(
    private val listaUsuarioViewModel: ListaUsuarioViewModel
) : RecyclerView.Adapter<UsuarioAdapter.ViewHolder>() {

    private lateinit var listaUsuarios: List<Usuario>

    class ViewHolder(
        private val binding: CardviewRolUsuarioBinding

        ) : RecyclerView.ViewHolder(binding.root) {
        val checkbox = binding.checkboxRolUsuario
        val button = binding.buttonVerHistorial
        val textViewAcudiente = binding.textViewAcudienteDe
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
        val usuario = listaUsuarios[position]
        holder.bind(listaUsuarios[position])
        holder.checkbox.setOnCheckedChangeListener(null)
        holder.checkbox.isChecked = usuario.rol == "Docente"
        holder.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            Log.d("Checado " , isChecked.toString())
            Log.d("Boton", holder.checkbox.isChecked.toString())
            usuario.rol = if (isChecked) "Docente" else ""
            listaUsuarioViewModel.modificarRol(usuario.rol, usuario.uid)
        }
        holder.button.setOnClickListener {
            it.context.startListaHistorialActividad(usuario)
        }

        if(usuario.nombreEstudiante.isNotBlank()) {
            val text = "Acudiente de ${usuario.nombreEstudiante} de ${usuario.gradoEstudiante}"
            holder.textViewAcudiente.text = text
        }

    }

    fun actualizarUsuarios(usuarios: List<Usuario>) {
        listaUsuarios = usuarios
        notifyDataSetChanged()
    }
}