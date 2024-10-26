package com.fsa.leaf_logic.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fsa.leaf_logic.databinding.FragmentNovaPlantaBinding

class NovaPlantaFragment : Fragment() {

    private var _binding: FragmentNovaPlantaBinding? = null

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

        val cadastrarButton = binding.cadastrar

        // Configurar ação do botão "Cadastrar"
        cadastrarButton.setOnClickListener {
            // Capturar os textos inseridos nos EditTexts
            val nomePlanta = binding.nomePlanta.text.toString()
            val especie = binding.especie.text.toString()

            // Verifique os valores capturados (você pode substituir essa parte por outra lógica)
            if (nomePlanta.isNotEmpty() && especie.isNotEmpty()) {
                // Ações após capturar os dados
                Toast.makeText(requireContext(), "Nome: $nomePlanta, Espécie: $especie", Toast.LENGTH_SHORT).show()

                // Aqui você pode adicionar lógica para salvar as informações ou processá-las
            } else {
                // Mostrar mensagem de erro se algum campo estiver vazio
                Toast.makeText(requireContext(), "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show()
            }
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}