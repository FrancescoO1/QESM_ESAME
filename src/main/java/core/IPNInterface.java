package core;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class IPNInterface extends JFrame {
    private static final int DEFAULT_CANVAS_WIDTH = 1200;
    private static final int DEFAULT_CANVAS_HEIGHT = 800;
    private static final int MIN_SPACING = 120;

    private final MatchingGame matchingGame;
    private final JPanel canvasPanel;
    private final JTextArea outputArea;
    private final JTextArea preferenceListNodoArea;
    private final JTextArea preferenceListFlussoArea;
    private final JButton btnNextStep;
    private final Map<Flusso, NodoIPN> assegnazioniParziali;
    private final Map<NodoIPN, Point> nodoPositions;
    private final Map<Flusso, Point> flussoPositions;
    private final Map<NodoSorgente, Point> sorgentePositions;

    private int currentStep;
    private int canvasWidth;
    private int canvasHeight;

    public IPNInterface(MatchingGame matchingGame) {
        this.matchingGame = matchingGame;
        this.assegnazioniParziali = new HashMap<>();
        this.nodoPositions = new HashMap<>();
        this.flussoPositions = new HashMap<>();
        this.sorgentePositions = new HashMap<>();
        this.currentStep = 0;

        // Calcola dimensioni canvas basate sul numero di elementi
        initializeCanvasSize();

        // Setup della finestra
        setupWindow();

        // Inizializza componenti UI
        this.canvasPanel = createCanvasPanel();
        this.outputArea = createOutputArea();
        this.preferenceListNodoArea = createPreferenceListArea("Preference List Nodo");
        this.preferenceListFlussoArea = createPreferenceListArea("Preference List Flusso");
        this.btnNextStep = createNextStepButton();

        // Layout
        layoutComponents();

        // Inizializzazione finale
        generaPosizioniCasuali();
        aggiornaPreferenceList();
        setVisible(true);
    }

    private void initializeCanvasSize() {
        int totalElements = matchingGame.getNodiIPN().size() +
                matchingGame.getFlussi().size() +
                matchingGame.getNodiSorgenti().size();
        canvasWidth = Math.max(1500, totalElements * 150); // Aumenta la larghezza per maggiore spazio
        canvasHeight = Math.max(900, totalElements * 100); // Aumenta l’altezza per maggiore spazio
    }

    private void setupWindow() {
        setTitle("Visualizzazione IPN, Sorgenti e Task");
        setSize(canvasWidth + 500, canvasHeight + 100); // Aggiungi margini extra per padding
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    private JPanel createCanvasPanel() {
        JPanel panel = new CanvasPanel();
        panel.setPreferredSize(new Dimension(canvasWidth, canvasHeight));
        return panel;
    }

    private JTextArea createOutputArea() {
        JTextArea area = new JTextArea(10, 50);
        area.setEditable(false);
        return area;
    }

    private JTextArea createPreferenceListArea(String title) {
        JTextArea area = new JTextArea(15, 30);
        area.setEditable(false);
        area.setBorder(BorderFactory.createTitledBorder(title));
        return area;
    }

    private JButton createNextStepButton() {
        JButton button = new JButton("Next Step");
        button.addActionListener(e -> eseguiMatchingPasso());
        return button;
    }

    private void layoutComponents() {
        add(canvasPanel, BorderLayout.CENTER);
        add(new JScrollPane(outputArea), BorderLayout.SOUTH);

        JPanel preferencePanel = new JPanel(new GridLayout(2, 1));
        preferencePanel.add(new JScrollPane(preferenceListNodoArea));
        preferencePanel.add(new JScrollPane(preferenceListFlussoArea));
        add(preferencePanel, BorderLayout.EAST);

        add(btnNextStep, BorderLayout.NORTH);
    }

    private void eseguiMatchingPasso() {
        if (currentStep < matchingGame.getFlussi().size()) {
            Flusso flusso = matchingGame.getFlussi().get(currentStep);
            NodoIPN migliorNodo = matchingGame.algoritmoMatching(flusso);

            if (migliorNodo != null) {
                assegnazioniParziali.put(flusso, migliorNodo);
                updateOutput(flusso, migliorNodo);
                currentStep++;
                aggiornaPreferenceList();
                canvasPanel.repaint();
            }
        } else {
            completaMatching();
        }
    }

    private void updateOutput(Flusso flusso, NodoIPN nodo) {
        StringBuilder output = new StringBuilder()
                .append("Flusso ").append(flusso.getId())
                .append(" assegnato a Nodo IPN ").append(nodo.getId()).append("\n")
                .append("Capacità residua Nodo ").append(nodo.getId())
                .append(": ").append(nodo.getL_z()).append("\n")
                .append("Capacità residua Flusso ").append(flusso.getId())
                .append(": ").append(flusso.getCapacita()).append("\n");

        outputArea.append(output.toString());
    }

    private void completaMatching() {
        outputArea.append("Matching completato!\n");
        btnNextStep.setEnabled(false);
    }

    private void aggiornaPreferenceList() {
        preferenceListNodoArea.setText(buildPreferenceListNodoText());
        preferenceListFlussoArea.setText(buildPreferenceListFlussoText());
    }

    private String buildPreferenceListNodoText() {
        StringBuilder text = new StringBuilder();
        for (NodoIPN nodo : matchingGame.getNodiIPN()) {
            text.append("Nodo ").append(nodo.getId()).append(": ");
            java.util.List<Flusso> preferenze = matchingGame.getPreferenceListNodo().getOrDefault(nodo, new java.util.ArrayList<>());
            if (preferenze != null && !preferenze.isEmpty()) {
                for (Flusso flusso : preferenze) {
                    text.append("Flusso ").append(flusso.getId()).append(" ");
                }
            } else {
                text.append("Nessuna preferenza");
            }
            text.append("\n");
        }
        return text.toString();
    }

    private String buildPreferenceListFlussoText() {
        StringBuilder text = new StringBuilder();
        for (Flusso flusso : matchingGame.getFlussi()) {
            text.append("Flusso ").append(flusso.getId()).append(": ");
            java.util.List<NodoIPN> preferenze = matchingGame.getPreferenceListFlusso().getOrDefault(flusso, new java.util.ArrayList<>());
            if (preferenze != null && !preferenze.isEmpty()) {
                for (NodoIPN nodo : preferenze) {
                    text.append("Nodo ").append(nodo.getId()).append(" ");
                }
            } else {
                text.append("Nessuna preferenza");
            }
            text.append("\n");
        }
        return text.toString();
    }


    private void generaPosizioniCasuali() {
        Random random = new Random();
        int nodeSize = 50; // Dimensione del nodo (diametro per i cerchi)
        int padding = 20;  // Spaziatura minima rispetto ai bordi del canvas
        int minDistance = 100; // Distanza minima tra i nodi per evitare sovrapposizioni

        // Dimensioni massime per i nodi all'interno del canvas
        int maxWidth = canvasWidth - nodeSize - padding;
        int maxHeight = canvasHeight - nodeSize - padding;

        // Genera posizioni casuali per i nodi sorgente
        for (NodoSorgente sorgente : matchingGame.getNodiSorgenti()) {
            Point pos;
            do {
                int x = random.nextInt(maxWidth - padding) + padding;
                int y = random.nextInt(maxHeight / 2 - padding) + padding; // Parte alta per i nodi sorgente
                pos = new Point(x, y);
            } while (isOverlapping(pos, sorgentePositions.values(), minDistance) ||
                    isOverlapping(pos, nodoPositions.values(), minDistance));
            sorgentePositions.put(sorgente, pos);
        }

        // Genera posizioni casuali per i nodi IPN
        for (NodoIPN nodo : matchingGame.getNodiIPN()) {
            Point pos;
            do {
                int x = random.nextInt(maxWidth - padding) + padding;
                int y = random.nextInt(maxHeight / 2 - padding) + maxHeight / 2; // Parte bassa per i nodi IPN
                pos = new Point(x, y);
            } while (isOverlapping(pos, nodoPositions.values(), minDistance) ||
                    isOverlapping(pos, sorgentePositions.values(), minDistance));
            nodoPositions.put(nodo, pos);
        }
    }

    // Metodo per verificare se una posizione è troppo vicina ad altre
    private boolean isOverlapping(Point pos, Iterable<Point> existingPositions, int minDistance) {
        for (Point existing : existingPositions) {
            if (pos.distance(existing) < minDistance) {
                return true;
            }
        }
        return false;
    }


    private <T> void generatePosition(T element, Random random, Map<T, Point> positions) {
        Point pos;
        do {
            int x = random.nextInt(canvasWidth - 100) + 50;
            int y = random.nextInt(canvasHeight - 100) + 50;
            pos = new Point(x, y);
        } while (isOverlapping(pos, positions.values()));
        positions.put(element, pos);
    }

    private boolean isOverlapping(Point pos, Collection<Point> existingPositions) {
        return existingPositions.stream()
                .anyMatch(existing -> pos.distance(existing) < MIN_SPACING);
    }

    private class CanvasPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // Disegna i nodi sorgente e le loro tabelle
            disegnaNodoSorgenti(g2d);

            // Disegna i nodi IPN
            disegnaNodiIPN(g2d);

            // Disegna le connessioni
            disegnaConnessioni(g2d);
        }

        private void disegnaNodoSorgenti(Graphics2D g2d) {
            for (NodoSorgente sorgente : matchingGame.getNodiSorgenti()) {
                Point pos = sorgentePositions.get(sorgente);
                if (pos != null) {
                    // Disegna il nodo sorgente in viola
                    g2d.setColor(new Color(128, 0, 128)); // Viola
                    g2d.fillOval(pos.x, pos.y, 50, 50);
                    g2d.setColor(Color.BLACK);
                    g2d.drawOval(pos.x, pos.y, 50, 50);
                    g2d.drawString("Nodo Sorgente " + sorgente.getId(),
                            pos.x + 10, pos.y + 65);

                    // Disegna la tabella dei flussi associati
                    int tableX = pos.x + 120;
                    int tableY = pos.y;
                    int rowHeight = 20;
                    int columnWidth = 80;

                    g2d.setColor(Color.WHITE);
                    g2d.fillRect(tableX, tableY, columnWidth, rowHeight * sorgente.getFlussi().size());
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(tableX, tableY, columnWidth, rowHeight * sorgente.getFlussi().size());

                    int currentY = tableY;
                    for (Flusso flusso : sorgente.getFlussi()) {
                        currentY += rowHeight;
                        g2d.drawString("Flusso " + flusso.getId(), tableX + 5, currentY - 5);
                    }
                }
            }
        }


        private void disegnaNodiIPN(Graphics2D g2d) {
            for (NodoIPN nodo : matchingGame.getNodiIPN()) {
                Point pos = nodoPositions.get(nodo);
                if (pos != null) {
                    // Disegna il nodo IPN in blu
                    g2d.setColor(Color.BLUE);
                    g2d.fillOval(pos.x, pos.y, 50, 50);
                    g2d.setColor(Color.BLACK);
                    g2d.drawOval(pos.x, pos.y, 50, 50);
                    g2d.drawString("Nodo IPN " + nodo.getId(),
                            pos.x + 10, pos.y + 65);
                    g2d.drawString("Capacità: " + nodo.getL_z(),
                            pos.x + 10, pos.y + 80);
                }
            }
        }

        private void disegnaConnessioni(Graphics2D g2d) {
            for (NodoSorgente sorgente : matchingGame.getNodiSorgenti()) {
                Point posS = sorgentePositions.get(sorgente);

                if (posS != null) {
                    // Itera sugli IPN nella preference list dei flussi associati al nodo sorgente
                    for (Flusso flusso : sorgente.getFlussi()) {
                        NodoIPN nodoAssegnato = matchingGame.getAssegnazioneParziale(flusso);

                        if (nodoAssegnato != null) {
                            Point posN = nodoPositions.get(nodoAssegnato);
                            if (posN != null) {
                                // Disegna una freccia rossa dal nodo sorgente al nodo IPN
                                g2d.setColor(Color.RED);
                                drawArrow(g2d, posS.x + 25, posS.y + 25, posN.x + 25, posN.y + 25);
                            }
                        }
                    }
                }
            }
        }

        private void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2) {
            int arrowSize = 10;
            double angle = Math.atan2(y2 - y1, x2 - x1);

            g2d.drawLine(x1, y1, x2, y2);
            int arrowX1 = (int) (x2 - arrowSize * Math.cos(angle - Math.PI / 6));
            int arrowY1 = (int) (y2 - arrowSize * Math.sin(angle - Math.PI / 6));
            int arrowX2 = (int) (x2 - arrowSize * Math.cos(angle + Math.PI / 6));
            int arrowY2 = (int) (y2 - arrowSize * Math.sin(angle + Math.PI / 6));

            g2d.drawLine(x2, y2, arrowX1, arrowY1);
            g2d.drawLine(x2, y2, arrowX2, arrowY2);
        }
    }

}