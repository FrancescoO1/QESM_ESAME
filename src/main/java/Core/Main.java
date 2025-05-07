


//QESM FINITO

package Core;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        int numeroSimulazioni = 3;
        List<SimulazioneConfig> configurazioni = new ArrayList<>();

        Grafici grafici = new Grafici();  // Grafici per la visualizzazione dei risultati

        // Configurazione Simulazione 1
        configurazioni.add(new SimulazioneConfig(
                new int[]{80, 90, 80, 85, 5, 5},  // capacità nodi, ho 6 nodi
                15,                         // numero iterazioni
                1                          // set di utenti che uso
        ));

        // Configurazione Simulazione 2
        configurazioni.add(new SimulazioneConfig(
                new int[]{65, 60, 55, 70, 10},
                15,
                2
        ));

        // Configurazione Simulazione 3
        configurazioni.add(new SimulazioneConfig(
                new int[]{40, 36, 38, 50},
                15,
                3
        ));

        // Esegui tutte le simulazioni
        for (int i = 0; i < numeroSimulazioni; i++) {
            System.out.println("\n============================================= AVVIO SIMULAZIONE " + (i + 1) + " =============================================");
            eseguiSimulazione(configurazioni.get(i), grafici);
            System.out.println("\n============================================= FINE SIMULAZIONE " + (i + 1) + " =============================================");
        }

        //aggiungere qua i grafici:
        grafici.generateQoSvsBiGrafico(); //genero il grafico 1
        grafici.generateUtilityVsBiGrafico(); //genero il grafico 2
        grafici.generateUtilityVsTasksGrafico(); //genero il grafico 3
        grafici.generateQoSVsRiGrafico(); //genero il grafico 4
        grafici.generateQoSVsIPNGrafico(); //genero il grafico 5
        grafici.generateUtilityVsP_z_iGrafico(); //genero il grafico 6
        grafici.generateQoSVsP_z_iGrafico(); //genero il grafico 7

        System.out.println("\n============================================= COMPLETATE TUTTE LE SIMULAZIONI =============================================");
    }

    private static void eseguiSimulazione(SimulazioneConfig config, Grafici grafici) {
        // Creazione dei nodi IPN
        List<NodoIPN> nodi = new ArrayList<>();
        for (int capacita : config.capacitaNodi) {
            nodi.add(new NodoIPN(capacita));
        }

        // Seleziona il set di utenti appropriato in base alla configurazione
        List<Utente> utenti;
        switch (config.setUtenti) {
            case 1:
                utenti = creaUtenti1();
                break;
            case 2:
                utenti = creaUtenti2();
                break;
            case 3:
                utenti = creaUtenti3();
                break;
            default:
                utenti = creaUtenti1(); // Default al primo set
        }

        // Avvio della simulazione
        Simulazione simulazione = new Simulazione(nodi, utenti);
        simulazione.esegui(config.iterazioni, grafici);
    }

    private static List<Utente> creaUtenti1() {
        List<Utente> utenti = new ArrayList<>();
/*
        // Utente 0 - Set 1
        Utente u0 = new Utente(0);
        u0.aggiungiFlusso(new Flusso(2, 0, 2, 12, 0, 0));
        u0.aggiungiFlusso(new Flusso(5, 0, 3, 12, 0, 1));
        u0.aggiungiFlusso(new Flusso(3, 0, 2, 11, 0, 2));
        u0.aggiungiFlusso(new Flusso(4, 0, 2, 11, 0, 3));
        u0.aggiungiFlusso(new Flusso(5, 0, 3, 11, 0, 4));
        u0.aggiungiFlusso(new Flusso(2, 0, 1, 12, 0, 5));
        utenti.add(u0);

        // Utente 1 - Set 1
        Utente u1 = new Utente(1);
        u1.aggiungiFlusso(new Flusso(4, 0, 1, 11, 1, 0));
        u1.aggiungiFlusso(new Flusso(2, 0, 2, 12, 1, 1));
        u1.aggiungiFlusso(new Flusso(3, 0, 2, 8, 1, 2));
        u1.aggiungiFlusso(new Flusso(4, 0, 2, 11, 1, 3));
        u1.aggiungiFlusso(new Flusso(5, 0, 3, 11, 1, 4));
        u1.aggiungiFlusso(new Flusso(2, 0, 1, 11, 1, 5));
        utenti.add(u1);

        // Utente 2 - Set 1
        Utente u2 = new Utente(2);
        u2.aggiungiFlusso(new Flusso(2, 0, 1, 8, 2, 0));
        u2.aggiungiFlusso(new Flusso(3, 0, 2, 12, 2, 1));
        u2.aggiungiFlusso(new Flusso(4, 0, 2, 11, 2, 2));
        u2.aggiungiFlusso(new Flusso(5, 0, 3, 12, 2, 3));
        u2.aggiungiFlusso(new Flusso(2, 0, 1, 11, 2, 4));
        u2.aggiungiFlusso(new Flusso(3, 0, 2, 11, 2, 5));
        utenti.add(u2);

        return utenti;

 */

        // Utente 0 - Set 1
        Utente u0 = new Utente(0);
        u0.aggiungiFlusso(new Flusso(2, 0, 2, 12, 0, 0));
        u0.aggiungiFlusso(new Flusso(5, 0, 3, 12, 0, 1));
        u0.aggiungiFlusso(new Flusso(3, 0, 2, 11, 0, 2));
        u0.aggiungiFlusso(new Flusso(4, 0, 2, 11, 0, 3));
        u0.aggiungiFlusso(new Flusso(5, 0, 3, 11, 0, 4));
        u0.aggiungiFlusso(new Flusso(2, 0, 1, 12, 0, 5));
        u0.aggiungiFlusso(new Flusso(3, 0, 2, 11, 0, 6));
        u0.aggiungiFlusso(new Flusso(4, 0, 2, 11, 0, 7));
        u0.aggiungiFlusso(new Flusso(5, 0, 3, 11, 0, 8));
        u0.aggiungiFlusso(new Flusso(2, 0, 1, 12, 0, 9));
        u0.aggiungiFlusso(new Flusso(3, 0, 2, 11, 0, 10));

        utenti.add(u0);

        // Utente 1 - Set 1
        Utente u1 = new Utente(1);
        u1.aggiungiFlusso(new Flusso(4, 0, 1, 11, 1, 0));
        u1.aggiungiFlusso(new Flusso(2, 0, 2, 12, 1, 1));
        u1.aggiungiFlusso(new Flusso(3, 0, 2, 8, 1, 2));
        u1.aggiungiFlusso(new Flusso(4, 0, 2, 11, 1, 3));
        u1.aggiungiFlusso(new Flusso(5, 0, 3, 11, 1, 4));
        u1.aggiungiFlusso(new Flusso(2, 0, 1, 11, 1, 5));
        u1.aggiungiFlusso(new Flusso(3, 0, 2, 11, 1, 6));
        u1.aggiungiFlusso(new Flusso(4, 0, 2, 11, 1, 7));
        u1.aggiungiFlusso(new Flusso(5, 0, 3, 11, 1, 8));
        u1.aggiungiFlusso(new Flusso(2, 0, 1, 12, 1, 9));
        u1.aggiungiFlusso(new Flusso(3, 0, 2, 11, 1, 10));
        utenti.add(u1);

        // Utente 2 - Set 1
        Utente u2 = new Utente(2);
        u2.aggiungiFlusso(new Flusso(2, 0, 1, 8, 2, 0));
        u2.aggiungiFlusso(new Flusso(3, 0, 2, 12, 2, 1));
        u2.aggiungiFlusso(new Flusso(4, 0, 2, 11, 2, 2));
        u2.aggiungiFlusso(new Flusso(5, 0, 3, 12, 2, 3));
        u2.aggiungiFlusso(new Flusso(2, 0, 1, 11, 2, 4));
        u2.aggiungiFlusso(new Flusso(3, 0, 2, 11, 2, 5));
        u2.aggiungiFlusso(new Flusso(4, 0, 2, 11, 2, 6));
        u2.aggiungiFlusso(new Flusso(5, 0, 3, 11, 2, 7));
        u2.aggiungiFlusso(new Flusso(2, 0, 1, 12, 2, 8));
        u2.aggiungiFlusso(new Flusso(3, 0, 2, 11, 2, 9));
        u2.aggiungiFlusso(new Flusso(4, 0, 2, 11, 2, 10));
        utenti.add(u2);

        return utenti;
    }

    private static List<Utente> creaUtenti2() {
        List<Utente> utenti = new ArrayList<>();

        // Utente 0 - Set 2
        Utente u0 = new Utente(0);
        u0.aggiungiFlusso(new Flusso(3, 0, 4, 10, 0, 0));
        u0.aggiungiFlusso(new Flusso(3, 0, 3, 9, 0, 1));
        u0.aggiungiFlusso(new Flusso(3, 0, 3, 8, 0, 2));
        u0.aggiungiFlusso(new Flusso(5, 0, 3, 9, 0, 3));
        u0.aggiungiFlusso(new Flusso(3, 0, 3, 9, 0, 4));
        u0.aggiungiFlusso(new Flusso(3, 0, 4, 9, 0, 5));
        u0.aggiungiFlusso(new Flusso(3, 0, 3, 8, 0, 6));
        u0.aggiungiFlusso(new Flusso(5, 0, 3, 9, 0, 7));
        utenti.add(u0);

        // Utente 1 - Set 2
        Utente u1 = new Utente(1);
        u1.aggiungiFlusso(new Flusso(3, 0, 3, 9, 1, 0));
        u1.aggiungiFlusso(new Flusso(5, 0, 3, 9, 1, 1));
        u1.aggiungiFlusso(new Flusso(3, 0, 4, 9, 1, 2));
        u1.aggiungiFlusso(new Flusso(3, 0, 3, 10, 1, 3));
        u1.aggiungiFlusso(new Flusso(4, 0, 4, 9, 1, 4));
        u1.aggiungiFlusso(new Flusso(3, 0, 4, 9, 1, 5));
        u1.aggiungiFlusso(new Flusso(3, 0, 3, 9, 1, 6));
        u1.aggiungiFlusso(new Flusso(4, 0, 3, 8, 1, 7));
        utenti.add(u1);

        // Utente 2 - Set 2
        Utente u2 = new Utente(2);
        u2.aggiungiFlusso(new Flusso(3, 0, 3, 9, 2, 0));
        u2.aggiungiFlusso(new Flusso(4, 0, 4, 9, 2, 1));
        u2.aggiungiFlusso(new Flusso(4, 0, 3, 9, 2, 2));
        u2.aggiungiFlusso(new Flusso(3, 0, 3, 9, 2, 3));
        u2.aggiungiFlusso(new Flusso(3, 0, 3, 10, 2, 4));
        u2.aggiungiFlusso(new Flusso(4, 0, 4, 9, 2, 5));
        u2.aggiungiFlusso(new Flusso(3, 0, 3, 8, 2, 6));
        u2.aggiungiFlusso(new Flusso(5, 0, 3, 8, 2, 7));
        utenti.add(u2);

        return utenti;
    }

    private static List<Utente> creaUtenti3() {
        List<Utente> utenti = new ArrayList<>();

        // Utente 0 - Set 3
        Utente u0 = new Utente(0);
        u0.aggiungiFlusso(new Flusso(4, 0, 9, 3, 0, 0));
        u0.aggiungiFlusso(new Flusso(3, 0, 9, 2, 0, 1));
        u0.aggiungiFlusso(new Flusso(4, 0, 8, 4, 0, 2));
        u0.aggiungiFlusso(new Flusso(5, 0, 9, 3, 0, 3));
        u0.aggiungiFlusso(new Flusso(4, 0, 9, 3, 0, 4));
        u0.aggiungiFlusso(new Flusso(3, 0, 8, 2, 0, 5));
        u0.aggiungiFlusso(new Flusso(4, 0, 8, 4, 0, 6));
        u0.aggiungiFlusso(new Flusso(5, 0, 9, 3, 0, 7));
        u0.aggiungiFlusso(new Flusso(5, 0, 9, 3, 0, 8));
        u0.aggiungiFlusso(new Flusso(4, 0, 9, 3, 0, 9));
        u0.aggiungiFlusso(new Flusso(3, 0, 7, 2, 0, 10));
        u0.aggiungiFlusso(new Flusso(3, 0, 8, 4, 0, 11));
        u0.aggiungiFlusso(new Flusso(5, 0, 10, 3, 0, 12));
        utenti.add(u0);

        // Utente 1 - Set 3
        Utente u1 = new Utente(1);
        u1.aggiungiFlusso(new Flusso(5, 0, 8, 2, 1, 0));
        u1.aggiungiFlusso(new Flusso(3, 0, 9, 3, 1, 1));
        u1.aggiungiFlusso(new Flusso(3, 0, 8, 2, 1, 2));
        u1.aggiungiFlusso(new Flusso(4, 0, 8, 4, 1, 3));
        u1.aggiungiFlusso(new Flusso(5, 0, 8, 4, 1, 4));
        u1.aggiungiFlusso(new Flusso(3, 0, 9, 2, 1, 5));
        u1.aggiungiFlusso(new Flusso(3, 0, 7, 2, 1, 6));
        u1.aggiungiFlusso(new Flusso(4, 0, 10, 2, 1, 7));
        u1.aggiungiFlusso(new Flusso(4, 0, 8, 2, 1, 8));
        u1.aggiungiFlusso(new Flusso(5, 0, 8, 2, 1, 9));
        u1.aggiungiFlusso(new Flusso(3, 0, 10, 4, 1, 10));
        u1.aggiungiFlusso(new Flusso(3, 0, 7, 2, 1, 11));
        u1.aggiungiFlusso(new Flusso(4, 0, 10, 2, 1, 12));
        utenti.add(u1);

        // Utente 2 - Set 3
        Utente u2 = new Utente(2);
        u2.aggiungiFlusso(new Flusso(4, 0, 9, 2, 2, 0));
        u2.aggiungiFlusso(new Flusso(3, 0, 8, 2, 2, 1));
        u2.aggiungiFlusso(new Flusso(3, 0, 8, 4, 2, 2));
        u2.aggiungiFlusso(new Flusso(5, 0, 9, 2, 2, 3));
        u2.aggiungiFlusso(new Flusso(4, 0, 9, 2, 2, 4));
        u2.aggiungiFlusso(new Flusso(3, 0, 10, 4, 2, 5));
        u2.aggiungiFlusso(new Flusso(3, 0, 8, 2, 2, 6));
        u2.aggiungiFlusso(new Flusso(5, 0, 9, 3, 2, 7));
        u2.aggiungiFlusso(new Flusso(5, 0, 9, 2, 2, 8));
        u2.aggiungiFlusso(new Flusso(4, 0, 10, 4, 2, 9));
        u2.aggiungiFlusso(new Flusso(3, 0, 7, 2, 2, 10));
        u2.aggiungiFlusso(new Flusso(3, 0, 10, 2, 2, 11));
        u2.aggiungiFlusso(new Flusso(5, 0, 9, 4, 2, 12));
        utenti.add(u2);

        return utenti;
    }
}

// Classe di supporto per configurare ogni simulazione
class SimulazioneConfig {
    public final int[] capacitaNodi;
    public final int iterazioni;
    public final int setUtenti;  // Nuovo campo per indicare quale set di utenti usare

    public SimulazioneConfig(int[] capacitaNodi, int iterazioni, int setUtenti) {
        this.capacitaNodi = capacitaNodi;
        this.iterazioni = iterazioni;
        this.setUtenti = setUtenti;
    }
}



