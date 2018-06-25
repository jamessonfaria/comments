package com.jamessonfaria.projetocomments.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.jamessonfaria.projetocomments.R
import com.google.gson.reflect.TypeToken
import com.google.gson.Gson
import com.jamessonfaria.projetocomments.model.Comentario
import com.jamessonfaria.projetocomments.util.Util
import kotlinx.android.synthetic.main.activity_detail_comment.*
import kotlinx.android.synthetic.main.adapter_lista_comentarios.view.*


class DetailCommentActivity : AppCompatActivity() {

    private var comentario: Comentario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_comment)

        val gson = Gson()
        val stringComentario = intent.getStringExtra("COMENTARIO")

        if (stringComentario != null) {
            val type = object : TypeToken<Comentario>() {}.type
            comentario = gson.fromJson(stringComentario, type)

            txtID.text = comentario!!.id.toString()
            txtUsuario.text = comentario!!.user
            txtConteudo.text = comentario!!.content
            txtData.text = Util.formatarData(comentario!!.created_at)

            Glide.with(applicationContext)
                    .load(comentario!!.uploaded_image)
                    .placeholder(R.drawable.no_image)
                    .into(imgFoto)

        }
    }
}
