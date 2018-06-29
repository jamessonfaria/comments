package com.jamessonfaria.projetocomments.adapter

import android.content.ClipData
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.Filter
import android.widget.Filterable
import com.bumptech.glide.Glide
import com.jamessonfaria.projetocomments.R
import com.jamessonfaria.projetocomments.model.Comentario
import com.jamessonfaria.projetocomments.util.Util
import kotlinx.android.synthetic.main.adapter_lista_comentarios.view.*
import java.util.*

class AdapterComentarios(context: Context, listaComentarios: List<Comentario>, val clickListener: (Comentario) -> Unit) : Filterable,
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var listaComentarios: List<Comentario>
    var listaFiltrada: List<Comentario>
    var context: Context

    init {
        this.listaComentarios = listaComentarios
        this.listaFiltrada = listaComentarios as List<Comentario>
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var itemLista = LayoutInflater.from(context).inflate(R.layout.adapter_lista_comentarios, parent, false)
        return Item(itemLista)
    }

    override fun getItemCount(): Int {
        return listaFiltrada.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as Item).bindData(listaFiltrada[position], clickListener)
    }

    class Item(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(comentario: Comentario, clickListener: (Comentario) -> Unit) {
            itemView.txtNome.text = comentario.user
            itemView.txtData.text = Util.formatarData(comentario.created_at)

            itemView.setOnClickListener { clickListener(comentario)}

            Glide.with(itemView.context)
                    .load(comentario.uploaded_image)
                    .placeholder(R.drawable.no_image)
                    .into(itemView.imgFoto)
        }
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(contraint: CharSequence?): FilterResults {
                var charString: String = contraint.toString()
                if(charString.isEmpty()){
                    listaFiltrada = listaComentarios
                }else{
                    var filtroLista: MutableList<Comentario> = mutableListOf()
                    for(c: Comentario in listaComentarios){
                        if(c.content.toLowerCase().contains(charString.toLowerCase())){
                            filtroLista.add(c)
                        }
                    }
                    listaFiltrada = filtroLista
                }

                var filterResult: FilterResults = FilterResults()
                filterResult.values = listaFiltrada
                return filterResult

            }

            override fun publishResults(contraint: CharSequence?, results: FilterResults?) {
                listaFiltrada = results!!.values as List<Comentario>
                notifyDataSetChanged()
            }

        }
    }


}