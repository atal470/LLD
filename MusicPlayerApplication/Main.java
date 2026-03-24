package MusicPlayerApplication;

import MusicPlayerApplication.enums.DeviceType;
import MusicPlayerApplication.enums.PlayStrategyType;
import MusicPlayerApplication.models.Artist;
import MusicPlayerApplication.models.Song;
import MusicPlayerApplication.observer.UserArtistObserver;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            MusicPlayerApplication application = MusicPlayerApplication.getInstance();
            MusicPlayerFacade player = MusicPlayerFacade.getInstance();

            // Populate library
            application.createSongInLibrary("Kesariya", "Arijit Singh", "/music/kesariya.mp3");
            application.createSongInLibrary("Chaiyya Chaiyya", "Sukhwinder Singh", "/music/chaiyya_chaiyya.mp3");
            application.createSongInLibrary("Tum Hi Ho", "Arijit Singh", "/music/tum_hi_ho.mp3");
            application.createSongInLibrary("Jai Ho", "A. R. Rahman", "/music/jai_ho.mp3");
            application.createSongInLibrary("Zinda", "Siddharth Mahadevan", "/music/zinda.mp3");

            // Create Artist Registry and register artists
            Artist arijit = new Artist("Arijit Singh", "Bollywood");
            Artist sukhwinder = new Artist("Sukhwinder Singh", "Bollywood");
            Artist arrahman = new Artist("A. R. Rahman", "Bollywood");

            player.registerArtist(arijit);
            player.registerArtist(sukhwinder);
            player.registerArtist(arrahman);

            System.out.println("\n-- Artist Follow / Observer --\n");
            UserArtistObserver alice = new UserArtistObserver("Alice");
            UserArtistObserver bob = new UserArtistObserver("Bob");

            player.followArtist("Arijit Singh", alice);
            player.followArtist("Arijit Singh", bob);
            player.followArtist("A. R. Rahman", alice);

            // Create playlist and add songs
            application.createPlaylist("Bollywood Vibes");
            application.addSongToPlaylist("Bollywood Vibes", "Kesariya");
            application.addSongToPlaylist("Bollywood Vibes", "Chaiyya Chaiyya");
            application.addSongToPlaylist("Bollywood Vibes", "Tum Hi Ho");
            application.addSongToPlaylist("Bollywood Vibes", "Jai Ho");

            // Connect device
            application.connectAudioDevice(DeviceType.BLUETOOTH);

            //Play/pause a single song
            System.out.println("\n-- Play / Pause --\n");
            application.playSingleSong("Zinda");
            System.out.println("State: " + player.getPlayerState());
            application.pauseCurrentSong("Zinda");
            System.out.println("State: " + player.getPlayerState());
            application.playSingleSong("Zinda");
            System.out.println("State: " + player.getPlayerState());

            System.out.println("\n-- Sequential --\n");
            application.selectPlayStrategy(PlayStrategyType.SEQUENTIAL);
            application.loadPlaylist("Bollywood Vibes");
            application.playAllTracksInPlaylist();

            System.out.println("\n-- Random --\n");
            application.selectPlayStrategy(PlayStrategyType.RANDOM);
            application.loadPlaylist("Bollywood Vibes");
            application.playAllTracksInPlaylist();

            System.out.println("\n-- Custom Queue Playback --\n");
            application.selectPlayStrategy(PlayStrategyType.CUSTOM_QUEUE);
            application.loadPlaylist("Bollywood Vibes");
            application.queueSongNext("Kesariya");
            application.queueSongNext("Tum Hi Ho");
            application.playAllTracksInPlaylist();

            System.out.println("\n-- Play Previous in Sequential --\n");
            application.selectPlayStrategy(PlayStrategyType.SEQUENTIAL);
            application.loadPlaylist("Bollywood Vibes");
            application.playAllTracksInPlaylist();
            application.playPreviousTrackInPlaylist();
            application.playPreviousTrackInPlaylist();

            // Artist Release Demo
            System.out.println("\n-- Artist Release --\n");
            Song apnaBanaLe = new Song("Apna Bana Le", "Arijit Singh", "/music/apnabanale.mp3");
            player.artistReleasedSong("Arijit Singh", apnaBanaLe);

            Song arziyaan = new Song("Arziyaan", "A. R. Rahman", "/music/arziyaan.mp3");
            player.artistReleasedSong("A. R. Rahman", arziyaan);

            // Recommendations Demo
            System.out.println("\n-- Recommendations --\n");
            List<Song> byArtist = player.getRecommendationsByArtist("Arijit Singh", 3);
            System.out.println("Recommendations for Arijit Singh:");
            byArtist.forEach(s -> System.out.println(" -> " + s.getTitle()));

            List<Song> smart = player.getRecommendations(3);
            System.out.println("Smart recommendations:");
            smart.forEach(s -> System.out.println(" -> " + s.getTitle()));

        } catch (Exception error) {
            System.err.println("Error: " + error.getMessage());
        }
    }
}
