package core;

import java.util.*;

public class MatchingGame {
    private final Map<NodoSorgente, Flusso> flussiPerSorgente;
    private final List<NodoIPN> nodiIPN;
    private final List<NodoSorgente> nodiSorgenti;
    private final Map<NodoIPN, List<Flusso>> preferenceListNodo; //preference list di flussi del nodo IPN, non cambia ad ogni iterazione rimane statica
    private final Map<Flusso, List<NodoIPN>> preferenceListFlusso;  //preference list di nodi del flusso, cambia ad ogni iterazione
    private static final Random random = new Random();

    private Map<Flusso, NodoIPN> assegnazioniParziali = new HashMap<>();

    public MatchingGame(List<NodoSorgente> nodiSorgenti, List<NodoIPN> nodiIPN) {
        this.nodiSorgenti = new ArrayList<>(nodiSorgenti);
        this.nodiIPN = new ArrayList<>(nodiIPN);
        this.flussiPerSorgente = initializeFlussiPerSorgente(nodiSorgenti);
        this.preferenceListNodo = new HashMap<>();
        this.preferenceListFlusso = new HashMap<>();

        initializePreferenceLists();
    }

    private double calcolaLatenzaRete() {
        return random.nextDouble() * 0.5;
    }

    //questo metodo inizializza i flussi per le sorgenti, ovvero per ogni sorgente prende il primo flusso e lo mette in una mappa, funziona in questo modo:
    //1. per ogni sorgente prende il primo flusso e lo mette in una mappa
    //2. ritorna la mappa

    private Map<NodoSorgente, Flusso> initializeFlussiPerSorgente(List<NodoSorgente> sorgenti) {
        Map<NodoSorgente, Flusso> result = new HashMap<>();
        for (NodoSorgente sorgente : sorgenti) {
            if (!sorgente.getFlussi().isEmpty()) {
                result.put(sorgente, sorgente.getFlussi().getFirst()); // Prende il primo flusso e lo mette nella mappa assegnato alla sorgente
            }
        }
        return result;
    }

    //questo metodo inizializza le liste di preferenza, ovvero per ogni nodo IPN e per ogni flusso crea una lista vuota e la mette in una mappa, funziona in questo modo:
    //1. per ogni nodo IPN crea una lista vuota di flussi e la mette in una mappa
    //2. per ogni flusso crea una lista vuota di nodi IPN e la mette in una mappa
    //3. ritorna le mappe

    private void initializePreferenceLists() {
        nodiIPN.forEach(ipn -> preferenceListNodo.put(ipn, new ArrayList<>()));
        flussiPerSorgente.values().forEach(flusso -> preferenceListFlusso.put(flusso, new ArrayList<>()));
    }

    //la preference list di ogni flussi ordina gli ipn in ordine crescente basandosi sulla metrica V_i_z = C_i_d_z, questa cosa va implementata in un altro metodo
    //ovvero il metodo è:
    //1. per ogni flusso ordina gli ipn in ordine crescente basandosi sulla metrica V_i_z = C_i_d_z
    //2. ritorna la lista ordinata ai flussi di competenza
    //per cui il codice di questo metodo sarebbe questo, devo usare la metrica V_i_z:

    public void aggiornaPreferenceListFlusso_V_i_z() {
        for (Flusso flusso : flussiPerSorgente.values()) {
            List<NodoIPN> preferenceList = new ArrayList<>(nodiIPN);
            preferenceList.sort((nodo1, nodo2) -> {                             //con sort ordino in modo crescente
                double latenzaRete = calcolaLatenzaRete();
                double C_i_d_z1 = nodo1.calcolaC_i_z_d(flusso, latenzaRete);    //V_i_z in questo codice è C_i_d_z1 e C_i_d_z2
                double C_i_d_z2 = nodo2.calcolaC_i_z_d(flusso, latenzaRete);
                return Double.compare(C_i_d_z1, C_i_d_z2);
            });
            preferenceListFlusso.put(flusso, preferenceList);                   //aggiungo la lista ordinata alla preference list del flusso
        }
    }


    //la preference list di flussi del nodi è una preference list che si basa sull'ordinamento decrescente dei flussi in base alla metrica
    //E_z_i = 1/ B_i, questa preference list di flussi del nodo va creata per ogni nodo una sola volta e vale per tutte le iterazioni:
    //i nodi devono preferire i flussi che hanno una scadenza breve, ovvero che hanno E_z_i alto.
    //il codice che esegue quanto detto è il seguente:
    //1. per ogni nodo IPN ordina i flussi in ordine decrescente basandosi sulla metrica E_z_i = 1/ B_i
    //2. ritorna la lista ordinata ai nodi di competenza
    //questo coidice funziona così:
    //1. per ogni nodo IPN ordina i flussi in ordine decrescente basandosi sulla metrica E_z_i = 1/ B_i
    //2. ritorna la lista ordinata ai nodi di competenza

    public void preferenceListNodo_E_z_i() {
        for (NodoIPN nodo : nodiIPN) {
            List<Flusso> preferenceList = new ArrayList<>(flussiPerSorgente.values());
            preferenceList.sort((flusso1, flusso2) -> {
                double E_z_i1 = 1 / flusso1.getB_i();
                double E_z_i2 = 1 / flusso2.getB_i();
                return Double.compare(E_z_i2, E_z_i1);
            });
            preferenceListNodo.put(nodo, preferenceList);
        }
    }


    public double calcolaPi() {
        double D_i_totale = flussiPerSorgente.values().stream()
                .mapToDouble(Flusso::calcolaD_i)
                .sum();
        return D_i_totale / flussiPerSorgente.size();
    }

    public double calcolaUtilita() {
        double pi = calcolaPi();
        double A = flussiPerSorgente.size() * nodiIPN.size();
        double somma = 0.0;

        for (Flusso flusso : flussiPerSorgente.values()) {
            for (NodoIPN nodo : nodiIPN) {
                double latenzaRete = calcolaLatenzaRete();
                double C_i_d_z = nodo.calcolaC_i_z_d(flusso, latenzaRete);
                double alpha_i_z = preferenceListNodo.get(nodo).contains(flusso) ? 1 : 0;
                somma += C_i_d_z * alpha_i_z;
            }
        }

        return 1 / (pi * somma / A);
    }



    //con il metodo algoritmoMatching devo implementare l'algoritmo di matching che userà i metodi aggiornaPreferenceListFlusso_V_i_z, aggiornaAssegnazioneParziale e preferenceListNodo_E_z_i.
    //in poche parole lo pseudocodice è questo:
    //for each flusso in flussi do:
    // costruisci la preference list del flusso usando aggiornaPreferenceListFlusso_V_i_z;
    //for each nodoIPN in nodiIPN do:
    // csotruisci la preference list del nodo usando preferenceListNodo_E_z_i;
    //for each flusso in flussi do:
    // invia una proposta al suo IPN preferito contenuto nella preference list del flusso;
    //for each nodoIPN in nodiIPN ricevente almeno una proposta do:
    // accetta la proposta del flusso preferito dal nodoIPN, tra quelle ricevute;
    // rifiuta le altre propste ricevute.
    //il codice che implementa questo pseudocodice è il seguente:
    //quello che fa questa parte di codice è di eseguire il matching per un flusso, ovvero di assegnare un flusso ad un nodo IPN e funziona in questo modo:
    //1. scorre tutti i nodi IPN e controlla se il nodo ha la capacità sufficiente per gestire il flusso
    //2. calcola la latenza di rete
    //3. calcola il tempo di completamento del flusso per il nodo IPN
    //4. se il tempo di completamento è minore del tempo di completamento migliore, allora aggiorna il tempo di completamento migliore e il nodo migliore, perchè lo faccio? perchè voglio assegnare il flusso al nodo che lo completa prima
    //5. calcola il tempo di completamento del flusso per il nodo IPN
    //6. calcola il ritardo del flusso
    //7. decrementa la capacità del nodo IPN
    //8. aggiorna l'assegnazione parziale che serve per tenere traccia dei flussi assegnati
    //9. aggiorna la preference list del nodo
    //10. ritorna il nodo migliore
    public NodoIPN algoritmoMatching(Flusso flusso) {  //forse meglio passare lista di flussi
        aggiornaPreferenceListFlusso_V_i_z();
        preferenceListNodo_E_z_i();
        NodoIPN migliorNodo = null;
        double migliorTempoCompletamento = Double.MAX_VALUE;

        for (NodoIPN nodo : nodiIPN) {
            if (!nodo.haCapacitaSufficiente(flusso)) {
                continue;
            }

            double latenzaRete = calcolaLatenzaRete();
            double tempoCompletamento = nodo.calcolaC_i_z_d(flusso, latenzaRete);

            if (tempoCompletamento < migliorTempoCompletamento) {
                migliorTempoCompletamento = tempoCompletamento;
                migliorNodo = nodo;
            }
        }

        if (migliorNodo != null) {
            migliorNodo.CalcolaP_i_d_z(flusso);
            double latenzaRete = calcolaLatenzaRete();
            double C_i_d_z = migliorNodo.calcolaC_i_z_d(flusso, latenzaRete);
            flusso.calcolaT_i(C_i_d_z);
            migliorNodo.decrementaCapacita(flusso.getCapacita());
            // Aggiorna assegnazione parziale
            aggiornaAssegnazioneParziale(flusso, migliorNodo);
            preferenceListNodo.get(migliorNodo).add(flusso);

        }

        aggiornaPreferenceListFlusso_V_i_z();
        return migliorNodo;
    }

    public int getMaxIterations() {
        // Il numero massimo di iterazioni è determinato dal numero totale di flussi
        // da processare tra tutte le sorgenti
        return nodiSorgenti.stream()
                .mapToInt(sorgente -> sorgente.getFlussi().size())
                .max()
                .orElse(0);
    }

    // Getters
    public List<Flusso> getFlussi() {
        return new ArrayList<>(flussiPerSorgente.values());
    }
    public List<NodoIPN> getNodiIPN() {
        return new ArrayList<>(nodiIPN);
    }
    public List<NodoSorgente> getNodiSorgenti() {
        return new ArrayList<>(nodiSorgenti);
    }
    public Map<Flusso, List<NodoIPN>> getPreferenceListFlusso() {
        return new HashMap<>(preferenceListFlusso);
    }
    public Map<NodoIPN, List<Flusso>> getPreferenceListNodo() {
        return new HashMap<>(preferenceListNodo);
    }

    public NodoIPN getAssegnazioneParziale(Flusso flusso) {
        return assegnazioniParziali.get(flusso);
    }

    public void aggiornaAssegnazioneParziale(Flusso flusso, NodoIPN nodoIPN) {
        assegnazioniParziali.put(flusso, nodoIPN);
    }
}