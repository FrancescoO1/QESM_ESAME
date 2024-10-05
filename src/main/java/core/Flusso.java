package core;

public class Flusso {
    int id;
    double scadenza;  // Deadline del flusso (beta_i)
    double tempoElaborazione;  // Tempo di elaborazione sul nodo IPN (p_z,i)
    double tempoCompletamento;  // Tempo di completamento (C_i,d,z)
    double ritardo;  // Ritardo (T_i)

    public Flusso(int id, double scadenza, double tempoElaborazione) {
        this.id = id;
        this.scadenza = scadenza;
        this.tempoElaborazione = tempoElaborazione;
        this.tempoCompletamento = 0.0;
        this.ritardo = 0.0;
    }

    // Calcola il tempo di completamento totale per il flusso
    public void calcolaTempoCompletamento(double latenzaRete) {
        this.tempoCompletamento = this.tempoElaborazione + latenzaRete;
    }

    // Calcola il ritardo (T_i)
    public void calcolaRitardo() {
        this.ritardo = Math.max(0, this.tempoCompletamento - this.scadenza);
    }
}
