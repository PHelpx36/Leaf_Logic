package com.fsa.leaf_logic.ui.gallery

import android.R
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
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
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NovaPlantaFragment : Fragment() {

    private var _binding: FragmentNovaPlantaBinding? = null
    private var ImageUri: Uri? = null
    private val REQUEST_SELECT_IMAGE_IN_ALBUM = 2

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNovaPlantaBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val userViewModel: UserViewModel by activityViewModels()

        binding.imagemPlanta.setOnClickListener{
            openDocumentIntent()
        }

        // Configurar ação do botão "Cadastrar"
        binding.cadastrar.setOnClickListener {
            // Capturar os textos inseridos nos EditTexts
            val nomePlanta = binding.nomePlanta.text.toString()
            val nomeVaso = binding.nomeVaso.text.toString()
            val especie = binding.especies.selectedItem.toString()
            val imagemBase64 = ImageUri?.let { encodeImageToBase64(it) }

            // Verifique os valores capturados (você pode substituir essa parte por outra lógica)
            if (nomePlanta.isNotEmpty() && nomeVaso.isNotEmpty() && especie.isNotEmpty()) {
                // Ações após capturar os dados
                Toast.makeText(requireContext(), "Nome: $nomePlanta, Espécie: $especie, Vaso: $nomeVaso", Toast.LENGTH_SHORT).show()

                val planta = imagemBase64?.let { it1 ->
                    Planta(
                        usuarioId = userViewModel.userId.value.toString().toInt(),
                        nome = nomePlanta,
                        especie = especie,
                        equipamentoId = 0,
                        descricao = "Não preenchido",
                        imagem = it1,
                        dataCadastro = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(Calendar.getInstance().time),
                        dataUpdate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(Calendar.getInstance().time)
                    )
                }

                planta?.let { it1 -> buscarEquipamento(nomeVaso, it1) }
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

    private fun openImageIntent() {
        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhoto, REQUEST_SELECT_IMAGE_IN_ALBUM)
    }

    private fun openDocumentIntent() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*" // Para permitir todos os tipos de arquivos. Para imagens, use "image/*"
        }
        startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM && resultCode == RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            ImageUri = selectedImageUri
            try {
                val inputStream = selectedImageUri?.let {
                    requireContext().contentResolver.openInputStream(
                        it
                    )
                }
                val bitmap = BitmapFactory.decodeStream(inputStream)
                binding.imagemPlanta.setImageBitmap(bitmap)
                inputStream?.close()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Erro: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun encodeImageToBase64(selectedImageUri: Uri): String? {
        val inputStream = requireContext().contentResolver.openInputStream(selectedImageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}