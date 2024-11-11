package com.fsa.leaf_logic.ui.slideshow

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.androidplot.xy.XYPlot
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class SlideshowFragment : Fragment() {

    private val args: SlideshowFragmentArgs by navArgs()
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

        val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        isoDateFormat.timeZone = TimeZone.getTimeZone("UTC") // Ajuste para UTC

        val calendar2 = Calendar.getInstance()
        calendar2.set(calendar2.get(Calendar.YEAR), calendar2.get(Calendar.MONTH), calendar2.get(Calendar.DAY_OF_MONTH))
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        binding.etDate2.setText(dateFormat.format(calendar2.time))
        var fim = isoDateFormat.format(calendar2.time)

        calendar2.add(Calendar.DAY_OF_YEAR, -5)
        binding.etDate.setText(dateFormat.format(calendar2.time))
        var inicio = isoDateFormat.format(calendar2.time)

        binding.etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(), // Contexto
                { _, selectedYear, selectedMonth, selectedDay ->
                    calendar.set(selectedYear, selectedMonth, selectedDay)
                    inicio = isoDateFormat.format(calendar.time)
                    binding.etDate.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        binding.etDate2.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(), // Contexto
                { _, selectedYear, selectedMonth, selectedDay ->
                    calendar.set(selectedYear, selectedMonth, selectedDay)
                    fim = isoDateFormat.format(calendar.time)
                    binding.etDate2.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        fetchLeituras(args.equipamentoId, inicio, fim)

        binding.button.setOnClickListener{
            fetchLeituras(args.equipamentoId, inicio, fim)
        }

        return root
    }

    private fun plotGraph(){
        if (leiturasOrganizadas.Temperaturas.isNotEmpty() ||
            leiturasOrganizadas.Umidades.isNotEmpty() ||
            leiturasOrganizadas.Luminosidades.isNotEmpty()){

            myPlot = binding.histChart

            myPlot.clear()

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

    private fun fetchLeituras(equipamentoId: String, inicio: String, fim: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Chama o endpoint da API para obter as leituras
                val response = RetrofitClient.apiService.getLeiturasByEquipamentoNData(equipamentoId, inicio, fim)

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