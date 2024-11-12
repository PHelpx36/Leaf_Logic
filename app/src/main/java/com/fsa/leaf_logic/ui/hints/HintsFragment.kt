package com.fsa.leaf_logic.ui.hints

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.androidplot.xy.XYPlot
import com.fsa.leaf_logic.Hint
import com.fsa.leaf_logic.HintImagem
import com.fsa.leaf_logic.R
import com.fsa.leaf_logic.RetrofitClient
import com.fsa.leaf_logic.databinding.FragmentHintsBinding
import com.fsa.leaf_logic.databinding.FragmentSlideshowBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class HintsFragment : Fragment() {

    private var _binding: FragmentHintsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHintsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val speciesSpinner = binding.speciesSpinner
        val searchButton = binding.searchButton

        // Exemplo de lista de espécies. Carregue de uma API se necessário.
        val speciesList = listOf("Orquídea", "Feijão")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, speciesList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        speciesSpinner.adapter = adapter

        // Configura o botão para buscar a dica da espécie selecionada
        searchButton.setOnClickListener {
            val selectedSpecies = speciesSpinner.selectedItem.toString()
            getHint(selectedSpecies)
        }

        return binding.root
    }

    private fun displayHints(hintResponse: Hint, images: List<HintImagem>) {
        binding.container.removeAllViews() // Limpa o container antes de adicionar novas views
        val hintText = hintResponse.conteudo

        var imageIndex = 0
        val titlePattern = "\\((.*?)\\)".toRegex() // Padrão para encontrar textos dentro de parênteses

        hintText.split("{imagem}").forEach { textSegment ->
            // Verifica se o segmento contém um título
            val titleMatch = titlePattern.find(textSegment)
            if (titleMatch != null) {
                // Remove os parênteses e pega o texto dentro
                val titleWithoutParentheses = titleMatch.groupValues[1]

                // Adiciona o título com estilo especial (negrito e maior)
                val titleTextView = TextView(requireContext()).apply {
                    text = titleWithoutParentheses // Texto sem os parênteses
                    textSize = 20f // Tamanho maior
                    setTypeface(null, android.graphics.Typeface.BOLD) // Aplica negrito
                    setPadding(16, 16, 16, 16)
                }
                binding.container.addView(titleTextView)
            } else {
                // Adiciona o texto normal
                val textView = TextView(requireContext()).apply {
                    text = textSegment
                    textSize = 16f
                    setPadding(16, 16, 16, 16)
                }
                binding.container.addView(textView)
            }

            // Se houver uma imagem a seguir, insere um ImageView com ela
            if (imageIndex < images.size) {
                val base64Image = images[imageIndex].imagem
                val bitmap = decodeBase64ToBitmap(base64Image)
                val imageView = ImageView(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    setImageBitmap(bitmap)
                    adjustViewBounds = true
                }
                binding.container.addView(imageView)
                imageIndex++
            }
        }
    }


    fun decodeBase64ToBitmap(base64Str: String): Bitmap? {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    private fun getHint(especie: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getHintPorEspecie(especie)

                withContext(Dispatchers.Main) {
                    if (response != null) {
                        getHintImages(response)

                    } else {
                        Toast.makeText(
                            context,
                            "Nenhuma notificação concluída.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Erro de rede: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erro HTTP: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun getHintImages(hint: Hint) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getHintImagens(hint.id.toString())

                withContext(Dispatchers.Main) {
                    if (response != null) {
                        Toast.makeText(
                            context,
                            "Dicas carregas com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                        displayHints(hint, response)

                    } else {
                        Toast.makeText(
                            context,
                            "Nenhuma notificação concluída.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Erro de rede: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erro HTTP: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}