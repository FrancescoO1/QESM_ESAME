package core;

import javax.swing.*;
import java.awt.*;

public class IPNInterface extends JFrame {
    private MatchingGame matchingGame;
    private JPanel ipnPanel;
    private JTextArea outputArea;

    public IPNInterface(MatchingGame matchingGame) {
        this.matchingGame = matchingGame;
        setTitle("Visualizzazione IPN e Task");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        ipnPanel = new JPanel();
        ipnPanel.setLayout(new GridLayout(matchingGame.getNodiIPN().size(), 1, 10, 10));
        add(ipnPanel, BorderLayout.CENTER);

        outputArea = new JTextArea(10, 30);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.SOUTH);

        JButton btnEseguiMatching = new JButton("Esegui Matching");
        btnEseguiMatching.addActionListener(e -> eseguiMatching());
        add(btnEseguiMatching, BorderLayout.NORTH);

        aggiornaIPNPanel();
        setVisible(true);
    }

    private void aggiornaIPNPanel() {
        ipnPanel.removeAll();
        for (NodoIPN ipn : matchingGame.getNodiIPN()) {
            JPanel panelIPN = new JPanel(new FlowLayout());
            panelIPN.setBorder(BorderFactory.createTitledBorder("IPN " + ipn.getId()));
            JLabel lblCapacita = new JLabel("Capacità: " + ipn.getL_z());
            JLabel lblTempoCoda = new JLabel("Tempo di Coda: " + ipn.getQ_z_i());

            JTextArea assignedFlowsArea = new JTextArea(5, 20);
            assignedFlowsArea.setEditable(false);
            assignedFlowsArea.setText(getAssignedFlowsText(ipn));
            JScrollPane assignedFlowsScrollPane = new JScrollPane(assignedFlowsArea);

            panelIPN.add(lblCapacita);
            panelIPN.add(lblTempoCoda);
            panelIPN.add(new JLabel("Flussi Assegnati:"));
            panelIPN.add(assignedFlowsScrollPane);

            ipnPanel.add(panelIPN);
        }
        ipnPanel.revalidate();
        ipnPanel.repaint();
    }

    private String getAssignedFlowsText(NodoIPN ipn) {
        StringBuilder assignedFlowsText = new StringBuilder();
        for (Flusso flusso : matchingGame.getPreferencelist_di_IPN_z(ipn)) {
            assignedFlowsText.append("Flusso ").append(flusso.getId())
                    .append(" - Ritardo: ").append(flusso.getT_i()).append("\n");
        }
        return assignedFlowsText.toString();
    }

    private void eseguiMatching() {
        matchingGame.eseguiMatching();
        outputArea.append("Risultati del matching:\n");
        for (Flusso flusso : matchingGame.getFlussi()) {
            outputArea.append("Flusso " + flusso.getId() + " completato con ritardo: " + flusso.getT_i() + "\n");
        }
        double utilita = matchingGame.calcolaUtilita();
        outputArea.append("Utilità del sistema: " + utilita + "\n\n");
        aggiornaIPNPanel();
    }
}
