package core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;


//TODO FAI I GRAFICI del paper in un'altra classe
//TODO cambiare i nomi nell'interfaccia grafica e farli tutti in italiano



public class IPNInterface extends JFrame {
    private final MatchingGame matchingGame;
    private final List<NodoSorgente> nodiSorgente;
    private final List<NodoIPN> nodiIPN;
    private final Map<NodoSorgente, Point> sorgentiPositions;
    private Map<Flusso, Boolean> flussiVisibili;
    private final Map<NodoIPN, Point> ipnPositions;
    private int currentStep = 0;
    private final NetworkPanel networkPanel;
    private final JPanel legendPanel;
    private final Map<NodoIPN, Double> initialCapacities;

    private static int currentIteration = 0;

    public IPNInterface(MatchingGame matchingGame) {
        this.matchingGame = matchingGame;
        this.nodiSorgente = matchingGame.getNodiSorgenti();
        this.nodiIPN = matchingGame.getNodiIPN();
        this.sorgentiPositions = new HashMap<>();
        this.ipnPositions = new HashMap<>();
        this.initialCapacities = new HashMap<>();
        this.flussiVisibili = new HashMap<>();

        // Store initial capacities
        for (NodoIPN ipn : nodiIPN) {
            initialCapacities.put(ipn, ipn.getL_z());
        }

        for (NodoSorgente sorgente : nodiSorgente) {
            for (Flusso flusso : sorgente.getFlussi()) {
                flussiVisibili.put(flusso, false);
            }
        }

        setTitle("IPN Network Visualization");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create main components
        networkPanel = new NetworkPanel(matchingGame);

        // fai in modo che il network panel abbia le scrollbars
        JScrollPane scrollPane = new JScrollPane(networkPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);


        // Panel legenda
        legendPanel = new JPanel();
        legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
        legendPanel.setBackground(new Color(240, 240, 240)); // Imposta uno sfondo simile
        legendPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Aggiungi un bordo


        // Add components to frame
        add(legendPanel, BorderLayout.EAST);

        // Initialize node positions
        initializePositions();

        // Set frame properties
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setVisible(true);

        updateLegendPanel(matchingGame);
    }

    public static void setCurrentIteration(int currentIteration) {
        IPNInterface.currentIteration = currentIteration;

    }

    private void initializePositions() {
        int startY = 150;  // Increased to make room for info panel
        int sorgentiX = 100;
        int ipnX = 800;    // Increased to make room for IPN info
        int spacing = 150;

        // Position source nodes on the left
        for (int i = 0; i < nodiSorgente.size(); i++) {
            sorgentiPositions.put(nodiSorgente.get(i),
                    new Point(sorgentiX, startY + i * spacing));
        }

        // Position IPN nodes on the right
        for (int i = 0; i < nodiIPN.size(); i++) {
            ipnPositions.put(nodiIPN.get(i),
                    new Point(ipnX, startY + i * spacing));
        }

    }

    public void updateLegendPanel(MatchingGame matchingGame) {
        legendPanel.removeAll();// Rimuovi tutti i componenti precedenti

        // Titolo
        JLabel titleLabel = new JLabel("Simulation Information:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Padding: top, left, bottom, right
        legendPanel.add(titleLabel);

        legendPanel.add(Box.createVerticalStrut(10)); // Spazio tra le righe

        // Informazioni generali
        JLabel totalIPNLabel = new JLabel("Total IPN Nodes: " + nodiIPN.size());
        totalIPNLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        totalIPNLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        legendPanel.add(totalIPNLabel);

        JLabel totalSourceLabel = new JLabel("Total Source Nodes: " + nodiSorgente.size());
        totalSourceLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        totalSourceLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        legendPanel.add(totalSourceLabel);

        legendPanel.add(Box.createVerticalStrut(10)); // Spazio tra le righe

        // Capacità iniziale dei nodi IPN
        JLabel initialCapacitiesLabel = new JLabel("Initial IPN Capacities:");
        initialCapacitiesLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        initialCapacitiesLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        legendPanel.add(initialCapacitiesLabel);

        for (NodoIPN ipn : nodiIPN) {
            JLabel ipnCapacityLabel = new JLabel(String.format("IPN%d: %.1f", ipn.getId(), initialCapacities.get(ipn)));
            ipnCapacityLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            ipnCapacityLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            legendPanel.add(ipnCapacityLabel);
        }

        //utilità
        JLabel utilityLabel = new JLabel("Utilità del sistema: " + matchingGame.calcolaUtilita());
        utilityLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        utilityLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        legendPanel.add(utilityLabel);

        legendPanel.add(Box.createVerticalStrut(10)); // Spazio aggiuntivo in basso

        legendPanel.revalidate(); // Aggiorna il layout del pannello
        legendPanel.repaint(); // Forza il ridisegno
    }



    public void UpdateInterfaceStatus() {
        // Aggiorna la visibilità dei flussi basandosi sulle assegnazioni
        for (NodoSorgente sorgente : nodiSorgente) {
            List<Flusso> flussi = sorgente.getFlussi();
            for (int i = 0;  i < flussi.size(); i++) {
                Flusso flusso = flussi.get(i);
                NodoIPN assegnato = matchingGame.getAssegnazioneParziale(flusso);
                flussiVisibili.put(flusso, assegnato != null);
            }
        }

        if (currentStep >= getTotalFlows()) {
            JOptionPane.showMessageDialog(this,
                    "Simulation completed!",
                    "End of Simulation",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        networkPanel.repaint();
    }

    private int getTotalFlows() {
        int total = 0;
        for (NodoSorgente sorgente : nodiSorgente) {
            total += sorgente.getFlussi().size();
        }
        return total;
    }




    private class NetworkPanel extends JPanel {
        private static final int NODE_RADIUS = 30;
        private final MatchingGame matchingGame;

        public NetworkPanel(MatchingGame matchingGame) {
            setBackground(Color.WHITE);
            this.matchingGame = matchingGame;
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(1000, 1800); // Dimensione preferita maggiore per consentire lo scorrimento
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // Disegna i componenti esistenti
            drawNodes(g2d);
            drawConnections(g2d);
            drawFlowTables(g2d);
            drawIterationCounter(g2d);

        }

        private void drawNodes(Graphics2D g2d) {
            // Draw source nodes
            g2d.setColor(new Color(148, 0, 211)); // Purple
            for (Map.Entry<NodoSorgente, Point> entry : sorgentiPositions.entrySet()) {
                Point p = entry.getValue();
                g2d.fillOval(p.x - NODE_RADIUS, p.y - NODE_RADIUS,
                        NODE_RADIUS * 2, NODE_RADIUS * 2);
                g2d.setColor(Color.WHITE);
                g2d.drawString("S" + entry.getKey().getId(),
                        p.x - 6, p.y + 6);
                g2d.setColor(new Color(148, 0, 211));
            }

            // Draw IPN nodes with capacity and waiting time labels
            g2d.setColor(Color.BLUE);
            for (Map.Entry<NodoIPN, Point> entry : ipnPositions.entrySet()) {
                NodoIPN ipn = entry.getKey();
                Point p = entry.getValue();

                // Draw node
                g2d.fillOval(p.x - NODE_RADIUS, p.y - NODE_RADIUS,
                        NODE_RADIUS * 2, NODE_RADIUS * 2);
                g2d.setColor(Color.WHITE);
                g2d.drawString("IPN" + ipn.getId(),
                        p.x - 15, p.y + 6);

                // Draw capacity and waiting time info
                g2d.setColor(Color.BLACK);
                String capacityInfo = String.format("Capacity: %.1f", ipn.getL_z());
                String waitingTimeInfo = String.format("Wait Time: %.1f", ipn.getQ_z_i());
                g2d.drawString(capacityInfo, p.x + NODE_RADIUS + 10, p.y - 5);
                g2d.drawString(waitingTimeInfo, p.x + NODE_RADIUS + 10, p.y + 15);

                g2d.setColor(Color.BLUE);
            }
        }

        private void drawConnections(Graphics2D g2d) {
            g2d.setStroke(new BasicStroke(2));

            // Per ogni nodo sorgente
            for (NodoSorgente sorgente : nodiSorgente) {
                Point sourcePoint = sorgentiPositions.get(sorgente);
                List<Flusso> flussi = sorgente.getFlussi();

                // Calcola gli offset per i punti di partenza dei flussi
                int numFlussi = flussi.size();
                int totalHeight = (numFlussi - 1) * 10;
                int startY = sourcePoint.y - totalHeight / 2;

                // Per ogni flusso del nodo sorgente
                for (int i = 0; i < flussi.size(); i++) {
                    Flusso flusso = flussi.get(i);

                    // Controlla se il flusso è visibile
                    if (flussiVisibili.get(flusso)) {
                        Point adjustedSourcePoint = new Point(
                                sourcePoint.x + NODE_RADIUS,
                                startY + i * 10
                        );

                        NodoIPN assignedIPN = matchingGame.getAssegnazioneParziale(flusso);
                        if (assignedIPN != null) {
                            Point targetPoint = ipnPositions.get(assignedIPN);

                            Color flowColor = getColorForFlow(flusso.getId());
                            g2d.setColor(flowColor);

                            // Disegna la linea
                            g2d.drawLine(
                                    adjustedSourcePoint.x,
                                    adjustedSourcePoint.y,
                                    targetPoint.x - NODE_RADIUS,
                                    targetPoint.y
                            );

                            // Disegna l'etichetta del flusso
                            String flowLabel = "F" + flusso.getId();
                            int midX = (adjustedSourcePoint.x + targetPoint.x - NODE_RADIUS) / 2;
                            int midY = (adjustedSourcePoint.y + targetPoint.y) / 2;

                            FontMetrics fm = g2d.getFontMetrics();
                            int labelWidth = fm.stringWidth(flowLabel);
                            int labelHeight = fm.getHeight();

                            g2d.setColor(Color.WHITE);
                            g2d.fillRect(midX - labelWidth/2 - 2, midY - labelHeight/2,
                                    labelWidth + 4, labelHeight);

                            g2d.setColor(flowColor);
                            g2d.drawString(flowLabel, midX - labelWidth/2, midY + labelHeight/3);
                        }
                    }
                }
            }
        }

        private Color getColorForFlow(int flowId) {
            // Usa l'ID del flusso per generare un colore unico
            float hue = (flowId * 0.618033988749895f) % 1.0f; // Golden ratio per una distribuzione uniforme
            return Color.getHSBColor(hue, 0.8f, 0.9f);
        }

        private void drawFlowTables(Graphics2D g2d) {
            for (Map.Entry<NodoSorgente, Point> entry : sorgentiPositions.entrySet()) {
                NodoSorgente sorgente = entry.getKey();
                Point position = entry.getValue();
                List<Flusso> flussi = sorgente.getFlussi();

                // Draw table
                int tableX = position.x + NODE_RADIUS + 20;
                int tableY = position.y - 40;
                int tableWidth = 150;
                int rowHeight = 20;
                int tableHeight = (flussi.size() + 1) * rowHeight;

                // Draw table background
                g2d.setColor(Color.WHITE);
                g2d.fillRect(tableX, tableY, tableWidth, tableHeight);

                // Draw table border
                g2d.setColor(Color.BLACK);
                g2d.drawRect(tableX, tableY, tableWidth, tableHeight);

                // Draw header
                g2d.drawString("Flussi ID | Capacità", tableX + 5, tableY + 15);
                g2d.drawLine(tableX, tableY + rowHeight,
                        tableX + tableWidth, tableY + rowHeight);

                // Draw flow entries
                for (int i = 0; i < flussi.size(); i++) {
                    Flusso flusso = flussi.get(i);
                    String entry_flussi = String.format("F%d | %.1f",
                            flusso.getId(), flusso.getCapacita());
                    g2d.drawString(entry_flussi, tableX + 5,
                            tableY + (i + 2) * rowHeight - 5);
                }
            }
        }

        private void drawIterationCounter(Graphics2D g2d) {
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));

            String iterationText = "Iteration: " + currentIteration;
            // Calcola la larghezza del testo
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(iterationText);

            // Posiziona il testo in alto a destra
            int x = getWidth() - textWidth - 50; // 10px di margine dal bordo destro
            int y = fm.getHeight(); // Posiziona il testo sotto il margine superiore

            g2d.drawString(iterationText, x, y);
        }

    }
}