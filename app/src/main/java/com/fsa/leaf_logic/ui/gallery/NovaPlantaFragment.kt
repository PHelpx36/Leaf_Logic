package com.fsa.leaf_logic.ui.gallery

import android.R
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fsa.leaf_logic.Planta
import com.fsa.leaf_logic.RetrofitClient
import com.fsa.leaf_logic.UserViewModel
import com.fsa.leaf_logic.databinding.ActivityMainBinding
import com.fsa.leaf_logic.databinding.FragmentNovaPlantaBinding
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NovaPlantaFragment : Fragment() {

    private var _binding: FragmentNovaPlantaBinding? = null
    private lateinit var view: ActivityMainBinding
    private lateinit var userIdTextView: TextView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentNovaPlantaBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //val textView: TextView = binding.textGallery
        galleryViewModel.text.observe(viewLifecycleOwner) {
            //textView.text = it
        }

        val userViewModel: UserViewModel by activityViewModels()

        val cadastrarButton = binding.cadastrar

        // Configurar ação do botão "Cadastrar"
        cadastrarButton.setOnClickListener {
            // Capturar os textos inseridos nos EditTexts
            val nomePlanta = binding.nomePlanta.text.toString()
            val nomeVaso = binding.nomeVaso.text.toString()
            val especie = binding.especies.selectedItem.toString()

            // Verifique os valores capturados (você pode substituir essa parte por outra lógica)
            if (nomePlanta.isNotEmpty() && nomeVaso.isNotEmpty() && especie.isNotEmpty()) {
                // Ações após capturar os dados
                Toast.makeText(requireContext(), "Nome: $nomePlanta, Espécie: $especie, Vaso: $nomeVaso", Toast.LENGTH_SHORT).show()

                val planta = Planta(
                    usuarioId = userViewModel.userId.value.toString().toInt(),
                    nome = nomePlanta,
                    especie = especie,
                    equipamentoId = 0,
                    descricao = "Não preenchido",
                    dataCadastro = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(Calendar.getInstance().time),
                    dataUpdate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(Calendar.getInstance().time)
                )

                buscarEquipamento(nomeVaso, planta)
            } else {
                // Mostrar mensagem de erro se algum campo estiver vazio
                Toast.makeText(requireContext(), "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show()
            }
        }
        setupSpinner()

        return root
    }

    private fun buscarEquipamento(equipamentoNome: String, planta: Planta) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getEquipamentoPorNome(equipamentoNome)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Equipamento encontrado com sucesso!", Toast.LENGTH_SHORT).show()
                        Handler(Looper.getMainLooper()).postDelayed({
                            binding.spinner2.visibility = View.INVISIBLE
                        }, 2000)

                        planta.equipamentoId = response.body()?.id

                        cadastrarPlanta(planta)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Erro, falha ao encontrar o equipamento!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Erro de rede: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Erro HTTP: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Erro: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun cadastrarPlanta(planta: Planta) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.criarPlanta(planta)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Planta cadastrada com sucesso!", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(com.fsa.leaf_logic.R.id.action_nav_novaPlanta_to_nav_home)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Erro, tente novamente mais tarde!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Erro de rede: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Erro HTTP: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Erro: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun setupSpinner() {
        // Crie um ArrayAdapter com as opções e o layout padrão de spinner
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, listOf("Selecione", "Feijão", "Pimenta", "Capim", "Outro"))

        // Defina o layout dropdown do Spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Associe o adapter ao Spinner
        binding.especies.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}