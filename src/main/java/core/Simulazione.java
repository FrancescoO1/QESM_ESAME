package core;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Simulazione {
    private final int numeroIterazioni;
    private final List<NodoIPN> nodiIPN;
    private final List<NodoSorgente> nodiSorgente;
    private IPNInterface ipnInterface;
    private final MatchingGame matchingGame;
    private ChartManager chartManager; // Modificato: ora usiamo un solo ChartManager
    private final int numeroGrafici;

    public Simulazione(List<NodoIPN> nodoIPN, List<NodoSorgente> nodoSorgente, int numeroIterazioni, int numeroGrafici) {
        this.numeroIterazioni = numeroIterazioni;
        this.nodiIPN = nodoIPN;
        this.nodiSorgente = nodoSorgente;
        this.matchingGame = new MatchingGame(this.nodiSorgente, this.nodiIPN);
        this.numeroGrafici = numeroGrafici;

        // Crea un singolo ChartManager con il numero specificato di grafici
        SwingUtilities.invokeLater(() -> {
            this.chartManager = new ChartManager("Analisi Multi-Grafico", 1); // Inizia con un grafico
            this.chartManager.setVisible(true);

            // Aggiungi gli altri grafici richiesti
            for (int i = 1; i < numeroGrafici; i++) {
                this.chartManager.addNewChart("Grafico " + (i + 1));
            }
        });
    }

    public void inizializzaInterfaccia() {
        SwingUtilities.invokeLater(() -> {
            this.ipnInterface = new IPNInterface(matchingGame);
            ipnInterface.setVisible(true);
        });
    }

    public void eseguiSimulazione() {
        inizializzaInterfaccia();

        Thread simulationThread = new Thread(() -> {
            try {
                for (int iterazione = 1; iterazione <= numeroIterazioni; iterazione++) {
                    System.out.println("Inizio iterazione " + iterazione);
                    final int currentIteration = iterazione;

                    ipnInterface.setCurrentIteration(currentIteration);

                    SwingUtilities.invokeLater(() -> {
                        ipnInterface.UpdateInterfaceStatus();
                    });

                    for (NodoIPN nodoIPN : nodiIPN) {
                        System.out.println("Capacità iniziale Nodo IPN: " + nodoIPN.getId() + " = " + nodoIPN.getL_z());
                    }

                    System.out.println();

                    SwingUtilities.invokeLater(() -> {
                        ipnInterface.updatePreferenceLists();
                    });


                    for (NodoSorgente sorgente : nodiSorgente) {
                        for (Flusso flusso : sorgente.getFlussi()) {
                            NodoIPN nodoAssegnato = matchingGame.algoritmoMatching(flusso);

                            if (nodoAssegnato != null) {
                                System.out.println("Flusso " + flusso.getId() + " del nodo sorgente " + sorgente.getId() + " assegnato a Nodo IPN " + nodoAssegnato.getId());
                                System.out.println("Ritardo del flusso: " + flusso.getT_i());
                            } else {
                                System.out.println("Flusso " + flusso.getId() + " non assegnato a nessun Nodo IPN");
                            }

                            Thread.sleep(500);

                            SwingUtilities.invokeLater(() -> {
                                ipnInterface.UpdateInterfaceStatus();
                            });
                        }
                    }

                    ipnInterface.updatePreferenceLists();

                    // Calcola l'utilità del sistema
                    matchingGame.calcolaUtilita();
                    System.out.println("--------------------------------------------------------------------------------Utilità del sistema: " + MatchingGame.utilita);

                    // Aggiorna tutti i grafici nel ChartManager
                    final int iterazioneFinal = iterazione;
                    SwingUtilities.invokeLater(() -> {
                        // Aggiorna il primo grafico con i dati standard
                        chartManager.updateChart(0, matchingGame, nodiSorgente);

                        // Esempio di come aggiornare gli altri grafici con dati diversi
                        // qua infatti aggiungo gli altri grafici che mi interessa

                        // Puoi personalizzare questa parte in base alle tue esigenze
                        for (int i = 1; i < numeroGrafici; i++) {
                            // Qui puoi aggiungere logica diversa per ogni grafico
                            // Per esempio, potresti voler mostrare metriche diverse per ogni grafico
                            chartManager.updateChart(i, matchingGame, nodiSorgente);
                        }
                    });

                    matchingGame.aggiornaPreferenceListFlusso_V_i_z();
                    printSystemState();

                    System.out.println("Fine iterazione " + iterazione);
                    ipnInterface.updateLegendPanel(matchingGame);
                    System.out.println("----------------------------------\n");
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        simulationThread.start();
    }

    private void printSystemState() {
        System.out.println("\n=== Stato del sistema ===");
        for (NodoIPN nodoIPN : nodiIPN) {
            StringBuilder stato = new StringBuilder()
                    .append("Nodo IPN ").append(nodoIPN.getId())
                    .append(": Capacità residua = ").append(nodoIPN.getL_z())
                    .append("\n  Flussi in coda: ");

            List<Flusso> flussiInCoda = nodoIPN.getFlussiInCoda();
            if (flussiInCoda.isEmpty()) {
                stato.append("nessuno");
            } else {
                for (Flusso flusso : flussiInCoda) {
                    stato.append("\n    - Flusso ").append(flusso.getId())
                            .append(" (tempo pre-elaborazione: ").append(flusso.getp_z_i()).append(")");
                }
            }

            System.out.println(stato.toString());
        }
        System.out.println();
    }

    // Metodi di utilità per gestire i grafici
    public void resetAllGraphs() {
        if (chartManager != null) {
            chartManager.resetAllCharts();
        }
    }

    public void resetGraph(int index) {
        if (chartManager != null) {
            chartManager.resetChart(index);
        }
    }

    public void addNewGraph(String title) {
        if (chartManager != null) {
            chartManager.addNewChart(title);
        }
    }

    public void removeGraph(int index) {
        if (chartManager != null) {
            chartManager.removeChart(index);
        }
    }
}