package MusicPlayerApplication.observer;

import MusicPlayerApplication.models.Artist;
import MusicPlayerApplication.models.Song;

public interface IArtistObserver {
    void onNewSongReleased(Artist artist, Song song);
}
