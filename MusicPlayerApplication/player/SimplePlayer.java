package player;
import models.Song;
import java.util.*;

public class SimplePlayer {
    private PlayerState state=new StoppedState();
    private List<Song> queue=new ArrayList<>();
    private int index=0;

    public void load(List<Song> songs){ this.queue=songs; }

    public void clickPlay(){ state.play(this); }
    public void clickPause(){ state.pause(this); }
    public void clickStop(){ state.stop(this); }

    public void play(){
        if(!queue.isEmpty())
            System.out.println("Playing: "+queue.get(index).getTitle());
    }

    public void setState(PlayerState s){ this.state=s; }
}
