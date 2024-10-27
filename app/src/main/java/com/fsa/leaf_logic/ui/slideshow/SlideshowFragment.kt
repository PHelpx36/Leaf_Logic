package com.fsa.leaf_logic.ui.slideshow

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androidplot.xy.XYPlot
import androidx.fragment.app.Fragment
import com.androidplot.xy.LineAndPointFormatter
import com.androidplot.xy.SimpleXYSeries
import com.androidplot.xy.Step
import com.fsa.leaf_logic.databinding.FragmentSlideshowBinding

class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null
    private lateinit var myPlot : XYPlot

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

        myPlot = binding.histChart

        val seriesX = listOf(0, 20, 30, 40, 50, 60) // Valores do eixo X
        val seriesY = listOf(10, 15, 30, 25, 35, 30) // Valores do eixo Y

        val series = SimpleXYSeries(seriesX, seriesY, "Temperaturas")

        // Formatação da linha e dos pontos da série
        val formatter = LineAndPointFormatter(
            Color.rgb(79, 145, 5),      // Cor da linha
            Color.BLACK,     // Cor dos pontos
            null,           // Sem preenchimento abaixo da linha
            null            // Sem configuração adicional de estilo
        )

        // Adiciona a série ao gráfico
        myPlot.addSeries(series, formatter)

        myPlot.setBackgroundColor(Color.WHITE)

        // Configuração de cor da grade (opcional, para um estilo mais limpo)
        myPlot.graph.gridBackgroundPaint.color = Color.WHITE // Fundo da grade branco
        myPlot.graph.domainGridLinePaint.color = Color.LTGRAY // Linhas da grade no eixo X em cinza claro
        myPlot.graph.rangeGridLinePaint.color = Color.LTGRAY // Linhas da grade no eixo Y em cinza claro


        // Atualiza o gráfico
        myPlot.invalidate()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}