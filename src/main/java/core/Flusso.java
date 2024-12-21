package core;

public class Flusso {
    private final int id;
    private final double B_i;        // scadenza del flusso, la deadline
    private final double p_z_i;      // tempo di preelaborazione del flusso
    private final double capacita;   // R_i
    private double T_i;             // ritardo del flusso
    private double D_i;             // flussi che mancano la deadline B_i

    public Flusso(int id, double scadenza, double tempoPreElaborazione, double capacita) {
        this.id = id;
        this.B_i = scadenza;
        this.p_z_i = tempoPreElaborazione;
        this.capacita = capacita;
        this.T_i = 0.0;
        this.D_i = 0;
    }

    public void calcolaT_i(double C_i_d_z) {
        this.T_i = Math.max(0, C_i_d_z - this.B_i);
    }

    public void calcolaD_i(Flusso flusso) {
        if (flusso.getT_i() > 0) {
            this.D_i++;
        }
    }

    // Getters
    public int getId() { return id; }
    public double getB_i() { return B_i; }
    public double getp_z_i() { return p_z_i; }
    public double getCapacita() { return capacita; }
    public double getT_i() { return T_i; }
    public double getD_i() { return D_i; }
}
