package observer;
import observer.Artist;

public interface ArtistObserver {
    void update(Artist artist, String album);
}
