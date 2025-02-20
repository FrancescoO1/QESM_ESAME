package Core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Flusso {
    public int R_i;         // Peso del flusso
    public double q_z_i;    // Tempo di attesa in coda (waiting time)
    public final double p_z_i; // Tempo di servizio (costante per il flusso)
    public final int B_i;      // Deadline (scadenza, costante per il flusso)
    public List<NodoIPN> listaPreferenzaNodi; // Lista di preferenza dei nodi IPN
    private final int idUtente;           // Identificativo dell'utente proprietario
    private final int idFlusso;            // Identificativo del flusso
    private Map<NodoIPN, Double> q_z_i_perNodo; //mappa per tenere traccia dei tempi di attesa specifici per flusso


    public Flusso(int R_i, int q_z_i, int p_z_i, int B_i, int idUtente, int idFlusso) {
        this.R_i = R_i;
        this.q_z_i = q_z_i;
        this.p_z_i = p_z_i;
        this.B_i = B_i;
        this.listaPreferenzaNodi = new ArrayList<>();
        this.idUtente = idUtente;
        this.idFlusso = idFlusso;
        this.q_z_i_perNodo = new HashMap<>();
    }

    public int getIdUtente() {
        return idUtente;
    }

    public int getIdFlusso() {
        return idFlusso;
    }

    public double getQ_z_i(NodoIPN nodo) {
        if (nodo == null) return q_z_i;
        return q_z_i_perNodo.getOrDefault(nodo, q_z_i);
    }

    public void setQ_z_i(NodoIPN nodo, double val) {
        q_z_i_perNodo.put(nodo, val);
    }

}






