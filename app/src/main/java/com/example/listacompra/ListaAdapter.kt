package com.example.listacompra

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.listacompra.databinding.ItemHeaderGrupoBinding
import com.example.listacompra.databinding.ItemListaBinding

class ListaAdapter(
    private val lista: MutableList<Any>, // Lista mista de ItemLista e String (Header)
    private val onRemover: (ItemLista) -> Unit,
    private val onEditar: (ItemLista) -> Unit,
    private val onToggleCheck: (ItemLista, Boolean) -> Unit
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

            // Ajustar visual baseado se o item foi "pego"
            if (item.marcado) {
                holder.binding.cardItem.setCardBackgroundColor(Color.parseColor("#E8F5E9")) // Verde claro
                holder.binding.botaoMarcar.text = "Voltar"
                holder.binding.botaoMarcar.setIconResource(android.R.drawable.ic_menu_revert)
                holder.binding.nomeItem.paintFlags = holder.binding.nomeItem.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                holder.binding.nomeItem.setTextColor(Color.parseColor("#4CAF50"))
            } else {
                holder.binding.cardItem.setCardBackgroundColor(Color.parseColor("#F8F9FA")) // Cinza claro/branco
                holder.binding.botaoMarcar.text = "Peguei"
                holder.binding.botaoMarcar.setIconResource(android.R.drawable.ic_input_add)
                holder.binding.nomeItem.paintFlags = holder.binding.nomeItem.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                holder.binding.nomeItem.setTextColor(Color.parseColor("#212121"))
            }

            holder.binding.botaoMarcar.setOnClickListener {
                onToggleCheck(item, !item.marcado)
            }

            holder.binding.botaoRemover.setOnClickListener {
                onRemover(item)
            }

            // Desativado clique no nome para editar conforme solicitado
            holder.binding.containerInfo.setOnClickListener(null)
            holder.binding.containerInfo.isClickable = false
        }
    }

    override fun getItemCount() = lista.size
}