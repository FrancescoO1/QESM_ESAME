package Core;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class NodoIPN {
    private int capacitaLz;
    public final Queue<Flusso> codaFlussi;
    public final double latenzaRete = 0.5;
    private final int idNodo;
    private static final AtomicInteger idCounter = new AtomicInteger(0);
    public List<Flusso> listaPreferenzeFlussi;
    public double tempoCompletamentoFlussoInElaborazione;  //  per memorizzare il tempo di completamento (C_i_d_z)
    private double ultimoTempoCoda;
    public boolean elaboratoInRitardo;
    public boolean disponibile; // indica se il nodo ha ancora capacità per elaborare flussi


    public NodoIPN(int capacitaLz) {
        this.capacitaLz = capacitaLz;
        this.codaFlussi = new LinkedList<>();
        this.idNodo = idCounter.incrementAndGet();
        this.listaPreferenzeFlussi = new ArrayList<>();
        this.tempoCompletamentoFlussoInElaborazione = -1; // Il valore -1 indica che il flusso non è ancora stato processato.
        this.ultimoTempoCoda = 0.0;
        this.elaboratoInRitardo = false;
        this.disponibile = true;
    }

    @Override
    public String toString() {
        return "NodoIPN-" + idNodo;
    }

    public void aggiungiFlusso(Flusso flusso) {
        if (disponibile && capacitaLz >= flusso.R_i && codaFlussi.size() < 3) { // controllo solo sulla capacità residua e la coda al nodo IPN
            codaFlussi.add(flusso);
        }
    }

    public double calcolaTempoCompletamento(Flusso flusso) {
        // Usa il tempo di attesa specifico per questo nodo
        return flusso.getQ_z_i(this) + flusso.p_z_i + latenzaRete;  //tempo attesa + q_z_i = tempo di preelaborazione
    }

    public double calcolaRitardo(Flusso flusso) {
        double C_i_d_z = calcolaTempoCompletamento(flusso);
        return Math.max(0, C_i_d_z - flusso.B_i);
    }

    public void resetDisponibilita() {
        // Un nodo è disponibile se ha una capacità residua > 0
        this.disponibile = (this.capacitaLz > 0);
    }

    public void elaboraFlusso(Flusso flussoInElaborazione, List<Utente> utenti) {
        if (flussoInElaborazione != null && codaFlussi.contains(flussoInElaborazione)) {
            // Verifica se c'è capacità sufficiente per elaborare il flusso
            if (capacitaLz >= flussoInElaborazione.R_i) {
                codaFlussi.remove(flussoInElaborazione);
                capacitaLz -= flussoInElaborazione.R_i;

                // Non settiamo più disponibile = false permanentemente
                // Ma valutiamo se il nodo può elaborare altri flussi in questa iterazione
                if (capacitaLz > 0) {
                    // Trova il flusso con il peso minimo tra quelli in coda
                    int pesoMinimoFlussoInCoda = Integer.MAX_VALUE;
                    for (Flusso f : codaFlussi) {
                        if (f.R_i < pesoMinimoFlussoInCoda) {
                            pesoMinimoFlussoInCoda = f.R_i;
                        }
                    }

                    // Se ci sono flussi in coda e la capacità residua è minore del peso minimo
                    // il nodo non è disponibile solo per questa iterazione
                    this.disponibile = codaFlussi.isEmpty() || capacitaLz >= pesoMinimoFlussoInCoda;
                } else {
                    this.disponibile = false;
                }

                // Rimuove il flusso dall'utente
                for (Utente u : utenti) {
                    if (u.getFlussi().contains(flussoInElaborazione)) {
                        u.getFlussi().remove(flussoInElaborazione);
                        break;
                    }
                }
            } else {
                // Se non c'è capacità sufficiente, il nodo non è disponibile solo per questa iterazione
                this.disponibile = false;
            }
        }
    }

    public double getUltimoTempoCoda() {
        return ultimoTempoCoda;
    }

    public int getCapacitaLz() {
        return capacitaLz;
    }

    public void setUltimoTempoCoda(double tempo) {
        this.ultimoTempoCoda = tempo;
    }
}