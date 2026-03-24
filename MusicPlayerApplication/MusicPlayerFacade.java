package MusicPlayerApplication;

import MusicPlayerApplication.core.AudioEngine;
import MusicPlayerApplication.core.AudioEngine.PlayerState;
import MusicPlayerApplication.models.Playlist;
import MusicPlayerApplication.models.Song;
import MusicPlayerApplication.models.Artist;
import MusicPlayerApplication.strategies.PlayStrategy;
import MusicPlayerApplication.enums.DeviceType;
import MusicPlayerApplication.enums.PlayStrategyType;
import MusicPlayerApplication.managers.DeviceManager;
import MusicPlayerApplication.managers.PlaylistManager;
import MusicPlayerApplication.managers.StrategyManager;
import MusicPlayerApplication.managers.ArtistRegistry;
import MusicPlayerApplication.observer.IArtistObserver;
import MusicPlayerApplication.service.RecommendationService;
import java.util.ArrayList;
import java.util.List;
import MusicPlayerApplication.device.IAudioOutputDevice;

public class MusicPlayerFacade {
    private static MusicPlayerFacade instance = null;
    private AudioEngine audioEngine;
    private Playlist loadedPlaylist;
    private PlayStrategy playStrategy;
    private RecommendationService recommendationService;

    private MusicPlayerFacade() {
        loadedPlaylist = null;
        playStrategy = null;
        audioEngine = new AudioEngine();
        recommendationService = RecommendationService.getInstance();
    }

    public static synchronized MusicPlayerFacade getInstance() {
        if (instance == null) {
            instance = new MusicPlayerFacade();
        }
        return instance;
    }

    public void connectDevice(DeviceType deviceType) {
        DeviceManager.getInstance().connect(deviceType);
    }

    public void setPlayStrategy(PlayStrategyType strategyType) {
        playStrategy = StrategyManager.getInstance().getStrategy(strategyType);
    }

    public void loadPlaylist(String name) {
        loadedPlaylist = PlaylistManager.getInstance().getPlaylist(name);
        if (playStrategy == null) {
            throw new RuntimeException("Play strategy not set before loading.");
        }
        playStrategy.setPlaylist(loadedPlaylist);
    }

    public void playSong(Song song) {
        if (!DeviceManager.getInstance().hasOutputDevice()) {
            throw new RuntimeException("No audio device connected.");
        }
        IAudioOutputDevice device = DeviceManager.getInstance().getOutputDevice();
        audioEngine.play(device, song);
        recommendationService.recordPlay(song);
    }

    public PlayerState getPlayerState() {
        return audioEngine.getPlayerState();
    }

    public void pauseSong(Song song) {
        if (!audioEngine.getCurrentSongTitle().equals(song.getTitle())) {
            throw new RuntimeException("Cannot pause \"" + song.getTitle() + "\"; not currently playing.");
        }
        audioEngine.pause();
        fetchAndApplyRecommendations();
    }

    public void playAllTracks() {
        if (loadedPlaylist == null) {
            throw new RuntimeException("No playlist loaded.");
        }
        while (playStrategy.hasNext()) {
            Song nextSong = playStrategy.next();
            IAudioOutputDevice device = DeviceManager.getInstance().getOutputDevice();
            audioEngine.play(device, nextSong);
        }
        System.out.println("Completed playlist: " + loadedPlaylist.getPlaylistName());
        fetchAndApplyRecommendations();
    }

    private void fetchAndApplyRecommendations() {
        if (loadedPlaylist == null) return;

        List<Song> candidates = new ArrayList<>(loadedPlaylist.getSongs());
        List<Song> recs = recommendationService.recommend(candidates);

        if (recs == null || recs.isEmpty()) {
            System.out.println("No recommendations available.");
            return;
        }

        System.out.println("Recommended songs:");
        for (Song s : recs) {
            System.out.println(" - " + s.getTitle());
            loadedPlaylist.addSongToPlaylist(s);
            if (playStrategy != null) playStrategy.addToNext(s);
        }
    }

    public void playNextTrack() {
        if (loadedPlaylist == null) {
            throw new RuntimeException("No playlist loaded.");
        }
        if (playStrategy.hasNext()) {
            Song nextSong = playStrategy.next();
            IAudioOutputDevice device = DeviceManager.getInstance().getOutputDevice();
            audioEngine.play(device, nextSong);
        } else {
            System.out.println("Completed playlist: " + loadedPlaylist.getPlaylistName());
        }
    }

    public void playPreviousTrack() {
        if (loadedPlaylist == null) {
            throw new RuntimeException("No playlist loaded.");
        }
        if (playStrategy.hasPrevious()) {
            Song prevSong = playStrategy.previous();
            IAudioOutputDevice device = DeviceManager.getInstance().getOutputDevice();
            audioEngine.play(device, prevSong);
        } else {
            System.out.println("Completed playlist: " + loadedPlaylist.getPlaylistName());
        }
    }

    public void enqueueNext(Song song) {
        playStrategy.addToNext(song);
    }

    public void registerArtist(Artist artist) {
        ArtistRegistry.getInstance().registerArtist(artist);
    }

    public void followArtist(String artistName, IArtistObserver observer) {
        var artistOpt = ArtistRegistry.getInstance().findArtist(artistName);
        if (artistOpt.isPresent()) {
            Artist artist = artistOpt.get();
            artist.addObserver(observer);
            System.out.println("[Follow] " + observer + " followed successfully.");
        } else {
            System.out.println("[Follow] Artist \"" + artistName + "\" not found.");
        }
    }

    public void unfollowArtist(String artistName, IArtistObserver observer) {
        var artistOpt = ArtistRegistry.getInstance().findArtist(artistName);
        if (artistOpt.isPresent()) {
            Artist artist = artistOpt.get();
            artist.removeObserver(observer);
        }
    }

    public void artistReleasedSong(String artistName, Song song) {
        var artistOpt = ArtistRegistry.getInstance().findArtist(artistName);
        if (artistOpt.isPresent()) {
            Artist artist = artistOpt.get();
            artist.releaseNewSong(song);
        } else {
            System.out.println("[Release] Artist \"" + artistName + "\" not found.");
        }
    }

    public List<Song> getRecommendations(int count) {
        return recommendationService.recommend(count);
    }

    public List<Song> getRecommendationsByArtist(String artistName, int count) {
        return recommendationService.recommendByArtist(artistName, count);
    }
}
