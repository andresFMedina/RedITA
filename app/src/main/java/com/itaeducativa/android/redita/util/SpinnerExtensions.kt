@file:Suppress("UNCHECKED_CAST")

package com.itaeducativa.android.redita.util

import android.R
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner

fun Spinner.setSpinnerEntries(entries: List<String>) {
    val arrayAdapter = ArrayAdapter(context, R.layout.simple_spinner_item, entries)
    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    adapter = arrayAdapter

}

/**
 * set spinner onItemSelectedListener listener
 */
fun Spinner.setSpinnerItemSelectedListener(listener: ItemSelectedListener?) {
    if (listener == null) {
        onItemSelectedListener = null
    } else {
        onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (tag != position) {
                    listener.onItemSelected(parent.getItemAtPosition(position))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}

/**
 * set spinner value
 */
fun Spinner.setSpinnerValue(value: Any?) {
    if (adapter != null) {
        val position = (adapter as ArrayAdapter<Any>).getPosition(value)
        setSelection(position, false)
        tag = position
    }
}

interface ItemSelectedListener {
    fun onItemSelected(item: Any)
}