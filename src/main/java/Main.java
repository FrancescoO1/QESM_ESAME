import core.Flusso;
import core.MatchingGame;
import core.NodoIPN;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Creazione di alcuni flussi con scadenze e tempi di elaborazione
        List<Flusso> flussi = new ArrayList<>();
        flussi.add(new Flusso(1, 2.0, 0.5));
        flussi.add(new Flusso(2, 1.5, 0.6));
        flussi.add(new Flusso(3, 1.8, 0.4));

        // Creazione di alcuni IPN con capacità disponibili
        List<NodoIPN> nodiIPN = new ArrayList<>();
        nodiIPN.add(new NodoIPN(1, 2.0));
        nodiIPN.add(new NodoIPN(2, 1.5));

        // Creazione del gioco di matching e assegnazione dei flussi agli IPN
        MatchingGame matchingGame = new MatchingGame(flussi, nodiIPN);
        matchingGame.eseguiMatching();

        // Calcolo e visualizzazione dell'utilità del sistema
        double utilita = matchingGame.calcolaUtilita();
        System.out.println("Utilità del sistema: " + utilita);
    }
}




