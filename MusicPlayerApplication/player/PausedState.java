package player;

public class PausedState implements PlayerState {
    public void play(SimplePlayer p){ p.setState(new PlayingState()); p.play(); }
    public void pause(SimplePlayer p){ System.out.println("Already paused"); }
    public void stop(SimplePlayer p){ p.setState(new StoppedState()); }
}
