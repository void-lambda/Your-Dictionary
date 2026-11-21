
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Deck implements Serializable {
    private static final long serialVersionUID = 1L;

    public String name;
    public List<Card> cards;

    public Deck(String name) {
        this.name = name;
        this.cards = new ArrayList<>();
    }
}
