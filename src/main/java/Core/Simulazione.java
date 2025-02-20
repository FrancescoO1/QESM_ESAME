package Core;

import java.util.ArrayList;
import java.util.List;

class Simulazione {
    private List<NodoIPN> nodi;
    private List<Utente> utenti;

    public Simulazione(List<NodoIPN> nodi, List<Utente> utenti) {
        this.nodi = nodi;
        this.utenti = utenti;
    }

    public void esegui(int iterazioni, Grafici grafici) {    //aggiunto grafici qua
        Matching.resetStabilita(); // Reset all'inizio della simulazione
        Matching.resetUtilitàFinale(); // Reset all'inizio della simulazione
        double Media_Bi = Matching.ValorMedio_Bi(utenti);
        double Media_p_z_i = Matching.ValorMedio_p_z_i(utenti);
        double Media_R_i = Matching.ValorMedio_Ri(utenti);
        double Utilita_finale = 0.0;
        int TotaleFlussi = Matching.contaTotaleFlussi(utenti);
        int TotaleNodi = Matching.contaTotaleIPN(nodi);

        for (int i = 0; i < iterazioni; i++) {
            System.out.println("-----------------------------------------------------------------------------------------------------------------------");
            System.out.println("\n=== ITERAZIONE: " + (i + 1) + " ===\n");
            // Reset della disponibilità di tutti i nodi all'inizio di ogni iterazione
            for (NodoIPN nodo : nodi) {
                nodo.resetDisponibilita();
            }
            Matching.eseguiMatching(utenti, nodi);

            //Matching.rimuoviFlussiDaNodiNonDisponibili(utenti, nodi);

            double utilita = Matching.calcolaUtilita(utenti, nodi);
            Utilita_finale = utilita;
            System.out.println("Utilità: " + Math.round(utilita*10)/10 + " %");

            System.out.println();
            Matching.rimuoviFlussiDaNodiNonDisponibili(utenti, nodi); //messo qua

            // Raccolgo tutti i flussi rimanenti per il controllo della stabilità
            List<Flusso> flussiRimanenti = new ArrayList<>();
            for (Utente u : utenti) {
                flussiRimanenti.addAll(u.getFlussi());
            }
            Matching.stabilita(flussiRimanenti, nodi, i + 1);
            Matching.aggiornamentoValoriIterazione(utenti, nodi);
            System.out.println("\n=== FINE ITERAZIONE: " + (i + 1) + " ===\n");
            if (Matching.tuttiFlussiElaborati(utenti)) {
                System.out.println("-----------------------------------------------------------------------------------------------------------------------");
                System.out.println("\n=== TUTTI I FLUSSI SONO STATI ELABORATI, INTERRUZIONE DELLA SIMULAZIONE. ===");
                System.out.println("-----------------------------------------------------------------------------------------------------------------------");
                break;
            }
        }
        Matching.setUtilitàFinale(Utilita_finale);
        double P_QoS_negata = Matching.ProbabilitaQoS_Negata();
        System.out.println();
        System.out.println("\n=== DATI RIEPILOGO SIMULAZIONE: ===");
        System.out.println();
        System.out.println("Nodi IPN in gioco nella simulazione: " + TotaleNodi);
        System.out.println();
        System.out.println("Flussi in gioco nella simulazione: " + TotaleFlussi);
        System.out.println();
        System.out.println("Probabilità di QoS Negata della simulazione: " + P_QoS_negata);
        System.out.println();
        System.out.println("Valore medio delle Bi della simulazione: " + Media_Bi);
        System.out.println();
        System.out.println("Valore medio dei p_z_i della simulazione: " + Media_p_z_i);
        System.out.println();
        System.out.println("Valore medio degli R_i della simulazione: " + Media_R_i);
        System.out.println();
        System.out.println("Utilità finale della simulazione: " + Math.round(Utilita_finale*10)/10 + " %");

        grafici.addSimulationResult(P_QoS_negata, Media_Bi, Utilita_finale, TotaleFlussi, Media_R_i, TotaleNodi, Media_p_z_i); //aggiunto lui per i grafici
    }
}






