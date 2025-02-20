package Core;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;


public class Grafici {
    // Store data for each simulation
    private static class SimulationData {
        double qosProbability;
        double avgBi;
        double utility;
        double TotalFlows;
        double CostoFlows;
        double N_IPN;
        double avg_p_z_i;

        SimulationData(double qosProbability, double avgBi, double utility, double TotalFlows, double CostoFlows, double N_IPN, double avg_p_z_i) {
            this.qosProbability = qosProbability;
            this.avgBi = avgBi;
            this.utility = utility;
            this.TotalFlows = TotalFlows;
            this.CostoFlows = CostoFlows;
            this.N_IPN = N_IPN;
            this.avg_p_z_i = avg_p_z_i;
        }
    }

    private List<SimulationData> simulationResults;

    public Grafici() {
        this.simulationResults = new ArrayList<>();
    }

    // Method to add data from each simulation
    public void addSimulationResult(double qosProbability, double avgBi, double utility, double TotalFlows, double CostoFlows, double N_IPN, double avg_p_z_i) {
        simulationResults.add(new SimulationData(qosProbability, avgBi, utility, TotalFlows, CostoFlows, N_IPN, avg_p_z_i));
    }

    // Grafico 1:
    public void generateQoSvsBiGrafico() {
        // Crea una serie XY per i dati
        XYSeries series = new XYSeries("QoS vs Mean B_i");

        // Aggiungi i punti alla serie
        for (SimulationData data : simulationResults) {
            series.add(data.avgBi, data.qosProbability);
        }

        // Crea il dataset
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        // Crea il grafico
        JFreeChart chart = ChartFactory.createXYLineChart(
                "QoS Denied Probability vs Mean value of tasks deadline", // titolo
                "Mean value of tasks deadline", // etichetta asse x
                "QoS Denied Probability", // etichetta asse y
                dataset, // dataset
                PlotOrientation.VERTICAL,
                true, // mostra legenda
                true, // usa tooltips
                false // usa URLs
        );

        // Ottieni il plot per personalizzare il rendering
        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // Imposta lo stile della linea e dei punti
        renderer.setSeriesLinesVisible(0, true);  // Mostra la linea
        renderer.setSeriesShapesVisible(0, true); // Mostra i punti
        renderer.setSeriesStroke(0, new BasicStroke(2.0f)); // Spessore della linea
        renderer.setSeriesPaint(0, Color.BLUE);

        plot.setRenderer(renderer);

        // Imposta lo sfondo del grafico a bianco
        chart.setBackgroundPaint(Color.WHITE);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        // Crea il frame per visualizzare il grafico
        JFrame frame = new JFrame("Grafico 1");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setSize(800, 600);
        frame.setVisible(true);

        //stampo i dati nel log per controllo
        System.out.println("\n=== DATI GRAFICO 1: QoS Denied Probability vs Mean value of tasks deadline ===");
        for (int i = 0; i < simulationResults.size(); i++) {
            SimulationData data = simulationResults.get(i);
            System.out.printf("Simulazione %d:\n", i + 1);
            System.out.printf(" Mean value of tasks deadline: %.2f\n", data.avgBi);
            System.out.printf("  QoS Denied Probability: %.2f\n", data.qosProbability);
        }
    }

    // Grafico 2:
    public void generateUtilityVsBiGrafico() {
        // Crea una serie XY per i dati
        XYSeries series = new XYSeries("Utility vs Mean B_i");

        // Aggiungi i punti alla serie
        for (SimulationData data : simulationResults) {
            series.add(data.avgBi, data.utility);
        }

        // Crea il dataset
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        // Crea il grafico
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Utility vs Mean value of tasks deadline", // titolo
                "Mean value of tasks deadline", // etichetta asse x
                "Utility", // etichetta asse y
                dataset, // dataset
                PlotOrientation.VERTICAL,
                true, // mostra legenda
                true, // usa tooltips
                false // usa URLs
        );

        // Ottieni il plot per personalizzare il rendering
        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // Imposta lo stile della linea e dei punti
        renderer.setSeriesLinesVisible(0, true);  // Mostra la linea
        renderer.setSeriesShapesVisible(0, true); // Mostra i punti
        renderer.setSeriesStroke(0, new BasicStroke(2.0f)); // Spessore della linea
        renderer.setSeriesPaint(0, Color.RED);

        plot.setRenderer(renderer);

        // Imposta lo sfondo del grafico a bianco
        chart.setBackgroundPaint(Color.WHITE);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        // Crea il frame per visualizzare il grafico
        JFrame frame = new JFrame("Grafico 2");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setSize(800, 600);
        frame.setVisible(true);

        //stampo i dati nel log per controllo
        System.out.println("\n=== DATI GRAFICO 2: Utility vs Mean value of tasks deadline ===");
        for (int i = 0; i < simulationResults.size(); i++) {
            SimulationData data = simulationResults.get(i);
            System.out.printf("Simulazione %d:\n", i + 1);
            System.out.printf(" Mean value of tasks deadline: %.2f\n", data.avgBi);
            System.out.printf("  Utility: %.2f\n", data.utility);
        }
    }

    // Grafico 3:
    public void generateUtilityVsTasksGrafico() {
        // Crea una serie XY per i dati
        XYSeries series = new XYSeries("Utility vs Number of tasks");

        // Aggiungi i punti alla serie
        for (SimulationData data : simulationResults) {
            series.add(data.TotalFlows, data.utility);
        }

        // Crea il dataset
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        // Crea il grafico
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Utility vs Number of tasks", // titolo
                "Number of tasks", // etichetta asse x
                "Utility", // etichetta asse y
                dataset, // dataset
                PlotOrientation.VERTICAL,
                true, // mostra legenda
                true, // usa tooltips
                false // usa URLs
        );

        // Ottieni il plot per personalizzare il rendering
        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // Imposta lo stile della linea e dei punti
        renderer.setSeriesLinesVisible(0, true);  // Mostra la linea
        renderer.setSeriesShapesVisible(0, true); // Mostra i punti
        renderer.setSeriesStroke(0, new BasicStroke(2.0f)); // Spessore della linea
        renderer.setSeriesPaint(0, Color.MAGENTA);
        plot.setRenderer(renderer);

        // Imposta lo sfondo del grafico a bianco
        chart.setBackgroundPaint(Color.WHITE);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        // Crea il frame per visualizzare il grafico
        JFrame frame = new JFrame("Grafico 3");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setSize(800, 600);
        frame.setVisible(true);

        //stampo i dati nel log per controllo
        System.out.println("\n=== DATI GRAFICO 3: Utility vs Number of tasks ===");
        for (int i = 0; i < simulationResults.size(); i++) {
            SimulationData data = simulationResults.get(i);
            System.out.printf("Simulazione %d:\n", i + 1);
            System.out.printf(" Number of tasks: %.2f\n", data.TotalFlows);
            System.out.printf("  Utility: %.2f\n", data.utility);
        }
    }

    // Grafico 4:
    public void generateQoSVsRiGrafico() {
        // Crea una serie XY per i dati
        XYSeries series = new XYSeries("QoS vs Mean R_i");

        // Aggiungi i punti alla serie
        for (SimulationData data : simulationResults) {
            series.add(data.CostoFlows, data.qosProbability);
        }

        // Crea il dataset
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        // Crea il grafico
        JFreeChart chart = ChartFactory.createXYLineChart(
                "QoS Denied Probability vs Mean value of tasks cost", // titolo
                "Mean value of tasks cost", // etichetta asse x
                "QoS Denied Probability", // etichetta asse y
                dataset, // dataset
                PlotOrientation.VERTICAL,
                true, // mostra legenda
                true, // usa tooltips
                false // usa URLs
        );

        // Ottieni il plot per personalizzare il rendering
        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // Imposta lo stile della linea e dei punti
        renderer.setSeriesLinesVisible(0, true);  // Mostra la linea
        renderer.setSeriesShapesVisible(0, true); // Mostra i punti
        renderer.setSeriesStroke(0, new BasicStroke(2.0f)); // Spessore della linea
        renderer.setSeriesPaint(0, Color.GREEN);

        plot.setRenderer(renderer);

        // Imposta lo sfondo del grafico a bianco
        chart.setBackgroundPaint(Color.WHITE);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        // Crea il frame per visualizzare il grafico
        JFrame frame = new JFrame("Grafico 4");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setSize(800, 600);
        frame.setVisible(true);

        //stampo i dati nel log per controllo
        System.out.println("\n=== DATI GRAFICO 4: QoS Denied Probability vs Mean value of tasks cost ===");
        for (int i = 0; i < simulationResults.size(); i++) {
            SimulationData data = simulationResults.get(i);
            System.out.printf("Simulazione %d:\n", i + 1);
            System.out.printf(" Mean value of tasks cost: %.2f\n", data.CostoFlows);
            System.out.printf("  QoS Denied Probability: %.2f\n", data.qosProbability);
        }
    }


    // Grafico 5:
    public void generateQoSVsIPNGrafico() {
        // Crea una serie XY per i dati
        XYSeries series = new XYSeries("QoS vs Number of IPNs");

        // Aggiungi i punti alla serie
        for (SimulationData data : simulationResults) {
            series.add(data.N_IPN, data.qosProbability);
        }

        // Crea il dataset
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        // Crea il grafico
        JFreeChart chart = ChartFactory.createXYLineChart(
                "QoS Denied Probability vs Number of IPNs", // titolo
                "Number of IPNs", // etichetta asse x
                "QoS Denied Probability", // etichetta asse y
                dataset, // dataset
                PlotOrientation.VERTICAL,
                true, // mostra legenda
                true, // usa tooltips
                false // usa URLs
        );

        // Ottieni il plot per personalizzare il rendering
        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // Imposta lo stile della linea e dei punti
        renderer.setSeriesLinesVisible(0, true);  // Mostra la linea
        renderer.setSeriesShapesVisible(0, true); // Mostra i punti
        renderer.setSeriesStroke(0, new BasicStroke(2.0f)); // Spessore della linea
        renderer.setSeriesPaint(0, Color.BLACK);

        plot.setRenderer(renderer);

        // Imposta lo sfondo del grafico a bianco
        chart.setBackgroundPaint(Color.WHITE);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        // Crea il frame per visualizzare il grafico
        JFrame frame = new JFrame("Grafico 5");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setSize(800, 600);
        frame.setVisible(true);

        //stampo i dati nel log per controllo
        System.out.println("\n=== DATI GRAFICO 5: QoS Denied Probability vs Number of IPNs ===");
        for (int i = 0; i < simulationResults.size(); i++) {
            SimulationData data = simulationResults.get(i);
            System.out.printf("Simulazione %d:\n", i + 1);
            System.out.printf(" Number of IPNs: %.2f\n", data.N_IPN);
            System.out.printf("  QoS Denied Probability: %.2f\n", data.qosProbability);
        }
    }

    // Grafico 6:
    public void generateUtilityVsP_z_iGrafico() {
        // Crea una serie XY per i dati
        XYSeries series = new XYSeries("Utility vs M_p_z_i");

        // Aggiungi i punti alla serie
        for (SimulationData data : simulationResults) {
            series.add(data.avg_p_z_i, data.utility);
        }

        // Crea il dataset
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        // Crea il grafico
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Utility vs Mean value of processing time", // titolo
                "Mean value of processing time", // etichetta asse x
                "Utility", // etichetta asse y
                dataset, // dataset
                PlotOrientation.VERTICAL,
                true, // mostra legenda
                true, // usa tooltips
                false // usa URLs
        );

        // Ottieni il plot per personalizzare il rendering
        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // Imposta lo stile della linea e dei punti
        renderer.setSeriesLinesVisible(0, true);  // Mostra la linea
        renderer.setSeriesShapesVisible(0, true); // Mostra i punti
        renderer.setSeriesStroke(0, new BasicStroke(2.0f)); // Spessore della linea
        renderer.setSeriesPaint(0, new Color(139, 69, 19));

        plot.setRenderer(renderer);

        // Imposta lo sfondo del grafico a bianco
        chart.setBackgroundPaint(Color.WHITE);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        // Crea il frame per visualizzare il grafico
        JFrame frame = new JFrame("Grafico 6");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setSize(800, 600);
        frame.setVisible(true);

        //stampo i dati nel log per controllo
        System.out.println("\n=== DATI GRAFICO 6: Utility vs Mean value of processing time ===");
        for (int i = 0; i < simulationResults.size(); i++) {
            SimulationData data = simulationResults.get(i);
            System.out.printf("Simulazione %d:\n", i + 1);
            System.out.printf(" Mean value of processing time: %.2f\n", data.avg_p_z_i);
            System.out.printf("  Utility: %.2f\n", data.utility);
        }
    }


    // Grafico 7:
    public void generateQoSVsP_z_iGrafico() {
        // Crea una serie XY per i dati
        XYSeries series = new XYSeries("QoS vs M_p_z_i");

        // Aggiungi i punti alla serie
        for (SimulationData data : simulationResults) {
            series.add(data.avg_p_z_i, data.qosProbability);
        }

        // Crea il dataset
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        // Crea il grafico
        JFreeChart chart = ChartFactory.createXYLineChart(
                "QoS Denied Probability vs Mean value of processing time", // titolo
                "Mean value of processing time", // etichetta asse x
                "QoS Denied Probability", // etichetta asse y
                dataset, // dataset
                PlotOrientation.VERTICAL,
                true, // mostra legenda
                true, // usa tooltips
                false // usa URLs
        );

        // Ottieni il plot per personalizzare il rendering
        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // Imposta lo stile della linea e dei punti
        renderer.setSeriesLinesVisible(0, true);  // Mostra la linea
        renderer.setSeriesShapesVisible(0, true); // Mostra i punti
        renderer.setSeriesStroke(0, new BasicStroke(2.0f)); // Spessore della linea
        renderer.setSeriesPaint(0, new Color(255, 165, 0));

        plot.setRenderer(renderer);

        // Imposta lo sfondo del grafico a bianco
        chart.setBackgroundPaint(Color.WHITE);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        // Crea il frame per visualizzare il grafico
        JFrame frame = new JFrame("Grafico 7");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setSize(800, 600);
        frame.setVisible(true);

        //stampo i dati nel log per controllo
        System.out.println("\n=== DATI GRAFICO 7: QoS Denied Probability vs Mean value of processing time ===");
        for (int i = 0; i < simulationResults.size(); i++) {
            SimulationData data = simulationResults.get(i);
            System.out.printf("Simulazione %d:\n", i + 1);
            System.out.printf(" QoS Denied Probability: %.2f\n", data.qosProbability);
            System.out.printf("  Utility: %.2f\n", data.utility);
        }
    }
}