package com.example.listacompra

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.listacompra.databinding.ItemHeaderGrupoBinding
import com.example.listacompra.databinding.ItemListaBinding

class ListaAdapter(
    private val lista: MutableList<Any>, // Lista mista de ItemLista e String (Header)
    private val onRemover: (ItemLista) -> Unit,
    private val onEditar: (ItemLista) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (lista[position] is String) TYPE_HEADER else TYPE_ITEM
    }

    inner class HeaderViewHolder(val binding: ItemHeaderGrupoBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class ItemViewHolder(val binding: ItemListaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_HEADER) {
            HeaderViewHolder(ItemHeaderGrupoBinding.inflate(inflater, parent, false))
        } else {
            ItemViewHolder(ItemListaBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = lista[position]

        if (holder is HeaderViewHolder && item is String) {
            holder.binding.textHeaderGrupo.text = item.uppercase()
        } else if (holder is ItemViewHolder && item is ItemLista) {
            holder.binding.nomeItem.text = item.nome
            holder.binding.categoriaItem.text = item.categoria
            holder.binding.textQuantidade.text = "${item.quantidade}x"

            holder.binding.checkbox.setOnCheckedChangeListener(null)
            holder.binding.checkbox.isChecked = item.marcado

            holder.binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                item.marcado = isChecked
            }

            holder.binding.botaoRemover.setOnClickListener {
                onRemover(item)
            }

            holder.binding.root.setOnClickListener {
                onEditar(item)
            }
        }
    }

    override fun getItemCount() = lista.size
}