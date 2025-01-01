package core;

import java.util.ArrayList;
import java.util.List;

public class NodoIPN {
    private final int id;
    private double L_z;    // capacità disponibile del nodo IPN
    private double C_i_d_z;// tempo di completamento
    private double P_i_d_z;// tempo di preelaborazione totale nel nodo IPN
    private double capacitaOriginale;
    private List<Flusso> flussiInCoda; // Lista dei flussi assegnati al nodo

    public NodoIPN(int id, double capacitaDisponibile) {
        this.id = id;
        this.L_z = capacitaDisponibile;
        this.C_i_d_z = 0.0;
        this.P_i_d_z = 0.0;
        this.capacitaOriginale = capacitaDisponibile;
        this.flussiInCoda = new ArrayList<>();
    }

    public double calcolaC_i_z_d(Flusso flusso, double latenzaRete) {
        return this.C_i_d_z = CalcolaP_i_d_z(flusso) + latenzaRete;
    }

    public double CalcolaP_i_d_z(Flusso flusso) {
        double tempoAttesaInCoda = flusso.calcolaQ_z_i(flusso, flussiInCoda);
        return this.P_i_d_z = tempoAttesaInCoda + flusso.getp_z_i();
    }

    public boolean haCapacitaSufficiente(Flusso flusso) {
        return this.L_z >= flusso.getCapacita();
    }

    public void decrementaCapacita(double quantita) {
        if (this.L_z < quantita) {
            throw new IllegalStateException("Capacità insufficiente nel nodo IPN " + id);
        }
        this.L_z -= quantita;
    }

    // Metodo per aggiungere un flusso alla coda
    public void aggiungiFlussoInCoda(Flusso flusso) {
        if (flusso != null && !flussiInCoda.contains(flusso)) {
            flussiInCoda.add(flusso);
        }
    }

    // Metodo per rimuovere un flusso dalla coda
    public void rimuoviFlussoInCoda(Flusso flusso) {
        flussiInCoda.remove(flusso);
    }

    // Metodo per ottenere la lista dei flussi in coda
    public List<Flusso> getFlussiInCoda() {
        return new ArrayList<>(flussiInCoda); // Ritorna una copia per evitare modifiche esterne
    }

    // Getters
    public int getId() { return id; }
    public double getL_z() { return L_z; }
}
