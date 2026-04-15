package com.example.listacompra

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listacompra.databinding.ActivityMainBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ListaAdapter
    private val listaItensOriginal = mutableListOf<ItemLista>()
    private val listaExibicao = mutableListOf<Any>()
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        token = getSharedPreferences("app_prefs", Context.MODE_PRIVATE).getString("auth_token", null)

        configurarRecycler()
        configurarBotoes()
        carregarItensApi()
    }

    private fun configurarRecycler() {
        adapter = ListaAdapter(
            listaExibicao,
            onRemover = { item -> removerItem(item) },
            onEditar = { item -> editarItem(item) },
            onToggleCheck = { item, isChecked -> toggleItemCheck(item, isChecked) }
        )

        binding.recyclerLista.layoutManager = LinearLayoutManager(this)
        binding.recyclerLista.adapter = adapter
    }

    private fun carregarItensApi() {
        val authToken = token ?: return
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getItems(authToken)
                if (response.isSuccessful) {
                    listaItensOriginal.clear()
                    response.body()?.let { listaItensOriginal.addAll(it) }
                    atualizarListaExibicao()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Erro ao carregar lista", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun atualizarListaExibicao() {
        listaExibicao.clear()
        val itensAgrupados = listaItensOriginal.groupBy { it.grupo }
        itensAgrupados.forEach { (grupo, itens) ->
            listaExibicao.add(grupo)
            listaExibicao.addAll(itens.sortedBy { it.nome })
        }
        adapter.notifyDataSetChanged()
        
        val temItensMarcados = listaItensOriginal.any { it.marcado }
        binding.botaoFinalizar.visibility = if (temItensMarcados) View.VISIBLE else View.GONE
    }

    private fun configurarBotoes() {
        binding.botaoAdicionar.setOnClickListener {
            abrirModalGrupos()
        }
        
        binding.botaoFinalizar.setOnClickListener {
            confirmarFinalizarLista()
        }

        binding.botaoLogout.setOnClickListener {
            confirmarLogout()
        }
    }

    private fun confirmarLogout() {
        AlertDialog.Builder(this)
            .setTitle("Sair")
            .setMessage("Deseja realmente encerrar a sessão?")
            .setPositiveButton("Sair") { _, _ -> realizarLogout() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun realizarLogout() {
        getSharedPreferences("app_prefs", Context.MODE_PRIVATE).edit().remove("auth_token").apply()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun confirmarFinalizarLista() {
        AlertDialog.Builder(this)
            .setTitle("Finalizar Lista")
            .setMessage("Remover todos os itens marcados?")
            .setPositiveButton("Sim") { _, _ -> removerItensMarcados() }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun removerItensMarcados() {
        val authToken = token ?: return
        val itensParaRemover = listaItensOriginal.filter { it.marcado }
        
        lifecycleScope.launch {
            try {
                itensParaRemover.forEach { item ->
                    item.id?.let { RetrofitClient.api.deleteItem(authToken, it) }
                }
                carregarItensApi()
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Erro ao finalizar lista", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun abrirModalGrupos() {
        val authToken = token ?: return
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getGroups(authToken)
                if (response.isSuccessful) {
                    val grupos = response.body()?.toTypedArray() ?: emptyArray()
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("Onde você vai comprar?")
                        .setItems(grupos) { _, index -> abrirModalCategorias(grupos[index]) }
                        .setNeutralButton("Novo Local") { _, _ -> adicionarNovoGrupo() }
                        .show()
                } else {
                    Toast.makeText(this@MainActivity, "Erro ao buscar locais", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Falha de conexão", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun adicionarNovoGrupo() {
        val input = EditText(this)
        input.hint = "Ex: Farmácia"
        AlertDialog.Builder(this).setTitle("Novo Local").setView(input)
            .setPositiveButton("Criar") { _, _ ->
                val nome = input.text.toString().trim()
                if (nome.isNotBlank()) salvarGrupoApi(nome)
            }.show()
    }

    private fun salvarGrupoApi(nome: String) {
        val authToken = token ?: return
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.addGroup(authToken, mapOf("nome" to nome))
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "Local '$nome' criado!", Toast.LENGTH_SHORT).show()
                    abrirModalCategorias(nome)
                } else {
                    Toast.makeText(this@MainActivity, "Erro ao criar local", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Falha de conexão", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun abrirModalCategorias(grupo: String) {
        val authToken = token ?: return
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getCategories(authToken, grupo)
                if (response.isSuccessful) {
                    val categorias = response.body()?.toTypedArray() ?: emptyArray()
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("$grupo - Categorias")
                        .setItems(categorias) { _, index -> abrirModalProdutos(grupo, categorias[index]) }
                        .setNeutralButton("Nova Categoria") { _, _ -> adicionarNovaCategoria(grupo) }
                        .setNegativeButton("Voltar") { _, _ -> abrirModalGrupos() }
                        .show()
                } else {
                    Toast.makeText(this@MainActivity, "Erro ao buscar categorias: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Falha de conexão", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun adicionarNovaCategoria(grupo: String) {
        val input = EditText(this)
        input.hint = "Ex: Remédios"
        AlertDialog.Builder(this).setTitle("Nova Categoria em $grupo").setView(input)
            .setPositiveButton("Criar") { _, _ ->
                val nome = input.text.toString().trim()
                if (nome.isNotBlank()) salvarCategoriaApi(grupo, nome)
            }.show()
    }

    private fun salvarCategoriaApi(grupo: String, nome: String) {
        val authToken = token ?: return
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.addCategory(authToken, mapOf("nome" to nome, "group" to grupo))
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "Categoria '$nome' criada!", Toast.LENGTH_SHORT).show()
                    abrirModalProdutos(grupo, nome)
                } else {
                    Toast.makeText(this@MainActivity, "Erro ao criar categoria", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Falha de conexão", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun abrirModalProdutos(grupo: String, categoria: String) {
        val authToken = token ?: return
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getProducts(authToken, categoria, grupo)
                if (response.isSuccessful) {
                    val produtos = response.body()?.toTypedArray() ?: emptyArray()
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle(categoria)
                        .setItems(produtos) { _, index -> pedirQuantidade(grupo, categoria, produtos[index]) }
                        .setNeutralButton("Novo Produto") { _, _ -> adicionarNovoProduto(grupo, categoria) }
                        .setNegativeButton("Voltar") { _, _ -> abrirModalCategorias(grupo) }
                        .show()
                } else {
                    Toast.makeText(this@MainActivity, "Erro ao buscar produtos: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Falha de conexão", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun adicionarNovoProduto(grupo: String, categoria: String) {
        val input = EditText(this)
        input.hint = "Ex: Aspirina"
        AlertDialog.Builder(this).setTitle("Novo Produto em $categoria").setView(input)
            .setPositiveButton("Criar") { _, _ ->
                val nome = input.text.toString().trim()
                if (nome.isNotBlank()) salvarProdutoApi(grupo, categoria, nome)
            }.show()
    }

    private fun salvarProdutoApi(grupo: String, categoria: String, nome: String) {
        val authToken = token ?: return
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.addProduct(authToken, mapOf("nome" to nome, "categoria" to categoria, "group" to grupo))
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "Produto '$nome' criado!", Toast.LENGTH_SHORT).show()
                    pedirQuantidade(grupo, categoria, nome)
                } else {
                    Toast.makeText(this@MainActivity, "Erro ao criar produto", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Falha de conexão", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun pedirQuantidade(grupo: String, categoria: String, nome: String) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_quantidade, null)
        val textProduto = view.findViewById<TextView>(R.id.textProduto)
        val textQtdTotal = view.findViewById<TextView>(R.id.textQtdTotal)
        val btnMenos = view.findViewById<MaterialButton>(R.id.btnMenos)
        val btnMais = view.findViewById<MaterialButton>(R.id.btnMais)
        val toggleGroup = view.findViewById<MaterialButtonToggleGroup>(R.id.toggleGroup)

        textProduto.text = "$nome ($grupo)"
        var quantidadeAtual = 1
        var incremento = 1

        fun atualizarUI() {
            textQtdTotal.text = quantidadeAtual.toString()
        }

        toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                incremento = when (checkedId) {
                    R.id.btnUnidade -> 1
                    R.id.btnDezena -> 10
                    R.id.btnCentena -> 100
                    R.id.btnMilhar -> 1000
                    else -> 1
                }
            }
        }

        btnMais.setOnClickListener {
            if (quantidadeAtual + incremento <= 1000000) {
                quantidadeAtual += incremento
                atualizarUI()
            }
        }

        btnMenos.setOnClickListener {
            if (quantidadeAtual - incremento >= 1) {
                quantidadeAtual -= incremento
                atualizarUI()
            } else {
                quantidadeAtual = 1
                atualizarUI()
            }
        }

        AlertDialog.Builder(this)
            .setView(view)
            .setPositiveButton("Adicionar") { _, _ ->
                adicionarItemApi(grupo, categoria, nome, quantidadeAtual)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun adicionarItemApi(grupo: String, categoria: String, nome: String, quantidade: Int) {
        val authToken = token ?: return
        val novoItem = ItemLista(nome = nome, categoria = categoria, grupo = grupo, quantidade = quantidade)
        lifecycleScope.launch {
            try {
                if (RetrofitClient.api.addItem(authToken, novoItem).isSuccessful) {
                    Toast.makeText(this@MainActivity, "Adicionado à lista!", Toast.LENGTH_SHORT).show()
                    carregarItensApi()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Erro ao adicionar item", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun toggleItemCheck(item: ItemLista, isChecked: Boolean) {
        val authToken = token ?: return
        if (item.id == null) {
            Toast.makeText(this, "Erro: Item sem ID", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Atualização Otimista
        item.marcado = isChecked
        atualizarListaExibicao()

        lifecycleScope.launch {
            try {
                // Usando a nova rota de toggle conforme solicitado
                val response = RetrofitClient.api.toggleItem(authToken, item.id)
                if (response.isSuccessful) {
                    // Atualiza o item local com o que veio da API (opcional)
                    response.body()?.let { itemRetornado ->
                        item.marcado = itemRetornado.marcado
                        atualizarListaExibicao()
                    }
                } else {
                    // Reverte em caso de erro
                    item.marcado = !isChecked
                    atualizarListaExibicao()
                    Toast.makeText(this@MainActivity, "Erro ao marcar item", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                item.marcado = !isChecked
                atualizarListaExibicao()
                Toast.makeText(this@MainActivity, "Falha de conexão", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun removerItem(item: ItemLista) {
        val authToken = token ?: return
        lifecycleScope.launch {
            try {
                item.id?.let {
                    if (RetrofitClient.api.deleteItem(authToken, it).isSuccessful) carregarItensApi()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Erro ao remover", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun editarItem(item: ItemLista) {
        val input = EditText(this)
        input.setText(item.nome)
        AlertDialog.Builder(this).setTitle("Editar").setView(input)
            .setPositiveButton("Salvar") { _, _ ->
                val novoNome = input.text.toString().trim()
                if (novoNome.isNotBlank() && item.id != null) {
                    lifecycleScope.launch {
                        try {
                            if (RetrofitClient.api.updateItem(token!!, item.id, mapOf("nome" to novoNome)).isSuccessful) carregarItensApi()
                        } catch (e: Exception) { /* erro */ }
                    }
                }
            }.show()
    }
}