package com.itaeducativa.android.redita.ui.actividad.actividad.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.databinding.CardviewMisActividadesBinding
import com.itaeducativa.android.redita.ui.actividad.actividad.viewmodels.ActividadViewModel
import com.itaeducativa.android.redita.ui.actividad.actividad.viewmodels.ListaActividadesViewModel
import com.itaeducativa.android.redita.util.startFormularioActividadActivity

class MisActividadesAdapter(private val listaActividadesViewModel: ListaActividadesViewModel) :
    RecyclerView.Adapter<MisActividadesAdapter.ViewHolder>() {

    private lateinit var listaActividades: List<Actividad>

    class ViewHolder(
        private val binding: CardviewMisActividadesBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private val viewModel = ActividadViewModel()
        val imagenActividad: ImageView = binding.imageViewMiActividad
        val imageButtonEditar = binding.imageButtonEditar
        val imageButtonEliminar = binding.imageButtonEliminar

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
        //val imagenes = listaActividades[position].imagenes
        //if (imagenes.isNullOrEmpty()) {
        holder.imagenActividad.visibility = View.GONE
        //}
        holder.imageButtonEditar.setOnClickListener {
            it.context.startFormularioActividadActivity(listaActividades[position])
        }
        holder.imageButtonEliminar.setOnClickListener {
            MaterialAlertDialogBuilder(it.context, R.style.ThemeOverlay_App_MaterialAlertDialog)
                .setTitle(it.resources.getString(R.string.titulo_dialogo))
                .setMessage(it.resources.getString(R.string.descripcion_dialogo))
                .setNegativeButton(it.resources.getString(R.string.cancelar_dialogo)) { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton(it.resources.getString(R.string.aceptar_dialogo)) { dialog, which ->
                    listaActividadesViewModel.desactivarActividad(listaActividades[position])
                }
                .show()
        }
    }

    fun actualizarActividades(actividades: List<Actividad>) {
        listaActividades = actividades
        notifyDataSetChanged()
    }


}