package com.example.listacompra

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.listacompra.databinding.ActivityCadastroBinding
import kotlinx.coroutines.launch

class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarBotoes()
    }

    private fun configurarBotoes() {
        binding.botaoCadastrar.setOnClickListener {
            val nome = binding.editNome.text.toString()
            val email = binding.editEmail.text.toString()
            val senha = binding.editSenha.text.toString()
            val confirmarSenha = binding.editConfirmarSenha.text.toString()

            Log.d("CADASTRO", "Botão cadastrar clicado. Nome: $nome, Email: $email")

            if (nome.isBlank() || email.isBlank() || senha.isBlank()) {
                Toast.makeText(this, "⚠️ Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (senha != confirmarSenha) {
                Toast.makeText(this, "❌ As senhas não conferem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "⏳ Enviando cadastro...", Toast.LENGTH_SHORT).show()
            realizarCadastro(nome, email, senha)
        }

        binding.botaoVoltarLogin.setOnClickListener {
            finish()
        }
    }

    private fun realizarCadastro(nome: String, email: String, senha: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.register(CadastroRequest(nome, email, senha))
                
                if (response.isSuccessful) {
                    Log.d("CADASTRO", "Sucesso: ${response.code()}")
                    Toast.makeText(this@CadastroActivity, "✅ Conta criada com sucesso!", Toast.LENGTH_LONG).show()
                    finish() // Volta para a tela de login
                } else {
                    val erroBody = response.errorBody()?.string()
                    Log.e("CADASTRO", "Erro API: $erroBody")
                    Toast.makeText(this@CadastroActivity, "❌ Erro do Servidor: $erroBody", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("CADASTRO", "Falha de Conexão", e)
                Toast.makeText(this@CadastroActivity, "🌐 Erro de Conexão: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }
}