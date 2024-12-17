package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchingGame {   // costruttore
    private List<Flusso> flussi;
    private List<NodoIPN> nodiIPN;
    private Map<NodoIPN, List<Flusso>> preferenceListNodo;  // Mappa degli assegnamenti IPN -> Flussi ovvero la preference list ancora non basata su E_z_(i)
    private Map<Flusso, List<NodoIPN>> preferenceListFlusso;

    public MatchingGame(List<Flusso> flussi, List<NodoIPN> nodiIPN) { // costruttore
        this.flussi = flussi;
        this.nodiIPN = nodiIPN;
        this.preferenceListNodo = new HashMap<>();
        this.preferenceListFlusso = new HashMap<>();

        // java ha dei for strani crea un iteratore ipn di tipo NodoIPN e gli fa scorrrere la lista nodiIPN, ad ogni iterazione ipn assume il valore di ogni
        // elemento della lista, nell'ordine in cui sono posti, il ciclo termina quando ogni elemento contenuto nella lista è stato visitato
        for (NodoIPN ipn : nodiIPN) {
            preferenceListNodo.put(ipn, new ArrayList<>());  // Inizializza la lista dei flussi vuota assegnati ad ogni IPN
        }

        for (Flusso flu : flussi) {
            preferenceListFlusso.put(flu, new ArrayList<>());  // Inizializza la lista dei nodi vuota assegnati ad ogni flusso
        }
    }

    //DEFINIRE LE METRICHE E_z(i) e V_i(z)

    //SCRIVERE IL CODICE CON I FOR NON ADDIDATI, LO PSEUDOCODICE DEL PAPER
   /* public void eseguiMatching() { //algoritmo di matching dei flussi al nodo IPN di interesse
        for (Flusso flusso : flussi) {
            NodoIPN migliorNodo = null;
            double migliorTempoCompletamento = Double.MAX_VALUE;  //metto un tetto che in quel momento è il miglior tempo di completamento

            // qua si calcola quali flussi vanno assegnati al nodo IPN in base alle miglior caratteristiche in quel momento del nodo
            // mi baso su una latenza di rete t_i,z calcolata in modo randomica, e il tempo di completamento C_i,d,z.
            for (NodoIPN nodo : nodiIPN) {
                double latenzaRete = calcolaLatenzaRete();
                double tempoCompletamento = nodo.calcolaC_i_z_d(flusso, latenzaRete);

                //trovato il miglior tempo di completemento, quel nodo diventa il migliore tra i nodi
                if (tempoCompletamento < migliorTempoCompletamento) {
                    migliorTempoCompletamento = tempoCompletamento;
                    migliorNodo = nodo;
                }
            }

            //essendo il miglior nodo in quel momento gli assegno i flussi
            if (migliorNodo != null) {
                migliorNodo.CalcolaP_i_d_z(flusso);
                flusso.calcolaTempoCompletamento(calcolaLatenzaRete());
                double latenzaRete = calcolaLatenzaRete();
                double C_i_d_z = migliorNodo.calcolaC_i_z_d(flusso, latenzaRete);
                flusso.calcolaT_i(C_i_d_z);
                System.out.println("T_i del flusso " + flusso.getId() + ": " + flusso.getT_i());
                preferenceListNodo.get(migliorNodo).add(flusso);  // Aggiungi il flusso all'IPN selezionato
            }
        }
    }
*/







    private double calcolaLatenzaRete() {
        return Math.random() * 0.5;
    }


    //SCRIVERE CALCOLO UTILITÀ CHE È DATA DA UN CALCOLO MATRICIALE, CERCARE LA LIBRERIA PER JAVA
   /* public double calcolaUtilita() {
        double utilitaTotale = 0.0; //variabile che accumula il numero di task che rispettano la scadenza B_i

        //Per ogni flusso, viene controllato se il tempoCompletamentoTeorico del task è inferiore o uguale alla sua scadenza, non va bene
        // dovrebbe usare il tempo di completamento reale C_i,d,z
        // Se il tempoCompletamento del flusso è minore o uguale alla scadenza, viene aggiunto 1 a utilitaTotale
        // Se il tempoCompletamento è maggiore della scadenza, viene aggiunto 0
        // ogni task che rispetta la scadenza contribuisce con 1 a utilitaTotale
        for (Flusso flusso : flussi) {
            utilitaTotale += (flusso.getTempoCompletamento() <= flusso.getB_i()) ? 1 : 0;
        }

        //utilitaTotale / flussi.size() calcola la frazione di task completati entro la scadenza rispetto al numero totale di task (flussi.size()),
        // restituendo un valore compreso tra 0 e 1.
        return utilitaTotale / flussi.size(); //es. 3/5 = 0.6 -> circa il 60 percento dei task sono stati preelaborati dal nodo IPN entro la scadenza beta
    }
    */

    //il parametro pi che esprime il rapporto tra il numero D_i e il numero dei flussi totali che ho, la funzione che fa ciò è la seguente:
    public double calcolaPi() {
          double D_i = 0;
            for (Flusso flusso : flussi) {
                D_i += flusso.getD_i();
       }
           return D_i / flussi.size();
    }


    // Ritorna i flussi assegnati a un determinato IPN, restiuisce la preference list che si basa su E_z_(i)
    public List<Flusso> getPreferencelist_di_IPN_z(NodoIPN ipn) {
        return preferenceListNodo.getOrDefault(ipn, new ArrayList<>());
    }

    public List<Flusso> getFlussi() {
        return flussi;
    }
    public List<NodoIPN> getNodiIPN() {
        return nodiIPN;
    }

}
