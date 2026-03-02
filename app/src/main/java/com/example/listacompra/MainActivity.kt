package com.example.listacompra

import android.os.Bundle
import android.view.LayoutInflater
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
    private val listaItensOriginal = mutableListOf<ItemLista>()
    private val listaExibicao = mutableListOf<Any>() // Mistura Header (String) e Itens (ItemLista)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarRecycler()
        configurarBotaoAdicionar()
    }

    private fun configurarRecycler() {
        adapter = ListaAdapter(
            listaExibicao,
            onRemover = { item -> removerItem(item) },
            onEditar = { item -> editarItem(item) }
        )

        binding.recyclerLista.layoutManager = LinearLayoutManager(this)
        binding.recyclerLista.adapter = adapter
    }

    private fun atualizarListaExibicao() {
        listaExibicao.clear()
        
        // Agrupar itens por grupo (Mercado, Roupas, etc.)
        val itensAgrupados = listaItensOriginal.groupBy { it.grupo }

        // Para cada grupo, adicionar um Header e depois seus itens
        itensAgrupados.forEach { (grupo, itens) ->
            listaExibicao.add(grupo) // Header
            listaExibicao.addAll(itens.sortedBy { it.nome }) // Itens em ordem alfabética
        }

        adapter.notifyDataSetChanged()
    }

    private fun configurarBotaoAdicionar() {
        binding.botaoAdicionar.setOnClickListener {
            abrirModalGrupos()
        }
    }

    private fun abrirModalGrupos() {
        val grupos = MockData.gruposIniciais.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Onde você vai comprar?")
            .setItems(grupos) { _, index ->
                val grupoSelecionado = grupos[index]
                if (grupoSelecionado == "Mercado") {
                    abrirModalCategorias(grupoSelecionado)
                } else {
                    criarNovoItemManual(grupoSelecionado)
                }
            }
            .setNeutralButton("Novo Local") { _, _ ->
                adicionarNovoGrupo()
            }
            .show()
    }

    private fun adicionarNovoGrupo() {
        val input = EditText(this)
        input.hint = "Ex: Padaria do João, Farmácia..."

        AlertDialog.Builder(this)
            .setTitle("Novo Local de Compra")
            .setView(input)
            .setPositiveButton("Criar") { _, _ ->
                val novoGrupo = input.text.toString()
                if (novoGrupo.isNotBlank()) {
                    MockData.gruposIniciais.add(novoGrupo)
                    abrirModalGrupos()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun abrirModalCategorias(grupo: String) {
        val categorias = MockData.categorias.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("$grupo - Categorias")
            .setItems(categorias) { _, index ->
                val categoriaSelecionada = categorias[index]
                abrirModalProdutos(grupo, categoriaSelecionada)
            }
            .setNegativeButton("Voltar") { _, _ -> abrirModalGrupos() }
            .show()
    }

    private fun abrirModalProdutos(grupo: String, categoria: String) {
        val produtos = MockData.produtosPorCategoria[categoria] ?: emptyList()
        val produtosArray = produtos.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle(categoria)
            .setItems(produtosArray) { _, index ->
                val produtoSelecionado = produtosArray[index]
                pedirQuantidade(grupo, categoria, produtoSelecionado)
            }
            .setNegativeButton("Voltar") { _, _ -> abrirModalCategorias(grupo) }
            .show()
    }

    private fun pedirQuantidade(grupo: String, categoria: String, nome: String) {
        val input = EditText(this)
        input.hint = "Quantidade"
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        input.setText("1")
        input.setSelection(1)

        AlertDialog.Builder(this)
            .setTitle("Quantas unidades?")
            .setMessage("$nome ($grupo)")
            .setView(input)
            .setPositiveButton("Adicionar") { _, _ ->
                val qtdText = input.text.toString()
                val qtd = if (qtdText.isNotBlank()) qtdText.toInt() else 1
                adicionarItem(grupo, categoria, nome, qtd)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun adicionarItem(grupo: String, categoria: String, nome: String, quantidade: Int) {
        val data = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        val novoItem = ItemLista(nome, categoria, grupo, data, quantidade)
        listaItensOriginal.add(novoItem)
        atualizarListaExibicao()
    }

    private fun criarNovoItemManual(grupo: String, categoriaSugerida: String = "Geral") {
        val input = EditText(this)
        input.hint = "Nome do item"

        AlertDialog.Builder(this)
            .setTitle("Novo Item em $grupo")
            .setView(input)
            .setPositiveButton("Próximo") { _, _ ->
                val nome = input.text.toString()
                if (nome.isNotBlank()) {
                    pedirQuantidade(grupo, categoriaSugerida, nome)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun removerItem(item: ItemLista) {
        listaItensOriginal.remove(item)
        atualizarListaExibicao()
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
                    atualizarListaExibicao()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}