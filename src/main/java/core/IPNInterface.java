package core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

//TODO aggiorna la visualizzazione dell'interfaccia

public class IPNInterface extends JFrame {
    private final MatchingGame matchingGame;
    private final List<NodoSorgente> nodiSorgente;
    private final List<NodoIPN> nodiIPN;
    private final Map<NodoSorgente, Point> sorgentiPositions;
    private final Map<NodoIPN, Point> ipnPositions;
    private int currentStep = 0;
    private final JButton nextStepButton;
    private final NetworkPanel networkPanel;
    private final Map<NodoIPN, Double> initialCapacities;

    public IPNInterface(MatchingGame matchingGame) {
        this.matchingGame = matchingGame;
        this.nodiSorgente = matchingGame.getNodiSorgenti();
        this.nodiIPN = matchingGame.getNodiIPN();
        this.sorgentiPositions = new HashMap<>();
        this.ipnPositions = new HashMap<>();
        this.initialCapacities = new HashMap<>();

        // Store initial capacities
        for (NodoIPN ipn : nodiIPN) {
            initialCapacities.put(ipn, ipn.getL_z());
        }

        setTitle("IPN Network Visualization");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create main components
        networkPanel = new NetworkPanel();
        nextStepButton = new JButton("Next Step");

        // Setup button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(new JLabel("Iteration: "));
        buttonPanel.add(nextStepButton);

        // Add components to frame
        add(networkPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Initialize node positions
        initializePositions();

        // Setup button action
        nextStepButton.addActionListener(e -> {
            currentStep++;
            networkPanel.repaint();
        });

        // Set frame properties
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setVisible(true);
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

    private class NetworkPanel extends JPanel {
        private static final int NODE_RADIUS = 30;

        public NetworkPanel() {
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw information panel first
            drawInfoPanel(g2d);

            // Draw nodes and connections
            drawNodes(g2d);
            drawConnections(g2d);
            drawFlowTables(g2d);

            // Draw iteration counter
            drawIterationCounter(g2d);
        }

        private void drawInfoPanel(Graphics2D g2d) {
            int panelX = getWidth() - 300;
            int panelY = 20;
            int panelWidth = 280;
            int panelHeight = 120;

            // Draw panel background
            g2d.setColor(new Color(240, 240, 240));
            g2d.fillRect(panelX, panelY, panelWidth, panelHeight);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(panelX, panelY, panelWidth, panelHeight);

            // Draw information
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            int textY = panelY + 20;
            g2d.drawString("Simulation Information:", panelX + 10, textY);
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));

            textY += 20;
            g2d.drawString("Total IPN Nodes: " + nodiIPN.size(), panelX + 10, textY);

            textY += 20;
            g2d.drawString("Total Source Nodes: " + nodiSorgente.size(), panelX + 10, textY);

            textY += 20;
            g2d.drawString("Initial IPN Capacities:", panelX + 10, textY);

            textY += 20;
            for (NodoIPN ipn : nodiIPN) {
                g2d.drawString(String.format("IPN%d: %.1f", ipn.getId(),
                        initialCapacities.get(ipn)), panelX + 20, textY);
                textY += 15;
            }
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
            g2d.setColor(Color.BLACK);

            for (NodoSorgente sorgente : nodiSorgente) {
                Point sourcePoint = sorgentiPositions.get(sorgente);
                List<Flusso> flussi = sorgente.getFlussi();

                if (currentStep < flussi.size()) {
                    Flusso currentFlusso = flussi.get(currentStep);
                    NodoIPN assignedIPN = matchingGame.getAssegnazioneParziale(currentFlusso);

                    if (assignedIPN != null) {
                        Point targetPoint = ipnPositions.get(assignedIPN);
                        g2d.drawLine(sourcePoint.x + NODE_RADIUS, sourcePoint.y,
                                targetPoint.x - NODE_RADIUS, targetPoint.y);
                    }
                }
            }
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
            String iterationText = "Iteration: " + currentStep;
            g2d.drawString(iterationText, getWidth() - 150, getHeight() - 20);
        }
    }
}