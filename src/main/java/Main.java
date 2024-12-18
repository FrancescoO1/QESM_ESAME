import core.Flusso;
import core.IPNInterface;
import core.MatchingGame;
import core.NodoIPN;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Creazione di alcuni flussi con scadenze e tempi di elaborazione
        List<Flusso> flussi = new ArrayList<>();
        flussi.add(new Flusso(1, 2.0, 0.5));
        flussi.add(new Flusso(2, 1.5, 0.6));
        flussi.add(new Flusso(3, 1.8, 0.4));
        flussi.add(new Flusso(4, 2.2, 0.7));
        flussi.add(new Flusso(5, 1.7, 0.3));
        flussi.add(new Flusso(6, 1.9, 0.8));
        flussi.add(new Flusso(7, 2.1, 0.2));
        flussi.add(new Flusso(8, 1.6, 0.9));
        flussi.add(new Flusso(9, 2.3, 0.1));
        flussi.add(new Flusso(10, 1.4, 0.5));

        // Creazione di alcuni nodi IPN con capacità disponibili
        List<NodoIPN> nodiIPN = new ArrayList<>();
        nodiIPN.add(new NodoIPN(1, 2.0));
        nodiIPN.add(new NodoIPN(2, 1.5));
        nodiIPN.add(new NodoIPN(3, 1.8));

        // Creazione del gioco di matching
        MatchingGame matchingGame = new MatchingGame(flussi, nodiIPN);

        // Avvio dell'interfaccia grafica
        new IPNInterface(matchingGame);
    }
}


