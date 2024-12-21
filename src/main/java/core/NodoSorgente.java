package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NodoSorgente {
    private final int id;
    private final List<Flusso> flussi;

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

    public int getId() {
        return id;
    }
}