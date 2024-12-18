package core;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class IPNInterface extends JFrame {
    private MatchingGame matchingGame;
    private JPanel canvasPanel; // Pannello per il disegno
    private JTextArea outputArea;
    private JTextArea preferenceListNodoArea;
    private JTextArea preferenceListFlussoArea;
    private JButton btnNextStep; // Bottone per iterare l'algoritmo passo-passo
    private Map<Flusso, NodoIPN> assegnazioniParziali; // Stato corrente del matching
    private int currentStep; // Contatore dei passi

    public IPNInterface(MatchingGame matchingGame) {
        this.matchingGame = matchingGame;
        this.assegnazioniParziali = new java.util.HashMap<>();
        this.currentStep = 0;

        setTitle("Visualizzazione IPN e Task");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Pannello per il disegno di nodi, flussi e frecce
        canvasPanel = new CanvasPanel();
        canvasPanel.setPreferredSize(new Dimension(800, 600));
        add(canvasPanel, BorderLayout.CENTER);

        // Area di output per i risultati
        outputArea = new JTextArea(10, 30);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.SOUTH);

        // Pannello laterale per mostrare le preference list
        JPanel preferencePanel = new JPanel(new GridLayout(2, 1));
        preferenceListNodoArea = new JTextArea();
        preferenceListNodoArea.setEditable(false);
        preferenceListNodoArea.setBorder(BorderFactory.createTitledBorder("Preference List Nodo"));

        preferenceListFlussoArea = new JTextArea();
        preferenceListFlussoArea.setEditable(false);
        preferenceListFlussoArea.setBorder(BorderFactory.createTitledBorder("Preference List Flusso"));

        preferencePanel.add(new JScrollPane(preferenceListNodoArea));
        preferencePanel.add(new JScrollPane(preferenceListFlussoArea));
        add(preferencePanel, BorderLayout.EAST);

        // Bottone per iterare l'algoritmo passo-passo
        btnNextStep = new JButton("Next Step");
        btnNextStep.addActionListener(e -> eseguiMatchingPasso());
        add(btnNextStep, BorderLayout.NORTH);

        aggiornaPreferenceList();
        setVisible(true);
    }

    private void eseguiMatchingPasso() {
        if (currentStep < matchingGame.getFlussi().size()) {
            Flusso flusso = matchingGame.getFlussi().get(currentStep);
            NodoIPN migliorNodo = matchingGame.eseguiMatchingPasso(flusso);
            assegnazioniParziali.put(flusso, migliorNodo);

            outputArea.append("Flusso " + flusso.getId() + " assegnato a Nodo IPN " + migliorNodo.getId() + "\n");
            outputArea.append("Capacità residua Nodo " + migliorNodo.getId() + ": " + migliorNodo.getL_z() + "\n");
            outputArea.append("Capacità residua Flusso " + flusso.getId() + ": " + flusso.getCapacita() + "\n");
            currentStep++;
            aggiornaPreferenceList();
            canvasPanel.repaint();
        } else {
            outputArea.append("Matching completato!\n");
            btnNextStep.setEnabled(false); // Disabilita il bottone al termine
        }
    }

    private void aggiornaPreferenceList() {
        // Mostra le preference list dei nodi
        StringBuilder nodoText = new StringBuilder();
        for (NodoIPN nodo : matchingGame.getNodiIPN()) {
            nodoText.append("Nodo ").append(nodo.getId()).append(": ");
            for (Flusso flusso : matchingGame.getPreferenceListNodo().get(nodo)) { // Uso corretto del getter
                nodoText.append("Flusso ").append(flusso.getId()).append(" ");
            }
            nodoText.append("\n");
        }
        preferenceListNodoArea.setText(nodoText.toString());

        // Mostra le preference list dei flussi
        StringBuilder flussoText = new StringBuilder();
        for (Flusso flusso : matchingGame.getFlussi()) {
            flussoText.append("Flusso ").append(flusso.getId()).append(": ");
            for (NodoIPN nodo : matchingGame.getPreferenceListFlusso().get(flusso)) {
                flussoText.append("Nodo ").append(nodo.getId()).append(" ");
            }
            flussoText.append("\n");
        }
        preferenceListFlussoArea.setText(flussoText.toString());
    }



    private class CanvasPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Disegna i nodi IPN come cerchi
            int xBase = 200;
            int yBase = 100;
            int circleRadius = 50;
            List<NodoIPN> nodi = matchingGame.getNodiIPN();
            for (int i = 0; i < nodi.size(); i++) {
                int x = xBase + i * 200;
                int y = yBase;
                NodoIPN nodo = nodi.get(i);

                g2d.setColor(Color.BLUE);
                g2d.fillOval(x, y, circleRadius, circleRadius);
                g2d.setColor(Color.BLACK);
                g2d.drawOval(x, y, circleRadius, circleRadius);

                g2d.drawString("Nodo " + nodo.getId(), x + 10, y + circleRadius + 15);
                g2d.drawString("Capacità: " + nodo.getL_z(), x + 10, y + circleRadius + 30);
            }

            // Disegna i flussi come rettangoli
            int flowXBase = 100;
            int flowYBase = 400;
            int rectWidth = 30;
            int rectHeight = 50;
            List<Flusso> flussi = matchingGame.getFlussi();
            for (int i = 0; i < flussi.size(); i++) {
                int x = flowXBase + i * 150;
                int y = flowYBase;
                Flusso flusso = flussi.get(i);

                g2d.setColor(Color.ORANGE);
                g2d.fillRect(x, y, rectWidth, rectHeight);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(x, y, rectWidth, rectHeight);

                g2d.drawString("Flusso " + flusso.getId(), x, y + rectHeight + 15);
                g2d.drawString("Capacità: " + flusso.getCapacita(), x, y + rectHeight + 30);

                // Disegna la freccia se il flusso è assegnato a un nodo
                NodoIPN nodoAssegnato = assegnazioniParziali.get(flusso);
                if (nodoAssegnato != null) {
                    int nodoX = xBase + nodi.indexOf(nodoAssegnato) * 200 + circleRadius / 2;
                    int nodoY = yBase + circleRadius / 2;
                    int flowX = x + rectWidth / 2;
                    int flowY = y;

                    g2d.setColor(Color.RED);
                    g2d.drawLine(flowX, flowY, nodoX, nodoY); // Disegna la linea
                    drawArrowHead(g2d, flowX, flowY, nodoX, nodoY); // Disegna la testa della freccia
                }
            }
        }

        // Metodo per disegnare la testa della freccia
        private void drawArrowHead(Graphics2D g2d, int x1, int y1, int x2, int y2) {
            double angle = Math.atan2(y2 - y1, x2 - x1);
            int arrowHeadSize = 10;

            int xArrow1 = (int) (x2 - arrowHeadSize * Math.cos(angle - Math.PI / 6));
            int yArrow1 = (int) (y2 - arrowHeadSize * Math.sin(angle - Math.PI / 6));
            int xArrow2 = (int) (x2 - arrowHeadSize * Math.cos(angle + Math.PI / 6));
            int yArrow2 = (int) (y2 - arrowHeadSize * Math.sin(angle + Math.PI / 6));

            g2d.drawLine(x2, y2, xArrow1, yArrow1);
            g2d.drawLine(x2, y2, xArrow2, yArrow2);
        }
    }

}
