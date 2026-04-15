package com.example.listacompra

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.listacompra.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        verificarTokenExistente()
        configurarBotoes()
    }

    private fun verificarTokenExistente() {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = sharedPref.getString("auth_token", null)
        if (token != null) {
            irParaMain()
        }
    }

    private fun configurarBotoes() {
        binding.botaoLogin.setOnClickListener {
            val email = binding.editEmail.text.toString()
            val senha = binding.editSenha.text.toString()

            if (email.isNotBlank() && senha.isNotBlank()) {
                realizarLogin(email, senha)
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.botaoIrParaCadastro.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }
    }

    private fun realizarLogin(email: String, senha: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.login(LoginRequest(email, senha))
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        salvarToken(loginResponse.token)
                        irParaMain()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Erro: Usuário ou senha inválidos", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Erro de conexão: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun salvarToken(token: String) {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("auth_token", "Bearer $token")
            apply()
        }
    }

    private fun irParaMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}