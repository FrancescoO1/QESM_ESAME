package core;

public class NodoIPN {
    private final int id;
    private double L_z;    // capacità disponibile del nodo IPN
    private double C_i_d_z;// tempo di completamento
    private double P_i_d_z;// tempo di preelaborazione totale nel nodo IPN
    private double capacitaOriginale;

    public NodoIPN(int id, double capacitaDisponibile) {
        this.id = id;
        this.L_z = capacitaDisponibile;
        this.C_i_d_z = 0.0;
        this.P_i_d_z = 0.0;
        this.capacitaOriginale = capacitaDisponibile;
    }

    public double calcolaC_i_z_d(Flusso flusso, double latenzaRete) { //DA RIFARE
        return this.C_i_d_z =+ latenzaRete;
    }

    public double CalcolaP_i_d_z(Flusso flusso) { // DA RIFARE
        return this.P_i_d_z = flusso.calcolaQ_z_i(flusso, ) + flusso.getp_z_i();
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

    // Getters
    public int getId() { return id; }
    public double getL_z() { return L_z; }
}
