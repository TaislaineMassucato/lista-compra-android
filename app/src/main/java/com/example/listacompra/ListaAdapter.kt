package com.example.listacompra

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.listacompra.databinding.ItemListaBinding

class ListaAdapter(
    private val lista: MutableList<ItemLista>,
    private val onRemover: (ItemLista) -> Unit,
    private val onEditar: (ItemLista) -> Unit
) : RecyclerView.Adapter<ListaAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemListaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemListaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]

        holder.binding.nomeItem.text = item.nome
        holder.binding.dataItem.text = item.data

        holder.binding.checkbox.setOnCheckedChangeListener(null)
        holder.binding.checkbox.isChecked = item.marcado

        holder.binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
            item.marcado = isChecked
        }

        holder.binding.botaoRemover.setOnClickListener {
            onRemover(item)
        }

        holder.binding.botaoEditar.setOnClickListener {
            onEditar(item)
        }
    }

    override fun getItemCount() = lista.size
}