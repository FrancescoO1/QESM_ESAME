package core;

public class Flusso {
    private int id; //nominativo del flusso
    private double B_i; //scadenza del flusso, la deadline
    private double p_z_i; //tempo di preelaborazione del flusso
    private double T_i; //ritardo del flusso
    private double D_i; //flussi che mancano la deadline B_i, che negano così la quality of service

    public Flusso(int id, double scadenza, double tempoPreElaborazione) {
        this.id = id;
        this.B_i = scadenza;
        this.p_z_i = tempoPreElaborazione;
        this.T_i = 0.0;
        this.D_i = 0;
    }


    public void calcolaT_i(double C_i_d_z) {
        this.T_i = Math.max(0, C_i_d_z  - this.B_i);
    }  //calcola il ritardo del flusso, ovvero il task i, T_i è sempre zero se il tempo di completamento è minore o uguale alla scadenza B_i, capisco che rispetto la scadenza.


    //calcolo D_i ovvero il numero dei flussi che sforano la deadline B_i
    public void calcolaD_i(Flusso flusso) {
        if (flusso.getT_i() > 0) { //ovvero se T_i è maggiore di zero.
            this.D_i++;
        }
    }

    // Getter per la GUI
    public int getId() {
        return id;
    }

    public double getT_i() {
        return T_i;
    }

    public double getB_i() {
        return B_i;
    }

    public double getp_z_i() {
        return p_z_i;
    }

    public double getD_i() {
        return D_i;
    }
}
