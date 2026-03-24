package observer;

import java.util.*;
import MusicPlayerApplication.managers.PlaylistManager;
import MusicPlayerApplication.models.Song;

public class Artist {
    private String name;
    private List<ArtistObserver> observers=new ArrayList<>();

    public Artist(String name){ this.name=name; }

    public void addObserver(ArtistObserver o){ observers.add(o); }

    public void releaseAlbum(String album){
      
        for(ArtistObserver o:observers){
            o.update(this, album);
        }

        try {
            PlaylistManager.getInstance().createPlaylist("New Releases");
        } catch (RuntimeException ignored) {}

        Song releaseSong = new Song(album, this.name, "");
        PlaylistManager.getInstance().addSongToPlaylist("New Releases", releaseSong);
        System.out.println("Added release '" + album + "' by " + name + " to playlist 'New Releases'.");
    }

    public String getName(){ return name; }
}
