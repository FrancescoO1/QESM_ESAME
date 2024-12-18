package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchingGame {   // costruttore
    private List<Flusso> flussi;
    private List<NodoIPN> nodiIPN;
    private Map<NodoIPN, List<Flusso>> preferenceListNodo;  // Mappa degli assegnamenti IPN -> Flussi ovvero la preference list, deve essere fissa
    private Map<Flusso, List<NodoIPN>> preferenceListFlusso; //deve variare ad ogni iterazione perchè si basa sull'externalities q_z_i

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

    private double calcolaLatenzaRete() {
        return Math.random() * 0.5;
    }


    //il parametro pi che esprime il rapporto tra il numero D_i e il numero dei flussi totali che ho, la funzione che fa ciò è la seguente:
    public double calcolaPi() {
        double D_i = 0;
        for (Flusso flusso : flussi) {
            D_i += flusso.getD_i();
        }
        return D_i / flussi.size();
    }

    //ora devo calcolare l'utilità sapendo che è una misura  della capacità del sistema di soddisfare le richieste dei task i di essere preelaborati, rispettando le deadline B_i
    //rispettando la QoS. la formula dell'utilità é U=(pi. (sommatoria sui nodi sommatoria sugli ipn C_i,d,z * alpha_i,z)/ A)^-1.  dove alpha_i,z  è l'elemento generico della matrice
    //di allocazione A, alpha_i,z=1 se il task i è allocato all'ipn Z, altrimenti vale 0. il codice che esprime quindi l'utilità basata sulla formula apena scritta è

    public double calcolaUtilita() {
        double utilita = 0.0;
        double pi = calcolaPi();
        double somma = 0.0;
        double alpha_i_z = 0.0;
        double A = flussi.size();
        for (Flusso flusso : flussi) {
            for (NodoIPN nodo : nodiIPN) {
                double latenzaRete = calcolaLatenzaRete();
                double C_i_d_z = nodo.calcolaC_i_z_d(flusso, latenzaRete);
                alpha_i_z = (preferenceListNodo.get(nodo).contains(flusso)) ? 1 : 0;
                somma += C_i_d_z * alpha_i_z;
            }
        }
        utilita = (pi * somma / A);
        return 1 / utilita; // faccio il ^-1.
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

    //ora definisco le funzioni di utilità

    //la funzione di utilita del task i nell'essere abbinato all'ipn z é V_i_z = C_i_d_z, per cui la preference list dei flusso ovvero del task i,
    //è una lista in cui si ordina in modo crescente gli ipn z in base alla metrica V_i_z, ovvero il tempo di completamento del task i nell'essere assegnato all'ipn z.
    //il numero di task i allocati allocati all'ipn z cambia durante l'esecuzione dell'algoritmo a causa dell'aumento del tempo di attesa in coda q_z_i.
    // quindi la preference list deve essere aggiornata ad ogni iterazione dell'algoritmo di matching, ovvero dopo ogni assegnazione. il codice che fa questo è:
    public void aggiornaPreferenceListFlusso() {
        for (Flusso flusso : flussi) {
            List<NodoIPN> preferenceList = new ArrayList<>();
            for (NodoIPN nodo : nodiIPN) {
                preferenceList.add(nodo);
            }
            preferenceList.sort((nodo1, nodo2) -> {
                double latenzaRete = calcolaLatenzaRete();
                double C_i_d_z1 = nodo1.calcolaC_i_z_d(flusso, latenzaRete);
                double C_i_d_z2 = nodo2.calcolaC_i_z_d(flusso, latenzaRete);
                return Double.compare(C_i_d_z1, C_i_d_z2);
            });
            preferenceListFlusso.put(flusso, preferenceList);
        }
    }

    //la funzione di utilita dell'ipn z nell'essere abbinato al task i è E_z_i= 1/B_i, ogni ipn z ha una preference list di task i ordinati in modo decrescente in base alla metrica
    //E_z_i. per cui l'ipnz deve dare precedenza ai task i con scadenze ravvicinate, ovvero che hanno u E_z_i alto, perchè si cerca di minimizzare T_i e massimizzare  utilita.
    //le reference list si fanno una volta e non si aggiornano più a differenza di preferenceListFlusso. il codice che fa ciò è:
    public void aggiornaPreferenceListNodo() {
        for (NodoIPN nodo : nodiIPN) {
            List<Flusso> preferenceList = new ArrayList<>();
            for (Flusso flusso : flussi) {
                preferenceList.add(flusso);
            }
            preferenceList.sort((flusso1, flusso2) -> {
                return Double.compare(1 / flusso1.getB_i(), 1 / flusso2.getB_i());
            });
            preferenceListNodo.put(nodo, preferenceList);
        }
    }

    //l'algoritmo di matching che deve essere, che deve essere definito nella funzione eseguiMatching(), ha questo pseudocodice:
    // for each flusso i in flussi do:
    //     costruisci la preference list secondo V_i_z
    // for each IPN z in nodiIPN do:
    //     costruisci la preference list secondo E_z_i
    //for each flusso i do:
    //     invia una proposta al suo IPN preferito z* contenuto nella sua preference list
    //for each IPN z ricevente almeno una proposta do:
    //     accetta la richiesta preferita i* tra quelle ricevute
    //     poi in accordo con E_z_i rifiuta le altre richieste
    //la funzione eseguiMatching() è la seguente che implementa questo pseudocodice:
    public void eseguiMatching() {
        aggiornaPreferenceListFlusso();
        aggiornaPreferenceListNodo();
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
                //flusso.calcolaTempoCompletamento(calcolaLatenzaRete());
                double latenzaRete = calcolaLatenzaRete();
                double C_i_d_z = migliorNodo.calcolaC_i_z_d(flusso, latenzaRete);
                flusso.calcolaT_i(C_i_d_z);
                System.out.println("T_i del flusso " + flusso.getId() + ": " + flusso.getT_i());
                preferenceListNodo.get(migliorNodo).add(flusso);  // Aggiungi il flusso all'IPN selezionato
            }
        }
    }

    public NodoIPN eseguiMatchingPasso(Flusso flusso) {
        NodoIPN migliorNodo = null;
        double migliorTempoCompletamento = Double.MAX_VALUE;

        for (NodoIPN nodo : nodiIPN) {
            double latenzaRete = calcolaLatenzaRete();
            double tempoCompletamento = nodo.calcolaC_i_z_d(flusso, latenzaRete);

            if (tempoCompletamento < migliorTempoCompletamento) {
                migliorTempoCompletamento = tempoCompletamento;
                migliorNodo = nodo;
            }
        }

        if (migliorNodo != null) {
            migliorNodo.CalcolaP_i_d_z(flusso);
        }

        return migliorNodo; // Restituisce il nodo assegnato
    }

    public Map<Flusso, List<NodoIPN>> getPreferenceListFlusso() {
        return preferenceListFlusso;
    }

    public Map<NodoIPN, List<Flusso>> getPreferenceListNodo() {
        return preferenceListNodo;
    }

}
