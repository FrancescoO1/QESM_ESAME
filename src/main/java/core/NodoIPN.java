package core;

public class NodoIPN {
    private int id; //nominativo
    private double L_z;  //capicità disponibile del nodo IPN
    private double q_z_i;  //la mia externality, tempo in attesa in coda all'IPN
    private double C_i_d_z; //tempo di completamento

    public NodoIPN(int id, double capacitaDisponibile) {
        this.id = id;
        this.L_z = capacitaDisponibile;
        this.q_z_i = 0.0;
        this.C_i_d_z = 0.0;

    }

    //calcola il tempo di preelaborazione del flusso P_i,d,z = q_z,i + p_z,i nel nodo IPN Z
    public void CalcolaP_i_d_z(Flusso flusso) {
        this.q_z_i += flusso.getp_z_i();
    }

    //calcola il tempo di completamento del flusso solo per l'IPN Z, ovvero calcola C_i,d,z = q_z,i + p_z,i + t_i,z

    public double calcolaC_i_z_d(Flusso flusso, double latenzaRete) {
        return this.C_i_d_z = flusso.getp_z_i() + latenzaRete + this.q_z_i;    //latenzaRete rappresenterebbe t_i,z; q_z,i rappresenterebbe la mia externalities.
    }

    public void decrementaCapacita(double quantita) {
        this.L_z -= quantita;
    }


    // Getter per la GUI
    public int getId() {
        return id;
    }

    public double getL_z() {
        return L_z;
    }

    public double getQ_z_i() {
        return q_z_i;
    }

}
