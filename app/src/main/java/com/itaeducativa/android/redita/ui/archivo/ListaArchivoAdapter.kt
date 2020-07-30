package com.itaeducativa.android.redita.ui.archivo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.data.modelos.Archivo
import com.itaeducativa.android.redita.databinding.CardviewArchivosBinding
import com.itaeducativa.android.redita.ui.reaccion.ReaccionListener
import com.itaeducativa.android.redita.util.reaccionHandler
import com.itaeducativa.android.redita.util.startArchivoDetalladoActivity

class ListaArchivoAdapter(
    private val listaArchivoViewModel: ListaArchivoViewModel,
    private val esAutor: Boolean
) : RecyclerView.Adapter<ListaArchivoAdapter.ViewHolder>() {
    private lateinit var listaArchivos: List<Archivo>

    var reaccionListener: ReaccionListener? = null

    class ViewHolder(private val binding: CardviewArchivosBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val viewModel = ArchivoViewModel()
        val imageButtonEliminar: ImageButton = binding.imageButtonEliminarArchivo

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
        val archivo = listaArchivos[position]
        holder.bind(archivo)
        if(esAutor){
            holder.imageButtonEliminar.visibility = View.VISIBLE
            holder.imageButtonEliminar.setOnClickListener {
                AlertDialog.Builder(it.context)
                    .setTitle(it.resources.getString(R.string.titulo_dialogo_archivo))
                    .setMessage(it.resources.getString(R.string.descripcion_dialogo_archivo))
                    .setNegativeButton(it.resources.getString(R.string.cancelar_dialogo)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(it.resources.getString(R.string.aceptar_dialogo)) { _, _ ->
                        listaArchivos.toMutableList().removeAt(position)
                        listaArchivoViewModel.eliminarArchivo(archivo)
                    }
                    .show()

            }
        }
    }

    fun actualizarArchivos(archivos: List<Archivo>){
        listaArchivos = archivos
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}