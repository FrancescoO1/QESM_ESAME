package core;

import java.util.*;

public class MatchingGame {
    private final Map<NodoSorgente, Flusso> flussiPerSorgente;
    private final Set<Flusso> allFlussi; //serve per tenere traccia di tutti i flussi
    private final List<NodoIPN> nodiIPN;
    private final List<NodoSorgente> nodiSorgenti;
    private final Map<NodoIPN, List<Flusso>> preferenceListNodo; //preference list di flussi del nodo IPN, non cambia ad ogni iterazione rimane statica
    private final Map<Flusso, List<NodoIPN>> preferenceListFlusso;  //preference list di nodi del flusso, cambia ad ogni iterazione
    private static final Random random = new Random();
    private Map<Flusso, NodoIPN> assegnazioniParziali = new HashMap<>();
    private Set<Flusso> flussiRifiutati = new HashSet<>();


    public MatchingGame(List<NodoSorgente> nodiSorgenti, List<NodoIPN> nodiIPN) {
        this.nodiSorgenti = new ArrayList<>(nodiSorgenti);
        this.nodiIPN = new ArrayList<>(nodiIPN);
        this.allFlussi = new HashSet<>();
        this.flussiPerSorgente = initializeFlussiPerSorgente(nodiSorgenti);
        this.preferenceListNodo = new HashMap<>();
        this.preferenceListFlusso = new HashMap<>();

        System.out.println("\n");
        initializePreferenceLists();
        System.out.println("\n");
    }

    public double calcolaLatenzaRete() {
        return 0.5;
    }

    private Map<NodoSorgente, Flusso> initializeFlussiPerSorgente(List<NodoSorgente> sorgenti) {
        Map<NodoSorgente, Flusso> result = new HashMap<>();
        for (NodoSorgente sorgente : sorgenti) {
            if (!sorgente.getFlussi().isEmpty()) {
                result.put(sorgente, sorgente.getFlussi().getFirst());
                allFlussi.addAll(sorgente.getFlussi()); // Add all flows to tracking set
            }
        }
        return result;
    }

    private void initializePreferenceLists() {
        nodiIPN.forEach(ipn -> preferenceListNodo.put(ipn, new ArrayList<>()));
        allFlussi.forEach(flusso -> {
            preferenceListFlusso.put(flusso, new ArrayList<>());
            System.out.println("Inizializzata preference list vuota per Flusso: " + flusso.getId());
        });
    }

    //la preference list di ogni flussi ordina gli ipn in ordine crescente basandosi sulla metrica V_i_z = C_i_d_z, questa cosa va implementata in un altro metodo
    //ovvero il metodo è:
    //1. per ogni flusso ordina gli ipn in ordine crescente basandosi sulla metrica V_i_z = C_i_d_z
    //2. ritorna la lista ordinata ai flussi di competenza
    //per cui il codice di questo metodo sarebbe questo, devo usare la metrica V_i_z:

    public void aggiornaPreferenceListFlusso_V_i_z() {
        preferenceListFlusso.clear();
        double latenzaRete = 0.5; //calcolo la latenza di rete una sola volta
        for (Flusso flusso : allFlussi) {
            List<NodoIPN> preferenceList = new ArrayList<>(nodiIPN);
            preferenceList.sort((nodo1, nodo2) -> {
                double C_i_d_z1 = nodo1.calcolaC_i_z_d(flusso,latenzaRete);
                double C_i_d_z2 = nodo2.calcolaC_i_z_d(flusso,latenzaRete);
                return Double.compare(C_i_d_z1, C_i_d_z2);
            });
            preferenceListFlusso.put(flusso, preferenceList);
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
            // Crea una lista di flussi basandosi sui flussi associati alle sorgenti
            List<Flusso> preferenceList = new ArrayList<>(allFlussi);

            // Ordina la lista di flussi in ordine decrescente rispetto a 1 / B_i
            preferenceList.sort((flusso1, flusso2) -> {
                double E_z_i1 = 1.0 / flusso1.getB_i(); // Calcola E_z_i per flusso1
                double E_z_i2 = 1.0 / flusso2.getB_i(); // Calcola E_z_i per flusso2
                return Double.compare(E_z_i2, E_z_i1); // Ordina in decrescente
            });

            // Aggiorna la mappa delle liste di preferenza per il nodo corrente
            preferenceListNodo.put(nodo, preferenceList);
        }
    }


    public double calcolaPi() {
        // Conta i flussi che sforano la propria deadline o sono stati rifiutati
        long flussiProblematici = allFlussi.stream()
                .filter(flusso -> {
                    // Se il flusso è stato rifiutato, conta come problematico
                    if (flussiRifiutati.contains(flusso)) {
                        return true;
                    }
                    // Se il flusso è assegnato, verifica se sfora la deadline
                    NodoIPN nodoAssegnato = assegnazioniParziali.get(flusso);
                    if (nodoAssegnato != null) {
                        return flusso.calcolaD_i(nodoAssegnato.calcolaC_i_z_d(flusso, calcolaLatenzaRete()));
                    }
                    // Se il flusso non è né rifiutato né assegnato, conta come problematico
                    return true;
                })
                .count();

        // Calcola il rapporto tra i flussi problematici e il totale
        return (double) flussiProblematici / allFlussi.size();
    }
    public Double calcolaUtilita() {
        // Calcolo di pi
        double pi = calcolaPi();

        // Calcolo di A: somma delle allocazioni attive
        long allocazioniAttive = assegnazioniParziali.size();

        // Se non ci sono allocazioni attive o tutti i flussi sono stati rifiutati
        if (allocazioniAttive == 0 || flussiRifiutati.size() == allFlussi.size()) {
            return 0.0; // Il sistema non sta facendo nulla di utile
        }

        double somma = 0.0;

        // Calcolo della somma pesata dei tempi di completamento
        for (Flusso flusso : allFlussi) {
            NodoIPN nodoAssegnato = assegnazioniParziali.get(flusso);
            if (nodoAssegnato != null) {
                double latenzaRete = calcolaLatenzaRete();
                double C_i_d_z = nodoAssegnato.calcolaC_i_z_d(flusso, latenzaRete);
                somma += C_i_d_z;
            } else if (flussiRifiutati.contains(flusso)) {
                // Penalizza l'utilità per i flussi rifiutati
                somma += flusso.getB_i() * 2; // Aggiungiamo una penalità proporzionale alla deadline
            }
        }

        // Calcolo dell'utilità considerando anche i flussi rifiutati
        double utilita = ((1 / ((pi * somma) / allocazioniAttive)) * 100);

        // Gestione di casi limite
        if (Double.isInfinite(utilita) || Double.isNaN(utilita)) {
            return 0.0; // Se c'è un problema nel calcolo, il sistema non è utile
        }

        // Limita l'utilità al range [0, 100]
        return Math.min(100.0, Math.max(0.0, utilita));
    }

    public NodoIPN algoritmoMatching(Flusso flusso) {
        aggiornaPreferenceListFlusso_V_i_z();
        preferenceListNodo_E_z_i();
        NodoIPN migliorNodo = null;
        double migliorTempoCompletamento = Double.MAX_VALUE;

        // Prima rimuovi il flusso dalla coda del nodo precedente se esisteva
        NodoIPN nodoPrecedente = assegnazioniParziali.get(flusso);
        if (nodoPrecedente != null) {
            nodoPrecedente.rimuoviFlussoInCoda(flusso);
        }

        boolean trovataCapacitaSufficiente = false;

        for (NodoIPN nodo : nodiIPN) {
            if (!nodo.haCapacitaSufficiente(flusso)) {
                continue;
            }

            trovataCapacitaSufficiente = true;

            // Aggiungi temporaneamente il flusso alla coda per calcolare il tempo di completamento
            nodo.aggiungiFlussoInCoda(flusso);

            double latenzaRete = calcolaLatenzaRete();
            double tempoCompletamento = nodo.calcolaC_i_z_d(flusso, latenzaRete);

            // Rimuovi il flusso dalla coda temporanea
            nodo.rimuoviFlussoInCoda(flusso);

            if (tempoCompletamento < migliorTempoCompletamento) {
                migliorTempoCompletamento = tempoCompletamento;
                migliorNodo = nodo;
            }
        }
        // Se nessun nodo ha capacità sufficiente, aggiungi il flusso a quelli rifiutati
        if (!trovataCapacitaSufficiente) {
            flussiRifiutati.add(flusso);
            // Rimuovi eventuali assegnazioni precedenti
            assegnazioniParziali.remove(flusso);
            return null;
        }

        if (migliorNodo != null) {
            // Aggiungi definitivamente il flusso alla coda del miglior nodo
            migliorNodo.aggiungiFlussoInCoda(flusso);
            migliorNodo.CalcolaP_i_d_z(flusso);
            flusso.calcolaT_i(migliorTempoCompletamento);
            migliorNodo.decrementaCapacita(flusso.getCapacita());
            // Aggiorna assegnazione parziale
            aggiornaAssegnazioneParziale(flusso, migliorNodo);
            preferenceListNodo.get(migliorNodo).add(flusso);
            // Rimuovi il flusso dai rifiutati se era stato precedentemente rifiutato
            flussiRifiutati.remove(flusso);
        }

        aggiornaPreferenceListFlusso_V_i_z();
        return migliorNodo;
    }

    public void aggiornaAssegnazioneParziale(Flusso flusso, NodoIPN nodoIPN) {
        // Rimuovi il flusso dalla coda del nodo precedente se esisteva
        NodoIPN nodoPrecedente = assegnazioniParziali.get(flusso);
        if (nodoPrecedente != null && nodoPrecedente != nodoIPN) {
            nodoPrecedente.rimuoviFlussoInCoda(flusso);
        }

        // Aggiorna l'assegnazione
        assegnazioniParziali.put(flusso, nodoIPN);
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

    public Set<Flusso> getAllFlussi() {
        return allFlussi;
    }

    public NodoIPN getAssegnazioneParziale(Flusso flusso) {
        return assegnazioniParziali.get(flusso);
    }


    // metodo per ottenere i flussi rifiutati
    public Set<Flusso> getFlussiRifiutati() {
        return new HashSet<>(flussiRifiutati);
    }


    // ritorna la preference list del flusso ricevuto in input
    public List<NodoIPN> getPreferenceListFlusso(Flusso flusso) {
        return preferenceListFlusso.get(flusso);
    }


    // ritorna la preference list del nodo ricevuto in input
    public List<Flusso> getPreferenceListNodo(NodoIPN nodo) {
        return preferenceListNodo.get(nodo);
    }

}