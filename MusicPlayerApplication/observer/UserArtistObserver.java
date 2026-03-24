package MusicPlayerApplication.observer;

import MusicPlayerApplication.models.Artist;
import MusicPlayerApplication.models.Song;

public class UserArtistObserver implements IArtistObserver {
    private final String userName;

    public UserArtistObserver(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public void onNewSongReleased(Artist artist, Song song) {
        System.out.println("[Notification => " + userName + "] " + artist.getName() + " just released \"" + song.getTitle() + "\"");
    }
}
