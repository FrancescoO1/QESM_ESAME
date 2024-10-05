package core;

import java.util.List;

public class MatchingGame {
    List<Flusso> flussi;
    List<NodoIPN> nodiIPN;

    public MatchingGame(List<Flusso> flussi, List<NodoIPN> nodiIPN) {
        this.flussi = flussi;
        this.nodiIPN = nodiIPN;
    }

    // Algoritmo di Gale-Shapley modificato per assegnare i flussi agli IPN
    public void eseguiMatching() {
        for (Flusso flusso : flussi) {
            NodoIPN migliorNodo = null;
            double migliorTempoCompletamento = Double.MAX_VALUE;

            // Cicla su ogni IPN e calcola il tempo di completamento
            for (NodoIPN nodo : nodiIPN) {
                double latenzaRete = calcolaLatenzaRete(flusso, nodo);  // Calcola la latenza di rete
                double tempoCompletamento = nodo.calcolaTempoElaborazione(flusso, latenzaRete);

                if (tempoCompletamento < migliorTempoCompletamento) {
                    migliorTempoCompletamento = tempoCompletamento;
                    migliorNodo = nodo;
                }
            }

            // Assegna il flusso al miglior nodo IPN
            if (migliorNodo != null) {
                migliorNodo.assegnaFlusso(flusso);
                flusso.calcolaTempoCompletamento(calcolaLatenzaRete(flusso, migliorNodo));
                flusso.calcolaRitardo();
                System.out.println("Flusso " + flusso.id + " assegnato a Nodo IPN " + migliorNodo.id);
            }
        }
    }

    // Funzione per calcolare la latenza di rete (ri,d,z)
    private double calcolaLatenzaRete(Flusso flusso, NodoIPN nodo) {
        // Implementazione semplice per latenza di rete tra flusso e IPN
        return Math.random() * 0.5;  // Generazione casuale della latenza di rete per il test
    }

    // Funzione per calcolare l'utilità del sistema
    public double calcolaUtilita() {
        double utilitaTotale = 0.0;
        for (Flusso flusso : flussi) {
            utilitaTotale += (flusso.tempoCompletamento <= flusso.scadenza) ? 1 : 0;
        }
        return utilitaTotale / flussi.size();  // Restituisce la percentuale di flussi soddisfatti
    }
}
