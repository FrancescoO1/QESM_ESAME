import core.*;

import javax.swing.*;
import java.util.*;

//TODO FAI I GRAFICI del paper in un'altra classe HO FATTO CORRETTO
//TODO cambiare i nomi nell'interfaccia grafica e farli tutti in italiano
//TODO utilita non sincrona tra Ipn interface e simulazione, nell'interfaccia si parte con valori altissimi di utilità e poi si allinea con il log forse
//il log funziona ho un'utilità del sistema che pressocchè non cambia. rimane stabile.



public class Main {

    private static final int NUMERO_NODI_IPN = 4;
    private static final int NUMERO_ITERAZIONI = 4; //se ho troppe iterazioni l'utilità aumenta del sistema dopo essere diminuita, perchè pi torna a zero dato che i flussi non computano più per cui non ho flussi che sforano la deadline
    private static final int NUMERO_FLUSSI_PER_NODO_SORGENTE = 3;
    private static final int NUMERO_NODI_SORGENTE = 6;
    private static final int CAPACITA_DISPONIBILE = 40;
    private static final int CAPACITA_FLUSSO = 2;
    private static final int SCADENZA_FLUSSO = 1;  //deve essere bassa perchè se fosse alta avrei sicuramente che C_i_d_z è minore di B_i, per cui Pi diventa nullo, perchè D è zero su tutti i flussi.
    private static final int NUMERO_GRAFICI = 3;
    private static final Random random = new Random();

    public static void main(String[] args) {

        // Creazione di nodi IPN con capacità disponibili
        List<NodoIPN> nodiIPN = new ArrayList<>();
        for (int i = 0; i < NUMERO_NODI_IPN; i++) {
            NodoIPN nodoIPN = new NodoIPN(i + 1, random.nextGaussian() * 0 + CAPACITA_DISPONIBILE);
            nodiIPN.add(nodoIPN);
        }

        // Creazione di nodi sorgenti
        List<NodoSorgente> nodiSorgenti = new ArrayList<>();
        for (int i = 0; i < NUMERO_NODI_SORGENTE; i++) {
            NodoSorgente nodoSorgente = new NodoSorgente(i + 1);
            nodiSorgenti.add(nodoSorgente);
        }

        // Aggiungi flussi ai nodi sorgenti
        //ho latenza di rete a 0.5 e tempo di preelaborazione a 0.5, per cui se non computo, ogni flusso ha C_i_d_z = 1 SEMPRE
        for (NodoSorgente nodoSorgente : nodiSorgenti) {
            for (int i = 0; i < NUMERO_FLUSSI_PER_NODO_SORGENTE; i++) {
                Flusso flusso = new Flusso(i + 1, random.nextGaussian() * 0.5 + SCADENZA_FLUSSO, 0.5, random.nextGaussian() * 0.5 + CAPACITA_FLUSSO, nodoSorgente);
                nodoSorgente.aggiungiFlusso(flusso);
            }
        }

        Simulazione simulazione = new Simulazione(nodiIPN, nodiSorgenti, NUMERO_ITERAZIONI, NUMERO_GRAFICI);
        simulazione.eseguiSimulazione();

    }
}
