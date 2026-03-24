package player;

public class StoppedState implements PlayerState {
    public void play(SimplePlayer p){ p.setState(new PlayingState()); p.play(); }
    public void pause(SimplePlayer p){ System.out.println("Cannot pause"); }
    public void stop(SimplePlayer p){ System.out.println("Already stopped"); }
}
