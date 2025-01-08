package core;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChartManager {

    // Metodo generico per creare il grafico a linee con sfondo bianco
    public static JFreeChart createLineChart(String title, String xAxisLabel, String yAxisLabel, Map<Double, Double> data) {
        // Crea la serie di dati
        XYSeries series = new XYSeries(title);
        List<Map.Entry<Double, Double>> sortedEntries = new ArrayList<>(data.entrySet());
        sortedEntries.sort(Map.Entry.comparingByKey());
        for (Map.Entry<Double, Double> entry : sortedEntries) {
            series.add(entry.getKey(), entry.getValue());
        }

        // Imposta i dati
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        // Crea il grafico
        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                xAxisLabel,
                yAxisLabel,
                dataset,
                PlotOrientation.VERTICAL,
                true,               // Legenda
                true,               // Tooltip
                false               // URL
        );

        // Personalizzazione del grafico: sfondo bianco
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);

        return chart;
    }

    // Metodo per visualizzare il grafico
    public static void plotChart(JFreeChart chart, String frameTitle) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(frameTitle);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            // Aggiunge il grafico al pannello
            ChartPanel chartPanel = new ChartPanel(chart);
            frame.add(chartPanel);
            frame.setVisible(true);
        });
    }

    // Metodo per plottare il grafico QoS Denied vs Media B_i
    public static void plotQoSDeniedVsMediaBi() {
        JFreeChart chart = createLineChart(
                "Grafico Qos Denied vs Media B_i",
                "Mean value of tasks deadlines",
                "QoS Denied probability",
                Simulazione.mediaBiPerSimulazione
        );
        plotChart(chart, "Grafico 1");
    }
}

//esempio di nuovo metodo:

//public static void plotUtilitaVsIterazioni(Map<Double, Double> data) {
//    JFreeChart chart = createLineChart(
//            "Utilità del sistema vs Iterazioni",
//            "Iterazioni",
//            "Utilità",
//            data
//    );
//    plotChart(chart, "Grafico Utilità vs Iterazioni");
//}
