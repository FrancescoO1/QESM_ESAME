package core;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;


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
    private final JPanel preferenceListPanel;
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

        // Create preference list panel
        preferenceListPanel = new JPanel();
        preferenceListPanel.setLayout(new BoxLayout(preferenceListPanel, BoxLayout.Y_AXIS));
        preferenceListPanel.setBackground(new Color(240, 240, 240));
        preferenceListPanel.setBorder(BorderFactory.createTitledBorder("Preference Lists"));

        // Create a scroll pane for preference lists
        JScrollPane prefScrollPane = new JScrollPane(preferenceListPanel);
        prefScrollPane.setPreferredSize(new Dimension(300, 200));

        // Create bottom right panel
        JPanel bottomRightPanel = new JPanel(new BorderLayout());
        bottomRightPanel.add(prefScrollPane, BorderLayout.CENTER);

        // Add to frame
        add(bottomRightPanel, BorderLayout.SOUTH);

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

        //mostra il numero di flussi rifiutati
        Set<Flusso> flussiRifiutati = matchingGame.getFlussiRifiutati();
        JLabel rejectedFlowsLabel = new JLabel("Rejected Flows: " + flussiRifiutati.size());
        rejectedFlowsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        rejectedFlowsLabel.setForeground(Color.RED);
        rejectedFlowsLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        legendPanel.add(rejectedFlowsLabel);

        legendPanel.add(Box.createVerticalStrut(10)); // Spazio tra le righe

        // Capacità iniziale dei nodi IPN
        JLabel initialCapacitiesLabel = new JLabel("Initial IPN Capacities:");
        initialCapacitiesLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        initialCapacitiesLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        legendPanel.add(initialCapacitiesLabel);

        for (NodoIPN ipn : nodiIPN) {
            double capacityPercentage = (ipn.getL_z() / initialCapacities.get(ipn)) * 100;
            JLabel ipnCapacityLabel = new JLabel(String.format("IPN%d: %.1f (%.1f%% remaining)",
                    ipn.getId(), ipn.getL_z(), capacityPercentage));
            ipnCapacityLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            ipnCapacityLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            legendPanel.add(ipnCapacityLabel);
        }

        // Utilità
        double utilita = matchingGame.calcolaUtilita();
        JLabel utilityLabel = new JLabel(String.format("System Utility: %.2f%%", utilita));
        utilityLabel.setFont(new Font("Arial", Font.BOLD, 12));
        if (utilita < 50) {
            utilityLabel.setForeground(Color.RED);
        } else {
            utilityLabel.setForeground(new Color(0, 128, 0)); // Dark green
        }
        utilityLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        legendPanel.add(utilityLabel);

        legendPanel.add(Box.createVerticalStrut(10));

        legendPanel.revalidate();
        legendPanel.repaint();
    }

    public void updatePreferenceLists() {
        preferenceListPanel.removeAll(); // Rimuovi tutti i componenti precedenti

        // Aggiungi titolo
        JLabel titleLabel = new JLabel("Preference Lists:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Padding: top, left, bottom, right
        preferenceListPanel.add(titleLabel);

        preferenceListPanel.add(Box.createVerticalStrut(10)); // Spazio tra le righe

        // Aggiungi le preference list dei flussi
        JLabel flussoLabel = new JLabel("Flows:");
        flussoLabel.setFont(new Font("Arial", Font.BOLD, 12));
        flussoLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        preferenceListPanel.add(flussoLabel);

        for (Flusso flusso : matchingGame.getAllFlussi()) {
            // Crea un pannello per il flusso corrente
            JPanel flussoEntry = new JPanel();
            flussoEntry.setLayout(new BoxLayout(flussoEntry, BoxLayout.Y_AXIS));

            // Etichetta del titolo del flusso
            JLabel flussoLabelFinal = new JLabel("Flusso " + flusso.getId() + ":" + " (Nodo Sorgente " + flusso.getNodoSorgente().getId() + ")");
            flussoLabelFinal.setFont(new Font("Arial", Font.BOLD, 12));
            flussoEntry.add(flussoLabelFinal);

            // Ottieni la lista di preferenza
            List<NodoIPN> preferenceList = matchingGame.getPreferenceListFlusso(flusso);
            if (preferenceList != null) {
                // Aggiungi ogni preferenza come etichetta
                preferenceList.forEach(item -> {
                    JLabel itemLabel = new JLabel("IPN" + item.getId() + " (C_i_z_d=" + item.calcolaC_i_z_d(flusso, matchingGame.calcolaLatenzaRete()) + ")");
                    itemLabel.setFont(new Font("Arial", Font.PLAIN, 12));
                    flussoEntry.add(itemLabel);
                });
            } else {
                // Se la lista è null, mostra un messaggio
                JLabel noPrefLabel = new JLabel("Nessuna preferenza disponibile");
                noPrefLabel.setFont(new Font("Arial", Font.ITALIC, 12));
                flussoEntry.add(noPrefLabel);
            }

            // Impostazioni estetiche
            flussoEntry.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            preferenceListPanel.add(flussoEntry);
        }


        preferenceListPanel.add(Box.createVerticalStrut(10)); // Spazio tra le righe

        // Aggiungi le preference list dei nodi
        JLabel nodoLabel = new JLabel("Nodes:");
        nodoLabel.setFont(new Font("Arial", Font.BOLD, 12));
        nodoLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        preferenceListPanel.add(nodoLabel);

        for (NodoIPN nodo : nodiIPN) {
            // Crea un pannello per il nodo corrente
            JPanel nodoEntry = new JPanel();
            nodoEntry.setLayout(new BoxLayout(nodoEntry, BoxLayout.Y_AXIS));
            //etichetta del titolo del nodo
            JLabel nodoLabelFinal = new JLabel("IPN" + nodo.getId());
            nodoLabelFinal.setFont(new Font("Arial", Font.BOLD, 12));
            nodoEntry.add(nodoLabelFinal);

            // Ottieni la lista di preferenza
            List<Flusso> preferenceList = matchingGame.getPreferenceListNodo(nodo);
            if (preferenceList != null) {
                // Aggiungi ogni preferenza come etichetta
                preferenceList.forEach(item -> {
                    JLabel itemLabel = new JLabel("Flusso " + item.getId() + " nodo sorgente:" + item.getNodoSorgente().getId() +" (B_i=" + item.getB_i() + ", E_z_i=" + (1.0/item.getB_i()) + ")");
                    itemLabel.setFont(new Font("Arial", Font.PLAIN, 12));
                    nodoEntry.add(itemLabel);
                });
            } else {
                // Se la lista è null, mostra un messaggio
                JLabel noPrefLabel = new JLabel("Nessuna preferenza disponibile");
                noPrefLabel.setFont(new Font("Arial", Font.ITALIC, 12));
                nodoEntry.add(noPrefLabel);
            }
            //impostazioni estetiche
            nodoEntry.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            preferenceListPanel.add(nodoEntry);
        }

        preferenceListPanel.revalidate(); // Aggiorna il layout del pannello
    }


    public void UpdateInterfaceStatus() {
        // Aggiorna la visibilità dei flussi basandosi sulle assegnazioni
        for (NodoSorgente sorgente : nodiSorgente) {
            List<Flusso> flussi = sorgente.getFlussi();
            for (int i = 0; i < flussi.size(); i++) {
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

                // Calculate average waiting time for this IPN node
                double avgWaitingTime = calculateWaitingTime(ipn);
                String waitingTimeInfo = String.format("Wait Time: %.1f", avgWaitingTime);

                g2d.drawString(capacityInfo, p.x + NODE_RADIUS + 10, p.y - 5);
                g2d.drawString(waitingTimeInfo, p.x + NODE_RADIUS + 10, p.y + 15);

                g2d.setColor(Color.BLUE);
            }
        }

        private double calculateWaitingTime(NodoIPN ipn) {
            List<Flusso> flussiInCoda = ipn.getFlussiInCoda();
            if (flussiInCoda.isEmpty()) {
                return 0.0;
            }

            double totalWaitingTime = 0.0;
            for (Flusso flusso : flussiInCoda) {
                totalWaitingTime += flusso.calcolaQ_z_i(flusso, flussiInCoda);
            }
            return totalWaitingTime; //se volessi la media dovrei fare totalWaitingTime / flussiInCoda.size()
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
                            g2d.fillRect(midX - labelWidth / 2 - 2, midY - labelHeight / 2,
                                    labelWidth + 4, labelHeight);

                            g2d.setColor(flowColor);
                            g2d.drawString(flowLabel, midX - labelWidth / 2, midY + labelHeight / 3);
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


