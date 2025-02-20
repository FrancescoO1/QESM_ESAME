package Core;

import java.util.ArrayList;
import java.util.List;

class Utente {
    private final int idUtente; //identifico in modo univoco l'utente
    private List<Flusso> flussi;

    public Utente(int idUtente) {
        this.idUtente = idUtente;
        this.flussi = new ArrayList<>();
    }

    public void aggiungiFlusso(Flusso flusso) {
        flussi.add(flusso);
    }

    public List<Flusso> getFlussi() {
        return flussi;
    }
}
