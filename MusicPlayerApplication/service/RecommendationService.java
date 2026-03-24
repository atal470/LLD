package MusicPlayerApplication.service;

import MusicPlayerApplication.managers.ArtistRegistry;
import MusicPlayerApplication.models.Artist;
import MusicPlayerApplication.models.Song;
import java.util.*;
import java.util.stream.Collectors;

public class RecommendationService {
    private static RecommendationService instance = null;
    private final List<Song> playHistory;
    private final Map<String, Integer> artistPlayCount;

    private RecommendationService() {
        playHistory = new ArrayList<>();
        artistPlayCount = new HashMap<>();
    }

    public static synchronized RecommendationService getInstance() {
        if (instance == null) {
            instance = new RecommendationService();
        }
        return instance;
    }

    public void recordPlay(Song song) {
        playHistory.add(song);
        artistPlayCount.merge(song.getArtist(), 1, Integer::sum);
    }

    // Legacy method for backward compatibility
    public List<Song> recommend(List<Song> songs) {
        Collections.shuffle(songs);
        return songs.subList(0, Math.min(5, songs.size()));
    }

    public List<Song> recommend(int count) {
        if (artistPlayCount.isEmpty()) {
            System.out.println("[Recommendations] No play history yet.");
            return Collections.emptyList();
        }

        String topArtist = artistPlayCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        System.out.println("[Recommendations] Top artist: " + topArtist + " (" + artistPlayCount.get(topArtist) + " plays)");
        return recommendByArtist(topArtist, count);
    }

    public List<Song> recommendByArtist(String artistName, int count) {
        var artistOpt = ArtistRegistry.getInstance().findArtist(artistName);
        if (artistOpt.isEmpty()) {
            System.out.println("[Recommendations] Artist not found: " + artistName);
            return Collections.emptyList();
        }

        Artist artist = artistOpt.get();
        List<Song> songs = artist.getDiscography().stream()
                .filter(s -> !playHistory.contains(s))
                .limit(count)
                .collect(Collectors.toList());

        if (songs.isEmpty()) {
            System.out.println("[Recommendations] No new songs from " + artistName + ".");
        }
        return songs;
    }

    public List<Song> getPlayHistory() {
        return Collections.unmodifiableList(playHistory);
    }
}
