package com.fsa.leaf_logic.ui.slideshow

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.androidplot.xy.XYPlot
import androidx.fragment.app.Fragment
import com.androidplot.xy.LineAndPointFormatter
import com.androidplot.xy.SimpleXYSeries
import com.fsa.leaf_logic.Leitura
import com.fsa.leaf_logic.Leituras
import com.fsa.leaf_logic.RetrofitClient
import com.fsa.leaf_logic.databinding.FragmentSlideshowBinding

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null
    private lateinit var myPlot : XYPlot
    private lateinit var leiturasOrganizadas : Leituras

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        fetchLeituras()

        return root
    }

    private fun plotGraph(){
        if (leiturasOrganizadas.Temperaturas.isNotEmpty() ||
            leiturasOrganizadas.Umidades.isNotEmpty() ||
            leiturasOrganizadas.Luminosidades.isNotEmpty()){

            myPlot = binding.histChart

            val seriesXTemp = leiturasOrganizadas.Temperaturas.indices.map { it.toFloat() }
            val seriesYTemp = leiturasOrganizadas.Temperaturas.map { it.toFloat() }

            val seriesTemp = SimpleXYSeries(seriesXTemp, seriesYTemp, "Temperaturas")

            // Formatação da linha e dos pontos da série
            val formatterTemp = LineAndPointFormatter(
                Color.rgb(79, 145, 5),      // Cor da linha
                Color.BLACK,     // Cor dos pontos
                null,           // Sem preenchimento abaixo da linha
                null            // Sem configuração adicional de estilo
            )

            // Adiciona a série ao gráfico
            myPlot.addSeries(seriesTemp, formatterTemp)

            val seriesXLum = leiturasOrganizadas.Luminosidades.indices.map { it.toFloat() }
            val seriesYLum = leiturasOrganizadas.Luminosidades.map { it.toFloat() }

            val seriesLum = SimpleXYSeries(seriesXLum, seriesYLum, "Temperaturas")

            // Formatação da linha e dos pontos da série
            val formatterLum = LineAndPointFormatter(
                Color.YELLOW,      // Cor da linha
                Color.BLACK,     // Cor dos pontos
                null,           // Sem preenchimento abaixo da linha
                null            // Sem configuração adicional de estilo
            )

            // Adiciona a série ao gráfico
            myPlot.addSeries(seriesLum, formatterLum)

            val seriesXUmi = leiturasOrganizadas.Umidades.indices.map { it.toFloat() }
            val seriesYUmi = leiturasOrganizadas.Umidades.map { it.toFloat() }

            val seriesUmi = SimpleXYSeries(seriesXUmi, seriesYUmi, "Temperaturas")

            // Formatação da linha e dos pontos da série
            val formatterUmi = LineAndPointFormatter(
                Color.BLUE,      // Cor da linha
                Color.BLACK,     // Cor dos pontos
                null,           // Sem preenchimento abaixo da linha
                null            // Sem configuração adicional de estilo
            )

            // Adiciona a série ao gráfico
            myPlot.addSeries(seriesUmi, formatterUmi)

            myPlot.setBackgroundColor(Color.WHITE)

            // Configuração de cor da grade (opcional, para um estilo mais limpo)
            myPlot.graph.gridBackgroundPaint.color = Color.WHITE // Fundo da grade branco
            myPlot.graph.domainGridLinePaint.color = Color.LTGRAY // Linhas da grade no eixo X em cinza claro
            myPlot.graph.rangeGridLinePaint.color = Color.LTGRAY // Linhas da grade no eixo Y em cinza claro


            // Atualiza o gráfico
            myPlot.invalidate()

        }
        else{
            Toast.makeText(requireContext(), "Erro, Nenhuma informação resgatada", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun fetchLeituras() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Chama o endpoint da API para obter as leituras
                val response = RetrofitClient.apiService.getLeituras()

                withContext(Dispatchers.Main) {
                    if (response.isNotEmpty()) {
                        // Exibe uma mensagem de sucesso
                        Toast.makeText(
                            requireContext(),
                            "Leituras obtidas com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()

                        leiturasOrganizadas = organizarLeituras(response)
                        plotGraph()

                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Nenhuma leitura encontrada",
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

    private fun organizarLeituras(response: List<Leitura>): Leituras {
        val leiturasOrganizadas = Leituras()

        response.forEach { leitura ->
            // Adiciona cada valor na lista correspondente
            leiturasOrganizadas.Temperaturas.add(leitura.temperatura)
            leiturasOrganizadas.Umidades.add(leitura.umidade)
            leiturasOrganizadas.Luminosidades.add(leitura.luminosidade)
        }
        return leiturasOrganizadas
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}