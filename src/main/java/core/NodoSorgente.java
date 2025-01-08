package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NodoSorgente {
    private final int id;
    private final List<Flusso> flussi;
    private int currentFlussoIndex = 0;

    public NodoSorgente(int id) {
        this.id = id;
        this.flussi = new ArrayList<>();
    }

    public void aggiungiFlusso(Flusso flusso) {
        if (flusso != null) {
            this.flussi.add(flusso);
        }
    }

    // Restituisce una copia immodificabile della lista per sicurezza
    public List<Flusso> getFlussi() {
        return Collections.unmodifiableList(flussi);
    }

    public void updateFlowStatus(int currentStep) {
        // Aggiorna l'indice del flusso corrente
        if (currentStep < flussi.size()) {
            currentFlussoIndex = currentStep;
        }
    }

    public Flusso getCurrentFlusso() {
        if (currentFlussoIndex < flussi.size()) {
            return flussi.get(currentFlussoIndex);
        }
        return null;
    }

    //il codice per calcolare il valore medio di B_i:
    public double calcolaB_iMedio() {
        double sum = 0;
        for (Flusso flusso : flussi) {
            sum += flusso.getB_i();
        }
        return sum / flussi.size();
    }

    public int getId() {
        return id;
    }
}