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
import com.fsa.leaf_logic.databinding.ActivityFormCadastroBinding

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class FormCadastro : AppCompatActivity() {

    private lateinit var view: ActivityFormCadastroBinding
    private lateinit var db: UsuarioOpenHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        view = ActivityFormCadastroBinding.inflate(layoutInflater)
        setContentView(view.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.hide()

        db = UsuarioOpenHelper(this)

        view.button.setOnClickListener {
            val nome = view.nome.text.toString()
            val email = view.email.text.toString()
            val senha = view.senha.text.toString()

            val user = User(nome = nome, email = email, senha = senha)

            if(user.nome != "" && user.email != "" && user.senha != ""){
                view.spinner.visibility = View.VISIBLE

                Handler(Looper.getMainLooper()).postDelayed({
                    view.spinner.visibility = View.INVISIBLE
                }, 2000)
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = RetrofitClient.apiService.criarUsuario(user)
                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@FormCadastro, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@FormCadastro, "Erro ao cadastrar usuário: ${response.code()}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: IOException) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@FormCadastro, "Erro de rede: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: HttpException) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@FormCadastro, "Erro HTTP: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@FormCadastro, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                finish()
            }
            else{
                Toast.makeText(this, "Preencha todos os campos porfavor!", Toast.LENGTH_SHORT).show()
            }
        }

        view.voltaLogin.setOnClickListener{
            telaLogin()
        }
    }
    private fun telaLogin() {
        val intent = Intent(this, FormLogin::class.java)
        startActivity(intent)
    }
}