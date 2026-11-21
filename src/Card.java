
import java.io.Serializable;

public class Card implements Serializable {
    private static final long serialVersionUID = 1L;

    public String front;
    public String back;
    public boolean isLearned;

    public Card(String front, String back) {
        this.front = front;
        this.back = back;
        this.isLearned = false;
    }
}
