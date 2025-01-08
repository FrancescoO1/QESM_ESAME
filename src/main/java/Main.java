import core.*;

import javax.swing.*;
import java.util.*;



//public class Main {

//
//    private static final int NUMERO_NODI_IPN = 4;
//    private static final int NUMERO_ITERAZIONI = 8;
//    private static final int NUMERO_FLUSSI_PER_NODO_SORGENTE = 3;
//    private static final int NUMERO_NODI_SORGENTE = 6;
//    private static final int CAPACITA_DISPONIBILE = 50;
//    private static final int CAPACITA_FLUSSO = 2;
//    private static final int SCADENZA_FLUSSO = 1;  //deve essere bassa perchè se fosse alta avrei sicuramente che C_i_d_z è minore di B_i, per cui Pi diventa nullo, perchè D è zero su tutti i flussi.
//    private static final Random random = new Random();



//    public static void main(String[] args) {
//
//        MatchingGame.calcolaUtilita();
//        System.out.println(MatchingGame.utilita);;
//
//        // Creazione di nodi IPN con capacità disponibili
//        List<NodoIPN> nodiIPN = new ArrayList<>();
//        for (int i = 0; i < NUMERO_NODI_IPN; i++) {
//            NodoIPN nodoIPN = new NodoIPN(i + 1, random.nextGaussian() * 0 + CAPACITA_DISPONIBILE);
//            nodiIPN.add(nodoIPN);
//        }
//
//        // Creazione di nodi sorgenti
//        List<NodoSorgente> nodiSorgenti = new ArrayList<>();
//        for (int i = 0; i < NUMERO_NODI_SORGENTE; i++) {
//            NodoSorgente nodoSorgente = new NodoSorgente(i + 1);
//            nodiSorgenti.add(nodoSorgente);
//        }
//
//        // Aggiungi flussi ai nodi sorgenti
//        //ho latenza di rete a 0.5 e tempo di preelaborazione a 0.5, per cui se non computo, ogni flusso ha C_i_d_z = 1 SEMPRE
//        for (NodoSorgente nodoSorgente : nodiSorgenti) {
//            for (int i = 0; i < NUMERO_FLUSSI_PER_NODO_SORGENTE; i++) {
//                Flusso flusso = new Flusso(i + 1, random.nextGaussian() * 0.5 + SCADENZA_FLUSSO, 0.5, random.nextGaussian() * 0.5 + CAPACITA_FLUSSO, nodoSorgente);
//                nodoSorgente.aggiungiFlusso(flusso);
//            }
//        }
//
//        Simulazione simulazione = new Simulazione(nodiIPN, nodiSorgenti, NUMERO_ITERAZIONI);
//        simulazione.eseguiSimulazione();
//
//    }
//}


//TODO aggiungere nuovi grafici,  guarda roba statica ecc teoria
//TODO sperimentare per vedere come cambia utilità

    public class Main {

        private static final int NUMERO_NODI_IPN = 10;
        private static final int NUMERO_ITERAZIONI = 4;
        private static final int NUMERO_FLUSSI_PER_NODO_SORGENTE = 5;
        private static final int NUMERO_NODI_SORGENTE = 4;
        private static final int CAPACITA_DISPONIBILE = 300;
        private static final int CAPACITA_FLUSSO = 5;
        //se non funziona ricordati di darli un valore
        private static double SCADENZA_FLUSSO;  //deve essere bassa perché se fosse alta avrei sicuramente che C_i_d_z è minore di B_i, per cui Pi diventa nullo, perché D_i è zero su tutti i flussi.
        private static final Random random = new Random();

        public static void main(String[] args) throws InterruptedException {
            double[] valoriScadenza = {1/*, 5.5, 6.5, 8, 9 , 10, 12, 14*/};
            List<Thread> threads = new ArrayList<>();

            for (double scadenza : valoriScadenza) {
                SCADENZA_FLUSSO = scadenza;

                List<NodoIPN> nodiIPN = new ArrayList<>();
                for (int i = 0; i < NUMERO_NODI_IPN; i++) {
                    NodoIPN nodoIPN = new NodoIPN(i + 1, /*random.nextGaussian() * 40 +*/ CAPACITA_DISPONIBILE);
                    nodiIPN.add(nodoIPN);
                }

                List<NodoSorgente> nodiSorgenti = new ArrayList<>();
                for (int i = 0; i < NUMERO_NODI_SORGENTE; i++) {
                    NodoSorgente nodoSorgente = new NodoSorgente(i + 1);
                    nodiSorgenti.add(nodoSorgente);
                }

                for (NodoSorgente nodoSorgente : nodiSorgenti) {
                    for (int i = 0; i < NUMERO_FLUSSI_PER_NODO_SORGENTE; i++) {
                        Flusso flusso = new Flusso(i + 1, random.nextGaussian() * 0.5 + SCADENZA_FLUSSO, 1.5,
                                random.nextGaussian() * 0.5 + CAPACITA_FLUSSO, nodoSorgente);
                        nodoSorgente.aggiungiFlusso(flusso);
                    }
                }

                Simulazione simulazione = new Simulazione(nodiIPN, nodiSorgenti, NUMERO_ITERAZIONI, SCADENZA_FLUSSO);
                Thread thread = simulazione.eseguiSimulazione();
                threads.add(thread);
            }

            // Aspetta che tutte le simulazioni siano completate
            for (Thread thread : threads) {
                thread.join();
            }

            // Ora che tutte le simulazioni sono completate, crea i grafici
            SwingUtilities.invokeLater(ChartManager::plotQoSDeniedVsMediaBi);
        }
    }
