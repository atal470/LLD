package player;
import java.util.*;

public interface PlayerState {
    void play(SimplePlayer p);
    void pause(SimplePlayer p);
    void stop(SimplePlayer p);
}
