package core;

import java.util.ArrayList;
import java.util.List;

public class Flusso {
    private final int id;
    private final double B_i;        // scadenza del flusso, la deadline
    private final double p_z_i;      // tempo di preelaborazione del flusso
    private double q_z_i;  // tempo in attesa in coda all'IPN
    private final double capacita;   // R_i, ovvero le risorse che servono per elaborarlo
    private double T_i;             // ritardo del flusso
    private static double D_i;   // flussi che mancano la deadline B_i
    private NodoSorgente nodoSorgente;



    public Flusso(int id, double scadenza, double tempoPreElaborazione, double capacita, NodoSorgente nodoSorgente) {
        this.id = id;
        this.B_i = scadenza;
        this.p_z_i = tempoPreElaborazione;
        this.q_z_i = 0.0;
        this.capacita = capacita;
        this.T_i = 0.0;
        this.D_i = 0.0;
        this.nodoSorgente = nodoSorgente;
    }

    public Double calcolaT_i(double C_i_d_z) {
        this.T_i = Math.max(0, C_i_d_z - this.B_i);
        return this.T_i;
    }

    public boolean calcolaD_i(double C_i_d_z) {
        // Calcola il ritardo T_i del flusso
        this.calcolaT_i(C_i_d_z);

        // Controlla se il flusso sfora la deadline
        return this.T_i > 0;
    }


    public double calcolaQ_z_i(Flusso flussoCorrente, List<Flusso> flussiAssegnatiAlNodo) {
        double tempoAttesaTotale = 0.0;

        if (flussiAssegnatiAlNodo == null || flussiAssegnatiAlNodo.isEmpty()) {
            this.q_z_i = 0.0;
            return 0.0;
        }

        // Ordina i flussi in base all'ordine di arrivo (assumiamo che l'ordine nella lista
        // rifletta l'ordine di arrivo)
        List<Flusso> flussiInCoda = new ArrayList<>(flussiAssegnatiAlNodo);

        // Trova la posizione del flusso corrente nella coda
        int posizioneFlussoCorrente = flussiInCoda.indexOf(flussoCorrente);

        if (posizioneFlussoCorrente == -1) {
            // Se il flusso non è ancora in coda, lo consideriamo come ultimo
            posizioneFlussoCorrente = flussiInCoda.size();
        }

        // Calcola il tempo di attesa sommando i tempi di preelaborazione di tutti i flussi
        // che precedono il flusso corrente nella coda
        for (int i = 0; i < posizioneFlussoCorrente; i++) {
            Flusso flussoInCoda = flussiInCoda.get(i);
            tempoAttesaTotale += flussoInCoda.getp_z_i();
        }

        // Aggiorna il tempo di attesa in coda per il flusso corrente
        this.q_z_i = tempoAttesaTotale;

        return this.q_z_i;
    }


    // Getters
    public int getId() { return id; }
    public double getB_i() { return B_i; }
    public double getT_i() { return this.T_i; }
    public double getp_z_i() { return p_z_i; }
    public double getCapacita() { return capacita; }
    public NodoSorgente getNodoSorgente() {
        return nodoSorgente;
    }
}
