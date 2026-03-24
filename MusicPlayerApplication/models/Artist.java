package MusicPlayerApplication.models;

import MusicPlayerApplication.observer.IArtistObserver;
import java.util.*;

public class Artist {
    private final String name;
    private final String genre;
    private final List<Song> discography;
    private final List<IArtistObserver> observers;

    public Artist(String name, String genre) {
        this.name = name;
        this.genre = genre;
        this.discography = new ArrayList<>();
        this.observers = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getGenre() {
        return genre;
    }

    public List<Song> getDiscography() {
        return Collections.unmodifiableList(discography);
    }

    public void addObserver(IArtistObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(IArtistObserver observer) {
        observers.remove(observer);
    }

    public void releaseNewSong(Song song) {
        discography.add(song);
        System.out.println("[Artist] " + name + " released a new song: \"" + song.getTitle() + "\"");
        notifyObservers(song);
    }

    private void notifyObservers(Song song) {
        for (IArtistObserver observer : observers) {
            observer.onNewSongReleased(this, song);
        }
    }
}
