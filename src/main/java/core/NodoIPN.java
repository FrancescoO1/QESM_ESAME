package core;

public class NodoIPN {
    private final int id;
    private double L_z;    // capacità disponibile del nodo IPN
    private double q_z_i;  // tempo in attesa in coda all'IPN
    private double C_i_d_z;// tempo di completamento
    private double capacitaOriginale;

    public NodoIPN(int id, double capacitaDisponibile) {
        this.id = id;
        this.L_z = capacitaDisponibile;
        this.q_z_i = 0.0;
        this.C_i_d_z = 0.0;
        this.capacitaOriginale = capacitaDisponibile;
    }

    public double calcolaC_i_z_d(Flusso flusso, double latenzaRete) {
        return this.C_i_d_z = flusso.getp_z_i() + latenzaRete + this.q_z_i;
    }

    public void CalcolaP_i_d_z(Flusso flusso) {
        this.q_z_i += flusso.getp_z_i();
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

    public void updateStatus(int currentStep) {
        // Reset dei valori se siamo al primo step
        if (currentStep == 0) {
            this.L_z = this.capacitaOriginale;
            this.q_z_i = 0.0;
            this.C_i_d_z = 0.0;
            return;
        }
    }

    // Getters
    public int getId() { return id; }
    public double getL_z() { return L_z; }
    public double getQ_z_i() { return q_z_i; }
}
