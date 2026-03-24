package MusicPlayerApplication.core;

import MusicPlayerApplication.models.Song;
import MusicPlayerApplication.device.IAudioOutputDevice;

public class AudioEngine {
    private Song currentSong;
    private boolean songIsPaused;
    private PlayerState state;

    public enum PlayerState {
        PLAYING, PAUSED, STOPPED
    }

    public AudioEngine() {
        currentSong = null;
        songIsPaused = false;
        state = PlayerState.STOPPED;
    }

    public String getCurrentSongTitle() {
        if (currentSong != null) {
            return currentSong.getTitle();
        }
        return "";
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public PlayerState getPlayerState() {
        return state;
    }

    public boolean isPaused() {
        return songIsPaused;
    }

    public void play(IAudioOutputDevice aod, Song song) {
        if (song == null) {
            throw new RuntimeException("Cannot play a null song.");
        }
        
        if (songIsPaused && song == currentSong) {
            songIsPaused = false;
            state = PlayerState.PLAYING;
            System.out.println("Resuming song: " + song.getTitle());
            aod.playAudio(song);
            return;
        }

        currentSong = song;
        songIsPaused = false;
        state = PlayerState.PLAYING;
        System.out.println("Playing song: " + song.getTitle());
        aod.playAudio(song);
    }

    public void pause() {
        if (currentSong == null) {
            throw new RuntimeException("No song is currently playing to pause.");
        }
        if (songIsPaused) {
            throw new RuntimeException("Song is already paused.");
        }
        songIsPaused = true;
        state = PlayerState.PAUSED;
        System.out.println("Pausing song: " + currentSong.getTitle());
    }

    public void stop() {
        currentSong = null;
        songIsPaused = false;
        state = PlayerState.STOPPED;
        System.out.println("Stopped playback.");
    }
}
