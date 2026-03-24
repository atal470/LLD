package player;

public class PlayingState implements PlayerState {
    public void play(SimplePlayer p){ System.out.println("Already playing"); }
    public void pause(SimplePlayer p){ p.setState(new PausedState()); }
    public void stop(SimplePlayer p){ p.setState(new StoppedState()); }
}
