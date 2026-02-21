package com.example.listacompra

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listacompra.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ListaAdapter
    private val lista = mutableListOf<ItemLista>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarRecycler()
        configurarBotao()
    }

    private fun configurarRecycler() {
        adapter = ListaAdapter(
            lista,
            onRemover = { item -> removerItem(item) },
            onEditar = { item -> editarItem(item) }
        )

        binding.recyclerLista.layoutManager = LinearLayoutManager(this)
        binding.recyclerLista.adapter = adapter
    }

    private fun configurarBotao() {
        binding.botaoAdicionar.setOnClickListener {
            val nome = binding.editItem.text.toString()

            if (nome.isNotBlank()) {
                val data = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    .format(Date())

                lista.add(ItemLista(nome, data))
                adapter.notifyItemInserted(lista.size - 1)
                binding.editItem.text.clear()
            }
        }
    }

    private fun removerItem(item: ItemLista) {
        val posicao = lista.indexOf(item)
        if (posicao != -1) {
            lista.removeAt(posicao)
            adapter.notifyItemRemoved(posicao)
        }
    }

    private fun editarItem(item: ItemLista) {
        val input = EditText(this)
        input.setText(item.nome)

        AlertDialog.Builder(this)
            .setTitle("Editar item")
            .setView(input)
            .setPositiveButton("Salvar") { _, _ ->
                val novoTexto = input.text.toString()
                if (novoTexto.isNotBlank()) {
                    item.nome = novoTexto
                    adapter.notifyDataSetChanged()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}