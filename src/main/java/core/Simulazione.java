package core;

import javax.swing.*;
import java.util.List;

public class Simulazione {
    private final int numeroIterazioni;
    private final List<NodoIPN> nodiIPN;
    private final List<NodoSorgente> nodiSorgente;

    public Simulazione(List<NodoIPN> nodoIPN, List<NodoSorgente> nodoSorgente, int numeroIterazioni) {
        this.numeroIterazioni = numeroIterazioni;
        this.nodiIPN = nodoIPN;
        this.nodiSorgente = nodoSorgente;
    }

    public void eseguiSimulazione() {
        MatchingGame matchingGame = new MatchingGame(this.nodiSorgente, this.nodiIPN);
        for (int iterazione = 1; iterazione <= numeroIterazioni; iterazione++) {
            System.out.println("Inizio iterazione " + iterazione);

            for (NodoIPN nodoIPN : nodiIPN) {
                System.out.println("Capacità iniziale Nodo IPN: " + nodoIPN.getId() + " = " + nodoIPN.getL_z());
            }

            System.out.println();

            for (NodoSorgente sorgente : nodiSorgente) {
                for (Flusso flusso : sorgente.getFlussi()) {
                    // Usa il metodo eseguiMatchingPasso per ogni flusso
                    NodoIPN nodoAssegnato = matchingGame.algoritmoMatching(flusso);

                    if (nodoAssegnato != null) {
                        System.out.println("Flusso " + flusso.getId() + " assegnato a Nodo IPN " + nodoAssegnato.getId());
                    } else {
                        System.out.println("Flusso " + flusso.getId() + " non assegnato a nessun Nodo IPN");
                    }
                }
            }


            // Aggiorna la preference list dei flussi per la prossima iterazione
            matchingGame.aggiornaPreferenceListFlusso_V_i_z();

            printSystemState();

            System.out.println("Fine iterazione " + iterazione);
            System.out.println("----------------------------------\n");

        }

        SwingUtilities.invokeLater(() -> new IPNInterface(matchingGame));
    }

    // Log del sistema
    private void printSystemState() {
        System.out.println("\n=== Stato del sistema ===");
        for (NodoIPN nodoIPN : nodiIPN) {
            System.out.println("Nodo IPN " + nodoIPN.getId() + ": Capacità residua = " + nodoIPN.getL_z() + " Tempo di attesa in coda al nodo = " + nodoIPN.getQ_z_i());    //TODO: aggiungi contatore dei flussi associati an nodo IPN
        }
        System.out.println();
    }



}
