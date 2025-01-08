package core;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Simulazione {
    private final int numeroIterazioni;
    private final List<NodoIPN> nodiIPN;
    private final List<NodoSorgente> nodiSorgente;
    private IPNInterface ipnInterface;
    private final MatchingGame matchingGame;
    public static Map<Double, Double>  mediaBiPerSimulazione = new HashMap<>(); // Mappa per salvare Media B_i -> QoS Denied

    public Simulazione(List<NodoIPN> nodoIPN, List<NodoSorgente> nodoSorgente, int numeroIterazioni, double scadenzaFLusso) {
        this.numeroIterazioni = numeroIterazioni;
        this.nodiIPN = nodoIPN;
        this.nodiSorgente = nodoSorgente;
        this.matchingGame = new MatchingGame(this.nodiSorgente, this.nodiIPN);
        this.mediaBiPerSimulazione = new HashMap<>();
    }

    public void inizializzaInterfaccia() {
        SwingUtilities.invokeLater(() -> {
            this.ipnInterface = new IPNInterface(matchingGame);
            ipnInterface.setVisible(true);
        });
    }

    public Thread eseguiSimulazione() {
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

                            Thread.sleep(200);

                            SwingUtilities.invokeLater(() -> {
                                ipnInterface.UpdateInterfaceStatus();
                            });
                        }
                    }

                    ipnInterface.updatePreferenceLists();

                    // Calcola l'utilità del sistema
                    matchingGame.calcolaUtilita();
                    System.out.println("--------------------------------------------------------------------------------Utilità del sistema: " + MatchingGame.utilita);

                    matchingGame.aggiornaPreferenceListFlusso_V_i_z();
                    printSystemState();

                    System.out.println("Fine iterazione " + iterazione);
                    ipnInterface.updateLegendPanel(matchingGame);
                    System.out.println("----------------------------------\n");
                }

                double QoSDenied = matchingGame.calcolaPi();
                double SommaB_i = 0;
                for (NodoSorgente sorgente : nodiSorgente) {
                    SommaB_i += sorgente.calcolaB_iMedio();
                }
                double mediaB_i = SommaB_i / nodiSorgente.size();

                synchronized (mediaBiPerSimulazione) {
                    mediaBiPerSimulazione.put(mediaB_i, QoSDenied);
                    printSystemState();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        simulationThread.start();
        return simulationThread;
    }

    private void printSystemState() {

        int currentIteration;
        for (currentIteration = 1; currentIteration <= numeroIterazioni; currentIteration++) {
            System.out.println("Stato del sistema - Iterazione: " + currentIteration);

            // Informazioni sui nodi IPN
            System.out.println("Nodi IPN:");
            for (NodoIPN nodoIPN : nodiIPN) {
                System.out.printf("  - Nodo IPN %d: Capacità residua %.2f, Flussi in coda: %d%n",
                        nodoIPN.getId(), nodoIPN.getL_z(), nodoIPN.getFlussiInCoda().size());
            }
            // Informazioni sui nodi sorgenti
            System.out.println("Nodi sorgente:");
            for (NodoSorgente nodoSorgente : nodiSorgente) {
                double mediaB_i = nodoSorgente.calcolaB_iMedio();
                System.out.printf("  - Nodo sorgente %d: Numero di flussi %d, Media B_i = %.2f%n",
                        nodoSorgente.getId(), nodoSorgente.getFlussi().size(), mediaB_i);
            }
            // Spaziatura per la leggibilità
            System.out.println("-----------------------------------------------");
        }
    }
}
