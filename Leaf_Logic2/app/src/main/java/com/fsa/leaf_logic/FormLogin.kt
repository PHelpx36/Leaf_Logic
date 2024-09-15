package com.fsa.leaf_logic

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fsa.leaf_logic.databinding.ActivityFormLoginBinding

class FormLogin : AppCompatActivity() {

    private lateinit var view: ActivityFormLoginBinding
    private lateinit var db: UsuarioOpenHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        view = ActivityFormLoginBinding.inflate(layoutInflater)
        setContentView(view.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = UsuarioOpenHelper(this)

        view.button.setOnClickListener {
            val email = view.email.text.toString()
            val senha = view.senha.text.toString()

            if (db.verificarUsuario(email, senha)) {
                view.spinner.visibility = View.VISIBLE

                Handler(Looper.getMainLooper()).postDelayed({
                    view.spinner.visibility = View.INVISIBLE
                }, 2000)
                telaMenu()
                Toast.makeText(this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Credenciais inválidas!", Toast.LENGTH_SHORT).show()
            }
        }
        view.cadastrar.setOnClickListener {
            telaCadastro()
        }
    }

    private fun telaMenu() {
        startActivity(Intent(this, MainActivity::class.java).apply { putExtra("userId", db.getUserIdByName(view.email.text.toString())) })
    }
    private fun telaCadastro() {
        startActivity(Intent(this, FormCadastro::class.java))
    }
}