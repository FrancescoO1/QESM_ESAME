package core;

import java.util.List;

public class Flusso {
    private final int id;
    private final double B_i;        // scadenza del flusso, la deadline
    private final double p_z_i;      // tempo di preelaborazione del flusso
    private double q_z_i;  // tempo in attesa in coda all'IPN
    private final double capacita;   // R_i
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
        this.D_i = 0;
        this.nodoSorgente = nodoSorgente;
    }

    public void calcolaT_i(double C_i_d_z) {
        this.T_i = Math.max(0, C_i_d_z - this.B_i);
    }

    public static double calcolaD_i(Flusso flusso) {
        if (flusso.getT_i() > 0) {
            D_i++;
        }
        return D_i;
    }

    public double calcolaQ_z_i(Flusso flusso, List<Flusso> flussiInCoda) { //TIENE CONTO DEI FLUSSI IN CODA?
        double tempoAttesa = 0.0;

        // Calcola il tempo totale richiesto dai flussi in coda (compreso il flusso corrente)
        for (Flusso f : flussiInCoda) {
            if (f != flusso) { // Esclude il flusso corrente
                tempoAttesa += f.getp_z_i(); // Tempo di preelaborazione del flusso
            }
        }

        // Aggiorna il tempo di attesa del flusso corrente
        flusso.q_z_i = tempoAttesa;

        return flusso.q_z_i;
    }


    // Getters
    public int getId() { return id; }
    public double getB_i() { return B_i; }
    public double getp_z_i() { return p_z_i; }
    public double getCapacita() { return capacita; }
    public double getT_i() { return T_i; }
    public double getD_i() { return D_i; }
    public NodoSorgente getNodoSorgente() {
        return nodoSorgente;
    }
}
