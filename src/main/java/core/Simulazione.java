package core;

import javax.swing.*;
import java.util.List;

public class Simulazione {
    private final int numeroIterazioni;
    private final List<NodoIPN> nodiIPN;
    private final List<NodoSorgente> nodiSorgente;
    private IPNInterface ipnInterface;
    private final MatchingGame matchingGame;

    public Simulazione(List<NodoIPN> nodoIPN, List<NodoSorgente> nodoSorgente, int numeroIterazioni) {
        this.numeroIterazioni = numeroIterazioni;
        this.nodiIPN = nodoIPN;
        this.nodiSorgente = nodoSorgente;
        this.matchingGame = new MatchingGame(this.nodiSorgente, this.nodiIPN);
    }

    public void inizializzaInterfaccia() {
        // Esegui l'inizializzazione dell'interfaccia nel thread di EDT
        SwingUtilities.invokeLater(() -> {
            this.ipnInterface = new IPNInterface(matchingGame);
            ipnInterface.setVisible(true);
        });
    }

    public void eseguiSimulazione() {
        // Inizializza l'interfaccia prima di iniziare la simulazione
        inizializzaInterfaccia();

        // Crea un thread separato per la simulazione
        Thread simulationThread = new Thread(() -> {
            try {
                // Attendi un momento per assicurarsi che l'interfaccia sia inizializzata
               // Thread.sleep(1000);

                for (int iterazione = 1; iterazione <= numeroIterazioni; iterazione++) {
                    System.out.println("Inizio iterazione " + iterazione);
                    final int currentIteration = iterazione;

                    ipnInterface.setCurrentIteration(currentIteration);

                    // Aggiorna l'interfaccia nel thread EDT
                    SwingUtilities.invokeLater(() -> {
                        ipnInterface.UpdateInterfaceStatus();
                    });

                    for (NodoIPN nodoIPN : nodiIPN) {
                        System.out.println("Capacità iniziale Nodo IPN: " + nodoIPN.getId() + " = " + nodoIPN.getL_z());
                    }

                    System.out.println();

                    for (NodoSorgente sorgente : nodiSorgente) {
                        for (Flusso flusso : sorgente.getFlussi()) {
                            NodoIPN nodoAssegnato = matchingGame.algoritmoMatching(flusso);

                            if (nodoAssegnato != null) {
                                System.out.println("Flusso " + flusso.getId() + " assegnato a Nodo IPN " + nodoAssegnato.getId());
                            } else {
                                System.out.println("Flusso " + flusso.getId() + " non assegnato a nessun Nodo IPN");
                            }

                            // Pausa breve per permettere di visualizzare l'animazione
                            Thread.sleep(500);

                            // Aggiorna l'interfaccia dopo ogni assegnazione
                            SwingUtilities.invokeLater(() -> {
                                ipnInterface.UpdateInterfaceStatus();
                            });
                        }
                    }

                    matchingGame.aggiornaPreferenceListFlusso_V_i_z();
                    printSystemState();

                    System.out.println("Fine iterazione " + iterazione);
                    System.out.println("----------------------------------\n");
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Avvia il thread della simulazione
        simulationThread.start();
    }

    private void printSystemState() {
        System.out.println("\n=== Stato del sistema ===");
        for (NodoIPN nodoIPN : nodiIPN) {
            System.out.println("Nodo IPN " + nodoIPN.getId() +
                    ": Capacità residua = " + nodoIPN.getL_z() +
                    " Tempo di attesa in coda al nodo = " + nodoIPN.getQ_z_i());
        }
        System.out.println();
    }
}