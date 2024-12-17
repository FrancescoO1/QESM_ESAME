import core.Flusso;
import core.IPNInterface;
import core.MatchingGame;
import core.NodoIPN;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            List<Flusso> flussi = new ArrayList<>();
            flussi.add(new Flusso(1, 2.0, 0.5));
            flussi.add(new Flusso(2, 1.5, 0.6));
            flussi.add(new Flusso(3, 1.8, 0.4));

            List<NodoIPN> nodiIPN = new ArrayList<>();
            nodiIPN.add(new NodoIPN(1, 2.0));
            nodiIPN.add(new NodoIPN(2, 1.5));

            MatchingGame matchingGame = new MatchingGame(flussi, nodiIPN);
            new IPNInterface(matchingGame);
        });
    }
}



