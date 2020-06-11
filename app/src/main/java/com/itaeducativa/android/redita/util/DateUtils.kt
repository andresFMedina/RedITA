package com.itaeducativa.android.redita.util

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.widget.EditText
import java.util.*


private val meses = arrayOf(
    "Enero",
    "Febrero",
    "Marzo",
    "Abril",
    "Mayo",
    "Junio",
    "Julio",
    "Agosto",
    "Septiembre",
    "Ocutubre",
    "Noviembre",
    "Diciembre"
)

fun obtenerFecha(context: Context, editText: EditText) {
    val c = Calendar.getInstance()

    var mes: Int = c.get(Calendar.MONTH)
    var dia: Int = c.get(Calendar.DAY_OF_MONTH)
    var anio: Int = c.get(Calendar.YEAR)

    val recogerFecha = DatePickerDialog(
        context,
        OnDateSetListener { view, year, month, dayOfMonth ->
            val mesActual = month + 1

            val mesFormateado = meses[month]
            val calendar = Calendar.getInstance()
            calendar[year, month] = dayOfMonth

            //Muestro la fecha con el formato deseado
            val fecha = "$dayOfMonth de $mesFormateado del $year"
            editText.setText(fecha)
            anio = year
            mes = month
            dia = dayOfMonth
        }, //Estos valores deben ir en ese orden, de lo contrario no mostrara la fecha actual
        anio, mes, dia
    )
    //Muestro el widget
    recogerFecha.show()
}

fun obtenerHora(context: Context, editText: EditText) {
    val c: Calendar = Calendar.getInstance()

    //Variables para obtener la hora hora

    //Variables para obtener la hora hora
    val hora: Int = c.get(Calendar.HOUR_OF_DAY)
    val minuto: Int = c.get(Calendar.MINUTE)

    val recogerHora = TimePickerDialog(
        context,
        OnTimeSetListener { view, hourOfDay, minute -> //Formateo el hora obtenido: antepone el 0 si son menores de 10
            val horaFormateada =
                if (hourOfDay < 10) "0$hourOfDay" else hourOfDay.toString()
            //Formateo el minuto obtenido: antepone el 0 si son menores de 10
            val minutoFormateado =
                if (minute < 10) "0$minute" else minute.toString()
            //Obtengo el valor a.m. o p.m., dependiendo de la selección del usuario

            //Muestro la hora con el formato deseado
            val hora =
                "$horaFormateada:$minutoFormateado"
            editText.setText(hora)
        }, //Estos valores deben ir en ese orden
        //Al colocar en false se muestra en formato 12 horas y true en formato 24 horas
        //Pero el sistema devuelve la hora en formato 24 horas
        hora, minuto, true
    )
    recogerHora.show()
}