import core.*;

import javax.swing.*;
import java.util.*;


public class Main {

    private static final int NUMERO_NODI_IPN = 5;
    private static final int NUMERO_ITERAZIONI = 10;
    private static final int NUMERO_FLUSSI_PER_NODO_SORGENTE = 2;
    private static final int NUMERO_NODI_SORGENTE = 7;
    private static final int CAPACITA_DISPONIBILE = 50;
    private static final int CAPACITA_FLUSSO = 2;
    private static final Random random = new Random();

    public static void main(String[] args) {

        // Creazione di nodi IPN con capacità disponibili
        List<NodoIPN> nodiIPN = new ArrayList<>();
        for (int i = 0; i < NUMERO_NODI_IPN; i++) {
            NodoIPN nodoIPN = new NodoIPN(i + 1, random.nextGaussian() * 10 + CAPACITA_DISPONIBILE);
            nodiIPN.add(nodoIPN);
        }

        // Creazione di nodi sorgenti
        List<NodoSorgente> nodiSorgenti = new ArrayList<>();
        for (int i = 0; i < NUMERO_NODI_SORGENTE; i++) {
            NodoSorgente nodoSorgente = new NodoSorgente(i + 1);
            nodiSorgenti.add(nodoSorgente);
        }

        // Aggiungi flussi ai nodi sorgenti
        for (NodoSorgente nodoSorgente : nodiSorgenti) {
            for (int i = 0; i < NUMERO_FLUSSI_PER_NODO_SORGENTE; i++) {
                Flusso flusso = new Flusso(i + 1, 2.0 - (i * 0.1), 0.5 + (i * 0.05), random.nextGaussian() * 0.5 + CAPACITA_FLUSSO );
                nodoSorgente.aggiungiFlusso(flusso);
            }
        }

        Simulazione simulazione = new Simulazione(nodiIPN, nodiSorgenti, NUMERO_ITERAZIONI);
        simulazione.eseguiSimulazione();

    }
}
