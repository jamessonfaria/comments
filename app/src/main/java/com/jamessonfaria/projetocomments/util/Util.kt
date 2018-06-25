package com.jamessonfaria.projetocomments.util

import android.content.Context
import android.net.ConnectivityManager
import android.util.JsonReader
import com.jamessonfaria.projetocomments.model.Comentario
import org.json.JSONArray
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.*

object Util {

    fun isNetworkAvaliabe(context: Context): Boolean{
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = cm.activeNetworkInfo

        return info != null && info.isConnected ; true ; false
    }

    fun formatarData(data: String): String {

        var formato = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        var dataFormatada: Date = formato.parse(data)


        formato = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        var dataFinal = formato.format(dataFormatada)

        return dataFinal
    }


}

