package com.fsa.leaf_logic

import android.content.Context
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

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

        supportActionBar?.hide()

        db = UsuarioOpenHelper(this)

        view.button.setOnClickListener {
            val email = view.email.text.toString()
            val senha = view.senha.text.toString()

            if(email != "" && senha != "") {
                if (email == "teste" && senha == "teste") {
                    Toast.makeText(this@FormLogin, "Versão de Teste!", Toast.LENGTH_SHORT).show()

                    val user = User(
                        0,
                        "Teste",
                        "teste@example.com",
                        "123"
                    )

                    telaMenu(user)
                } else {
                    view.spinner.visibility = View.VISIBLE

                    Handler(Looper.getMainLooper()).postDelayed({
                        view.spinner.visibility = View.INVISIBLE
                    }, 2000)
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val response =
                                RetrofitClient.apiService.getUsuarioByEmailNPass(email, senha)
                            withContext(Dispatchers.Main) {
                                if (response.isSuccessful && response.body() != null) {
                                    val usuario = response.body()  // Objeto retornado pela API
                                    Toast.makeText(
                                        this@FormLogin,
                                        "Bem-vindo, ${usuario?.nome}!",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    val user = User(
                                        usuario?.id ?: 0,
                                        usuario?.nome ?: "",
                                        usuario?.email ?: "",
                                        ""
                                    )

                                    telaMenu(user)
                                } else {
                                    Toast.makeText(
                                        this@FormLogin,
                                        "Erro, credenciais inválidas!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } catch (e: IOException) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@FormLogin,
                                    "Erro de rede: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: HttpException) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@FormLogin,
                                    "Erro HTTP: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@FormLogin,
                                    "Erro: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
            else{
                Toast.makeText(this, "Preencha todos os campos porfavor!", Toast.LENGTH_SHORT).show()
            }
        }
        view.cadastrar.setOnClickListener {
            telaCadastro()
        }
    }

    private fun telaMenu(user: User) {
        startActivity(Intent(this, MainActivity::class.java).apply {
            putExtra("user", user) // Passando o objeto User
        })
    }

    private fun telaCadastro() {
        startActivity(Intent(this, FormCadastro::class.java))
    }
}