package com.example.listacompra

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.listacompra.databinding.ActivityCadastroBinding

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
            // Lógica de cadastro aqui
        }

        binding.botaoVoltarLogin.setOnClickListener {
            finish() // Volta para a tela de login
        }
    }
}