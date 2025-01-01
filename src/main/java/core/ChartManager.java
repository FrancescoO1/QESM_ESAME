package core;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChartManager extends JFrame {
    private final List<XYSeriesCollection> datasets;
    private final List<JFreeChart> charts;
    private final List<XYSeries> seriesList;
    private final DecimalFormat decimalFormat;
    private final JPanel mainPanel;
    private int currentCharts;

    public ChartManager(String title, int initialCharts) {
        super(title);

        this.datasets = new ArrayList<>();
        this.charts = new ArrayList<>();
        this.seriesList = new ArrayList<>();
        this.currentCharts = 0;

        // Use GridBagLayout for more flexible layout management
        this.mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // Configurazione del formato numerico per parsing corretto
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ITALIAN);
        this.decimalFormat = new DecimalFormat("#,##0.00", symbols);

        setContentPane(new JScrollPane(mainPanel));

        // Add initial charts
        for (int i = 0; i < initialCharts; i++) {
            addNewChart("Grafico " + (i + 1));
        }

        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public int addNewChart(String chartTitle) {
        // Create new series and dataset
        XYSeries series = new XYSeries(chartTitle);
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        // Create new chart
        JFreeChart chart = ChartFactory.createXYLineChart(
                chartTitle,
                "Numero di Flussi",
                "Utilità (%)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Customize the chart
        customizeChart(chart);

        // Add to collections
        seriesList.add(series);
        datasets.add(dataset);
        charts.add(chart);

        // Update layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = currentCharts;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Create chart panel with fixed preferred size
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 400));
        mainPanel.add(chartPanel, gbc);

        currentCharts++;

        // Refresh the frame
        revalidate();
        pack();

        return currentCharts - 1; // Return the index of the new chart
    }

    public void removeChart(int chartIndex) {
        if (chartIndex < 0 || chartIndex >= currentCharts) {
            throw new IllegalArgumentException("Invalid chart index");
        }

        // Remove from collections
        seriesList.remove(chartIndex);
        datasets.remove(chartIndex);
        charts.remove(chartIndex);

        // Remove from panel and rebuild layout
        mainPanel.removeAll();
        currentCharts--;

        // Rebuild the layout
        for (int i = 0; i < currentCharts; i++) {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.insets = new Insets(5, 5, 5, 5);

            ChartPanel chartPanel = new ChartPanel(charts.get(i));
            chartPanel.setPreferredSize(new Dimension(800, 400));
            mainPanel.add(chartPanel, gbc);
        }

        // Refresh the frame
        revalidate();
        pack();
    }

    private void customizeChart(JFreeChart chart) {
        XYPlot plot = (XYPlot) chart.getPlot();

        // Personalizzazione dello sfondo
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.GRAY);
        plot.setRangeGridlinePaint(Color.GRAY);

        // Personalizzazione delle linee del grafico
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, true);
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShape(0, new Rectangle(-4, -4, 8, 8));
        plot.setRenderer(renderer);

        // Configurazione dell'asse delle X
        NumberAxis domainAxis = new NumberAxis("Numero dei Flussi");
        domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        plot.setDomainAxis(domainAxis);

        // Configurazione dell'asse Y
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setRange(0, 100);
        rangeAxis.setTickUnit(new NumberTickUnit(10));
        rangeAxis.setLabel("Utilità (%)");
    }

    public void updateChart(MatchingGame matchingGame, List<NodoSorgente> nodiSorgenti) {
        updateChart(0, matchingGame, nodiSorgenti); // Update first chart by default, aggiorno in modo automatico il primo grafico, infatti ho l'indice 0, posso decidere io quindi che grafico aggiornare
    }

    public void updateChart(int chartIndex, MatchingGame matchingGame, List<NodoSorgente> nodiSorgenti) {  //metodo che fa i conti
        try {
            int totalFlows = nodiSorgenti.stream()
                    .mapToInt(sorgente -> sorgente.getFlussi().size())
                    .sum();

            // Ottieni direttamente il valore dell'utilità come double
            double utility = matchingGame.calcolaUtilita();

            updateChart(chartIndex, totalFlows, utility);
        } catch (Exception e) {
            System.err.println("Errore nel parsing dell'utilità: " + e.getMessage());
        }
    }

    public void updateChart(int chartIndex, int totalFlows, double utility) { //metodo che crea il grafico aggiornandolo
        if (chartIndex < 0 || chartIndex >= seriesList.size()) {
            throw new IllegalArgumentException("Indice del grafico non valido");
        }
        seriesList.get(chartIndex).add(totalFlows, utility);
    }

    public void resetAllCharts() {
        for (XYSeries series : seriesList) {
            series.clear();
        }
    }

    public void resetChart(int chartIndex) {
        if (chartIndex < 0 || chartIndex >= seriesList.size()) {
            throw new IllegalArgumentException("Indice del grafico non valido");
        }
        seriesList.get(chartIndex).clear();
    }

    public static ChartManager createAndShowChart() {
        ChartManager chartManager = new ChartManager("Utilità vs Numero di Flussi", 1);
        chartManager.setVisible(true);
        return chartManager;
    }
}