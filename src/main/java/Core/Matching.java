package Core;
import java.util.*;

//ricorda:
//Una variabile public è accessibile da qualsiasi parte del programma, mentre una variabile private può essere utilizzata solo all'interno della classe in cui è dichiarata.
//il paper parla di prendere le scadenze alte, mentre per minimizzare il ritardo bisognerebbe prendere quelle con scadenza più basse. (parlarne con la pic) il resto funziona tutto dal punto di vista teorico.

public class Matching {

    public static List<Flusso> PL_E_z_i(List<Utente> utenti, List<NodoIPN> nodi) {
        // Raccoglie tutti i flussi da tutti gli utenti
        List<Flusso> tuttiIFlussi = new ArrayList<>();
        for (Utente utente : utenti) {
            tuttiIFlussi.addAll(utente.getFlussi());
        }

        // Per ogni nodo, creare una lista di preferenze basata su E_z_i = 1/B_i
        List<Flusso> preferenzeFlussi = new ArrayList<>(tuttiIFlussi);

        // Ordina i flussi in base a E_z_i (1/B_i) in ordine decrescente
        Collections.sort(preferenzeFlussi, (f1, f2) -> Double.compare(1.0 / f2.B_i, 1.0 / f1.B_i));

        // Stampa le preferenze
        System.out.println("\n--- LISTA PREFERENZE E_z_i (1/B_i decrescente) ---");
        System.out.println();
        for (int i = 0; i < preferenzeFlussi.size(); i++) {
            Flusso flusso = preferenzeFlussi.get(i);
            System.out.println("Preferenza " + (i+1) + ": Flusso " + flusso.getIdFlusso() + " dell'Utente " + flusso.getIdUtente() + " - 1/B_i: " + (1.0 / flusso.B_i));
        }

        // Aggiorna le liste di preferenza per ogni nodo
        for (NodoIPN nodo : nodi) {
            nodo.listaPreferenzeFlussi = new ArrayList<>(preferenzeFlussi);
        }

        return preferenzeFlussi;
    }

    public static List<NodoIPN> PL_V_i_z(Flusso flusso, List<NodoIPN> nodi) {
        // Calcola i tempi di completamento per il flusso su ogni nodo
        List<NodoIPN> preferenze = new ArrayList<>(nodi);

        // Ordina i nodi in base al tempo di completamento crescente
        Collections.sort(preferenze, (n1, n2) -> {
            double tempo1 = n1.calcolaTempoCompletamento(flusso);
            double tempo2 = n2.calcolaTempoCompletamento(flusso);
            return Double.compare(tempo1, tempo2);
        });

        // Stampa le preferenze
        System.out.println();
        System.out.println("\n--- LISTA PREFERENZE V_i_z (Tempi Completamento crescenti) per Flusso " + flusso.getIdFlusso() + " dell' Utente " + flusso.getIdUtente() + " ---");
        System.out.println();
        for (int i = 0; i < preferenze.size(); i++) {
            NodoIPN nodo = preferenze.get(i);
            double tempoCompletamento = nodo.calcolaTempoCompletamento(flusso);
            System.out.println("Preferenza " + (i+1) + ": Nodo " + nodo +
                    " - Tempo Completamento: " + tempoCompletamento);
        }

        // Aggiorna la lista di preferenza del flusso
        flusso.listaPreferenzaNodi = new ArrayList<>(preferenze);

        return preferenze;
    }

    //metodo che controlla se tutti i flussi sono stati elaborati dai nodi IPN, ovvero se ogni utente ha esaurito i suoi flussi
    public static boolean tuttiFlussiElaborati(List<Utente> utenti) {
        for (Utente utente : utenti) {
            if (!utente.getFlussi().isEmpty()) {
                return false; //se non è vuota allora falso
            }
        }
        return true; //se è vuota allora vero
    }

    // Stampa una riga aggregata con il tempo in coda per ciascun nodo per il flusso indicato
    public static void printAggregatedWaitingTimes(Flusso flusso, List<NodoIPN> nodi) {
        StringBuilder sb = new StringBuilder();
        sb.append("Flusso ").append(flusso.getIdFlusso())
                .append(" dell'Utente ").append(flusso.getIdUtente())
                .append(" - Nuovo tempo in coda ");
        for (NodoIPN nodo : nodi) {
            sb.append(nodo).append(": ").append(flusso.getQ_z_i(nodo)).append(", ");
        }
        if (sb.length() >= 2)
            sb.setLength(sb.length() - 2); // rimuove l'ultima virgola
        System.out.println(sb.toString());
    }

    // Trova l'attuale assegnazione (nodo) di un flusso, se presente.
    private static NodoIPN findCurrentAssignment(Flusso f, List<NodoIPN> nodi) {
        for (NodoIPN nodo : nodi) {
            if (nodo.codaFlussi.contains(f)) {
                return nodo;
            }
        }
        return null;
    }

    // metodo che svuota le code dei nodi non disponibili
    // in modo che possano essere riassegnati nel matching nelle iterazioni successive.
    public static void rimuoviFlussiDaNodiNonDisponibili(List<Utente> utenti, List<NodoIPN> nodi) {

        for (NodoIPN nodo : nodi) {
            if (!nodo.disponibile) {
                if (!nodo.codaFlussi.isEmpty()) {
                    nodo.codaFlussi.clear();
                    System.out.println("Coda del nodo " + nodo + " svuotata.");
                    System.out.println();
                } else {
                    System.out.println("Coda del nodo " + nodo + " vuota.");
                    System.out.println();
                }
            }
        }
    }

    // ALGORITMO DI MATCHING:

    public static void eseguiMatching(List<Utente> utenti, List<NodoIPN> nodi) {

        //FASE DI PREPARAZIONE
        System.out.println("\n--- PREPARAZIONE SIMULAZIONE ---");
        System.out.println();

        // Calcola una sola volta le liste di preferenza E_z_i per tutti i nodi (statica)
        PL_E_z_i(utenti, nodi);
        System.out.println();

        // Per ogni flusso di ogni utente, calcola la PL_V_i_z iniziale (dinamica)
        for (Utente utente : utenti) {
            for (Flusso flusso : new ArrayList<>(utente.getFlussi())) {
                System.out.println();
                System.out.println("Flusso " + flusso.getIdFlusso() + " dell'Utente " + flusso.getIdUtente() + " - R_i: " + flusso.R_i + " p_z_i: " + flusso.p_z_i + " B_i: " + flusso.B_i); // sennò metti non assegnato
                PL_V_i_z(flusso, nodi);
            }
        }

        //FASE DI MATCHING INIZIALE MI ASSOCIO AI NODI CHE HANNO IL FLUSSO NELLA LISTA PL_E_z_i E LO HANNO SICURAMENTE, INIZIALMENTE I TC SONO NULLI PER CUI VA BENE QUALSIASI NODO SECONDO PL_V_i_z.
        System.out.println("\n--- FASE DI MATCHING ---");
        System.out.println();
        // Prima fase: matching iniziale dei flussi ai nodi
        for (Utente utente : utenti) {
            for (Flusso flusso : new ArrayList<>(utente.getFlussi())) {
                NodoIPN currentAssignment = findCurrentAssignment(flusso, nodi);
                boolean changed = false;
                for (NodoIPN nodoPreferito : flusso.listaPreferenzaNodi) {
                    if (nodoPreferito.disponibile && nodoPreferito.codaFlussi.size() < 3 && nodoPreferito.getCapacitaLz() >= flusso.R_i) {
                        if (currentAssignment == null) {
                            // Il flusso non è ancora assegnato: assegnalo e interrompi la ricerca.
                            nodoPreferito.aggiungiFlusso(flusso);
                            System.out.println("Flusso " + flusso.getIdFlusso() + " dell'Utente " + flusso.getIdUtente() +
                                    " assegnato a " + nodoPreferito);
                            currentAssignment = nodoPreferito;
                            changed = true;
                            break;
                        } else {
                            // Il flusso è già assegnato: controlla se il candidato è preferito rispetto all'attuale assegnazione.
                            int posCandidate = flusso.listaPreferenzaNodi.indexOf(nodoPreferito);
                            int posCurrent = flusso.listaPreferenzaNodi.indexOf(currentAssignment);
                            if (posCandidate < posCurrent) {
                                // Il candidato è più preferito: riassegna il flusso.
                                currentAssignment.codaFlussi.remove(flusso);
                                nodoPreferito.aggiungiFlusso(flusso);
                                System.out.println("Flusso " + flusso.getIdFlusso() + " dell'Utente " + flusso.getIdUtente() +
                                        " riassegnato da " + currentAssignment + " a " + nodoPreferito);
                                currentAssignment = nodoPreferito;
                                changed = true;
                                break;
                            }
                        }
                    }
                }
                // Se non c'è stato alcun cambiamento, stampo il nodo attuale (o che non è assegnato)
                if (!changed) {
                    if (currentAssignment != null) {
                        System.out.println("Flusso " + flusso.getIdFlusso() + " dell'Utente " + flusso.getIdUtente()
                                + " rimane assegnato a " + currentAssignment);
                    } else {
                        System.out.println("Flusso " + flusso.getIdFlusso() + " dell'Utente " + flusso.getIdUtente()
                                + " non è assegnato a nessun nodo.");
                    }
                }
            }
        }

        //FASE DI ELABORAZIONE, ALLA CODA DI FLUSSI DEL NODO ELABORO SECONDO PL_E_z_i, QUANDO VADO A RICOLLEGARE QUELLI IN CODA NON ELABORATI GLI COLLEGO SECONDO PL_V_i_z. E VIA COSÌ
        System.out.println("\n--- FASE DI ELABORAZIONE ---");
        for (NodoIPN nodo : nodi) {

            // Se il nodo non è disponibile, salta la sua elaborazione
            if (!nodo.disponibile) {
                System.out.println();
                System.out.println(nodo + " non disponibile, capacità finita, saltato dalla fase di elaborazione.");
                continue;
            }

            System.out.println("\n--ELABORAZIONE al " + nodo + ":" + " Capacità residua: " + nodo.getCapacitaLz());
            System.out.println("Flussi in coda: " + nodo.codaFlussi.size());
            if (nodo.codaFlussi.isEmpty()) {
                System.out.println("Coda nel nodo " + nodo + " vuota!");
                System.out.println("Tempo in coda prima dell'elaborazione: " + nodo.getUltimoTempoCoda());
                System.out.println("Tempo di completamento calcolato: " + nodo.getUltimoTempoCoda());
            } else { // se la coda non fosse vuota
                // Trova il flusso con 1/B_i più piccolo tra quelli in coda
                Flusso flussoInElaborazione = null;
                double minimoProporzionale = Double.MAX_VALUE;
                for (Flusso flussoInCoda : nodo.codaFlussi) {
                    double val = 1.0 / flussoInCoda.B_i;
                    if (val < minimoProporzionale) {
                        minimoProporzionale = val;
                        flussoInElaborazione = flussoInCoda;
                    }
                }
                if(flussoInElaborazione != null){
                    System.out.println("Elaborazione flusso " + flussoInElaborazione.getIdFlusso() + " dell'Utente " +
                            flussoInElaborazione.getIdUtente());
                    // Stampa tempo in coda prima dell'elaborazione
                    System.out.println("Tempo in coda prima dell'elaborazione: " +
                            flussoInElaborazione.getQ_z_i(nodo));
                    // Calcola e memorizza il tempo di completamento per il flusso in elaborazione
                    double tempoCompletamento = nodo.calcolaTempoCompletamento(flussoInElaborazione);
                    nodo.tempoCompletamentoFlussoInElaborazione = tempoCompletamento;

                    if (tempoCompletamento > flussoInElaborazione.B_i) {
                        nodo.elaboratoInRitardo = true;
                    } else {
                        nodo.elaboratoInRitardo = false;
                    }

                    System.out.println("Tempo di completamento calcolato: " + tempoCompletamento);
                    nodo.elaboraFlusso(flussoInElaborazione, utenti);
                    double ritardo = nodo.calcolaRitardo(flussoInElaborazione);
                    if(ritardo > 0){
                        System.out.println(nodo + " - Flusso elaborato con ritardo: " + ritardo);
                    } else {
                        System.out.println(nodo + " - Flusso elaborato senza ritardo");
                    }
                }
            }
        }

        System.out.println("\n--- RIEPILOGO ITERAZIONE ---");
        System.out.println();
    }

    public static void aggiornamentoValoriIterazione(List<Utente> utenti, List<NodoIPN> nodi) {

        //Dopo l'elaborazione, per ogni nodo (che ha processato un flusso) aggiorna il tempo in coda per tutti i flussi rimanenti
        for (NodoIPN nodo : nodi) {
            double newTime;
            if (nodo.tempoCompletamentoFlussoInElaborazione != -1) {
                // Se il nodo ha elaborato un flusso, usiamo il tempo calcolato
                newTime = nodo.tempoCompletamentoFlussoInElaborazione;
                // Aggiorniamo l'attributo ultimoTempoCoda
                nodo.setUltimoTempoCoda(newTime);
                // Resettiamo tempoCompletamentoFlussoInElaborazione per la prossima iterazione
                nodo.tempoCompletamentoFlussoInElaborazione = -1;
            } else {
                // Se il nodo non ha elaborato alcun flusso, usiamo l'ultimo tempo salvato
                newTime = nodo.getUltimoTempoCoda();
            }
            // Aggiorniamo i tempi in coda per tutti i flussi rimanenti, usando newTime (sia che sia nuovo o l'ultimo tempo)
            for (Utente u : utenti) {
                for (Flusso f : u.getFlussi()) {
                    f.setQ_z_i(nodo, newTime);
                }
            }
        }

        //Calcolo le nuove liste di preferenza per i flussi rimasti in coda
        System.out.println("\nAggiornamento liste di preferenza per flussi rimanenti in coda:");
        System.out.println();
        Set<Flusso> flussiRimanenti = new HashSet<>();
        for (Utente u : utenti) {
            flussiRimanenti.addAll(u.getFlussi());
        }
        for (Flusso f : flussiRimanenti) {
            printAggregatedWaitingTimes(f, nodi);
            PL_V_i_z(f, nodi);
            System.out.println();
        }
    }


    public static double calcolaPi(List<Utente> utenti, List<NodoIPN> nodi) {

        int totalFlussiElaborati = 0;
        int flussiInRitardo = 0;

        // Per ogni nodo
        for (NodoIPN nodo : nodi) {
            // Considera solo il flusso elaborato (al massimo uno per nodo)
            if (nodo.tempoCompletamentoFlussoInElaborazione != -1) {
                totalFlussiElaborati++;
                // Se il nodo ha elaborato un flusso, usiamo il flag elaboratoInRitardo
                if (nodo.elaboratoInRitardo) {
                    flussiInRitardo++;
                }
            }
        }

        System.out.println("Valore di flussiInRitardo: " + flussiInRitardo);
        System.out.println("Valore di totalFlussiElaborati: " + totalFlussiElaborati);

        // Evita la divisione per zero
        if (totalFlussiElaborati == 0) {
            return 0.0;
        }

        // Calcola e restituisce la QoS come rapporto tra flussi in ritardo e totale flussi elaborati, e non sul totale dei flussi, ora è più accurato
        return (double) flussiInRitardo / totalFlussiElaborati;
    }


    public static double calcolaUtilita(List<Utente> utenti, List<NodoIPN> nodi) {

        // Calcola A come la somma dei flussi allocati (sia in coda che in elaborazione)
        int A = 0;
        for (NodoIPN nodo : nodi) {
            // Conta i flussi in coda
            A += nodo.codaFlussi.size();
            // Se c'è un flusso in elaborazione (tempo di completamento != -1), aggiungilo al conteggio
            if (nodo.tempoCompletamentoFlussoInElaborazione != -1) {
                A++;
            }
        }
        System.out.println("Valore di A: " + A);

        // Calcola Pi (QoS) come rapporto tra flussi in ritardo e totale flussi
        double Pi = calcolaPi(utenti, nodi);

        storicoPi.add(Pi);

        System.out.println("Valore di Pi: " + Pi);

        // Calcola la somma dei tempi di completamento dei flussi in elaborazione
        double somma = 0;
        for (NodoIPN nodo : nodi) {
            // Se c'è un flusso in elaborazione, aggiungi il suo tempo di completamento
            if (nodo.tempoCompletamentoFlussoInElaborazione != -1) {
                somma += nodo.tempoCompletamentoFlussoInElaborazione;
            }
        }
        System.out.println("Valore di somma: " + somma); //quando aggiorno per tutti i quelli in coda sarà il doppio della somma, perchè ho 3 flussi, uno lo elaboro in tempo x, gli altri due subiscono x di ritardo, per cui nel log vedo 2x se sommo, ma somma è x ed è corretto

        double U = 0;

        if (A == 0){
            U = 0;
        } else if (Pi == 0){
            U = 100;
        } else if (Pi == 1){
            U = (A / (Pi * somma)) * 100;
        } else if (Pi > 0 && Pi < 1){             //funziona bene su tanti flussi in gioco, se ho tipo 1 su 3 in ritardo funziona male.
            U = 100 - (A / (Pi * somma));
        }
        // Assicuriamoci che U sia sempre tra 0 e 100
        U = Math.max(0, Math.min(100, U));

        return U;
    }

    //Se siamo alla prima iterazione, salva lo stato corrente e restituisce false.
    // Nelle iterazioni successive confronta lo stato corrente con quello salvato:
    // se le preferenze o l'assegnamento di un flusso sono cambiati,
    // la stabilità non è ancora raggiunta. Se non ci sono cambiamenti,
    // la stabilità è raggiunta e viene stampato un messaggio con l'iterazione in cui ciò è avvenuto.

    //Se nessun flusso (e quindi nessuna coppia flusso‑nodo) ha interesse a deviare, allora l'allocazione è stabile sul piano bilaterale.
    //Questo significa che non esiste alcuna coppia (flusso, nodo) che possa scambiare o modificare l'assegnamento per migliorare entrambi i lati

    private static int stabilitaRaggiuntagaIterazione = -1; // -1 indica che la stabilità non è ancora stata raggiunta
    private static Map<Flusso, List<NodoIPN>> preferenzePrecedenti = new HashMap<>(); //per memorizzare le preferenze dell'iterazione precedente per ogni flusso
    private static Map<Flusso, NodoIPN> assegnamentoPrecedente = new HashMap<>();

    public static boolean stabilita(List<Flusso> flussi, List<NodoIPN> nodi, int iterazioneCorrente) {
        boolean stabilita = true;

        // Se è la prima iterazione, salva solo lo stato attuale
        if (iterazioneCorrente == 1) {
            for (Flusso flusso : flussi) {
                preferenzePrecedenti.put(flusso, new ArrayList<>(flusso.listaPreferenzaNodi));
                assegnamentoPrecedente.put(flusso, findCurrentAssignment(flusso, nodi));
            }
            return false;
        }

        // Per le iterazioni successive, confronta con lo stato precedente
        for (Flusso flusso : flussi) {
            List<NodoIPN> prefPrecedenti = preferenzePrecedenti.get(flusso);
            NodoIPN assegnamentoPrev = assegnamentoPrecedente.get(flusso);
            NodoIPN assegnamentoAttuale = findCurrentAssignment(flusso, nodi);

            // Verifica se qualcosa è cambiato
            boolean preferencesChanged = !flusso.listaPreferenzaNodi.equals(prefPrecedenti);
            boolean assignmentChanged = !Objects.equals(assegnamentoAttuale, assegnamentoPrev);

            if (preferencesChanged || assignmentChanged) {
                stabilita = false;
                break;
            }
        }

        // Aggiorna lo stato precedente per la prossima iterazione
        preferenzePrecedenti.clear();
        assegnamentoPrecedente.clear();
        for (Flusso flusso : flussi) {
            preferenzePrecedenti.put(flusso, new ArrayList<>(flusso.listaPreferenzaNodi));
            assegnamentoPrecedente.put(flusso, findCurrentAssignment(flusso, nodi));
        }

        // Se abbiamo raggiunto la stabilità per la prima volta, salviamo l'iterazione
        if (stabilita && stabilitaRaggiuntagaIterazione == -1) {
            stabilitaRaggiuntagaIterazione = iterazioneCorrente;
            System.out.println("\n=== STABILITÀ RAGGIUNTA ALL'ITERAZIONE " + iterazioneCorrente + " ===");
        }

        return stabilita;
    }

    // Metodo per resettare lo stato della stabilità, per ogni simulazione che eseguo
    public static void resetStabilita() {
        stabilitaRaggiuntagaIterazione = -1;
        preferenzePrecedenti.clear();
        assegnamentoPrecedente.clear();
    }


//La "stabilità di scambio strettamente bilaterale" è un concetto usato nella teoria degli scambi e negli algoritmi di matching. In pratica, un'allocazione (o matching)
// è definita stabile in senso strettamente bilaterale se non esiste alcuna coppia di agenti, non abbinati tra loro nell'allocazione corrente, che possano scambiarsi risorse o modificare
// il loro abbinamento in modo che entrambi migliorino la loro situazione.
//In altre parole, non dovrebbe esserci nessuna coppia di soggetti che, pur non essendo attualmente associati, possano concordare uno scambio che li porterebbe a ottenere
// un risultato migliore rispetto a quello che hanno. Questo requisito garantisce che nessuna deviazione bilaterale (cioè un cambiamento concordato tra due agenti) possa rendere entrambi
// i partecipanti più soddisfatti, mantenendo così l'allocazione stabile.
//Questa forma di stabilità è "strettamente bilaterale" perché si focalizza esclusivamente su possibili deviazioni coinvolgenti solo due agenti, senza considerare scambi o coalizioni più ampie.



//METODI PER I GRAFICI:

// valuto quante volte su tutte le iterazioni ho processato aleno un flusso in ritardo
// quindi quante volte Pi assume valore > di zero.
    private static List<Double> storicoPi = new ArrayList<>();

    public static double ProbabilitaQoS_Negata() {
         if (storicoPi.isEmpty()) {
             return 0.0; // Nessuna iterazione ancora eseguita
         }
         int count = 0;
         for (double pi : storicoPi) {
            if (pi > 0) {
            count++;
             }
         }

        return (double) count / storicoPi.size();
    }


    //valor medio delle scadenze
    public static double ValorMedio_Bi(List<Utente> utenti) {
        int somma = 0;
        int count = 0;
        for (Utente u : utenti) {
            for (Flusso f : u.getFlussi()) {
                somma += f.B_i;
                count++;
            }
        }
        return (double) somma / count;
    }

    //valor medio del tempo di elaborazione
    public static double ValorMedio_p_z_i(List<Utente> utenti) {
        int somma = 0;
        int count = 0;
        for (Utente u : utenti) {
            for (Flusso f : u.getFlussi()) {
                somma += f.p_z_i;
                count++;
            }
        }
        return (double) somma / count;
    }

    //valor medio del costo dei flussi
    public static double ValorMedio_Ri(List<Utente> utenti) {
        int somma = 0;
        int count = 0;
        for (Utente u : utenti) {
            for (Flusso f : u.getFlussi()) {
                somma += f.R_i;
                count++;
            }
        }
        return (double) somma / count;
    }

    //ottenere il valore dell'utilità alla fine della simulazione
    private static double UtilitàFinale = 0.0;

    public static void setUtilitàFinale(double utilità) {
        UtilitàFinale = utilità;
    }

    public static void resetUtilitàFinale() {
        UtilitàFinale = 0.0;
    }

    //metodo per contare quanti flussi abbiamo in totale nella simulazione
    public static int contaTotaleFlussi(List<Utente> utenti) {
        int totale = 0;
        for (Utente utente : utenti) {
            totale += utente.getFlussi().size();
        }
        return totale;
    }

    //metodo per calcolare quanti IPN ho nella simulazione
    public static int contaTotaleIPN(List<NodoIPN> nodi) {
        return nodi.size();
    }

}
