package core;

public class NodoIPN {
    int id;
    double capacitaDisponibile;  // Capacità disponibile (L_z)
    double tempoCoda;  // Tempo di coda nel nodo (q_z,i)

    public NodoIPN(int id, double capacitaDisponibile) {
        this.id = id;
        this.capacitaDisponibile = capacitaDisponibile;
        this.tempoCoda = 0.0;
    }

    // Assegna un flusso e aggiorna il tempo di coda
    public void assegnaFlusso(Flusso flusso) {
        this.tempoCoda += flusso.tempoElaborazione;
    }

    // Calcola il tempo totale di elaborazione per un flusso (con latenza)
    public double calcolaTempoElaborazione(Flusso flusso, double latenzaRete) {
        return flusso.tempoElaborazione + latenzaRete + this.tempoCoda;
    }
}
