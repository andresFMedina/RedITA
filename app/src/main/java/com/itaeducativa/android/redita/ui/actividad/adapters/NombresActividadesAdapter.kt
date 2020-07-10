package com.itaeducativa.android.redita.ui.actividad.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.itaeducativa.android.redita.R
import java.util.*
import kotlin.collections.ArrayList


class NombresActividadesAdapter(context: Context, private val listaNombresFull: MutableList<String>) :
    ArrayAdapter<String>(context, 0, listaNombresFull) {

    override fun getFilter(): Filter {
        return filtroNombre
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val productName = getItem(position)
        var view = convertView
        if (convertView == null) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.nombre_item, parent, false)
            val viewHolder = ViewHolder(view!!)
            view.tag = viewHolder
        }

        val viewHolder = view!!.tag as ViewHolder
        if (productName != null) {
            viewHolder.bindBeer(productName)
        }

        return view
    }

    private val filtroNombre: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()
            val suggestionList: MutableList<String> = mutableListOf()
            if (constraint.isNullOrEmpty()) {
                suggestionList.addAll(listaNombresFull)
            } else {
                val filterPattern =
                    constraint.toString().toLowerCase(Locale.ROOT).trim()
                for (productName in listaNombresFull) {
                    if (productName.toLowerCase(Locale.ROOT).contains(filterPattern)) {
                        suggestionList.add(productName)
                    }
                }
            }
            results.values = suggestionList
            results.count = suggestionList.size
            return results
        }

        override fun publishResults(
            constraint: CharSequence?,
            results: FilterResults
        ) {
            val listaNombres: MutableList<String>? = results.values as MutableList<String>
            if(listaNombres.isNullOrEmpty()){
                clear()
                listaNombres?.addAll(listaNombres)
            }
            notifyDataSetChanged()
        }

        override fun convertResultToString(resultValue: Any): CharSequence {
            return resultValue as String
        }
    }

    private class ViewHolder internal constructor(itemView: View) {
        private var productNameTextView: TextView? = null

        init {
            productNameTextView = itemView.findViewById(R.id.product_name_text_view)
        }

        fun bindBeer(productName: String?) {
            productNameTextView!!.text = productName
        }
    }
}