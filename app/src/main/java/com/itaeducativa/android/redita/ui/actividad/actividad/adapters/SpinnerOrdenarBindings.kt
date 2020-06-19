package com.itaeducativa.android.redita.ui.actividad.actividad.adapters

import android.widget.Spinner
import androidx.databinding.BindingAdapter
import com.itaeducativa.android.redita.util.ItemSelectedListener
import com.itaeducativa.android.redita.util.setSpinnerEntries
import com.itaeducativa.android.redita.util.setSpinnerItemSelectedListener
import com.itaeducativa.android.redita.util.setSpinnerValue

class SpinnerOrdenarBindings {

    companion object DataBindingAdapter {
        @BindingAdapter("entries")
        @JvmStatic
        fun Spinner.setEntries(entries: List<String>) {
            setSpinnerEntries(entries)
        }

        @BindingAdapter("onItemSelected")
        @JvmStatic
        fun Spinner.setItemSelectedListener(itemSelectedListener: ItemSelectedListener?) {
            setSpinnerItemSelectedListener(itemSelectedListener)
        }

        @BindingAdapter("newValue")
        @JvmStatic
        fun Spinner.setNewValue(newValue: Any?) {
            setSpinnerValue(newValue)
        }
    }
}