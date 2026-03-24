package MusicPlayerApplication.managers;

import MusicPlayerApplication.models.Artist;
import java.util.*;

public class ArtistRegistry {
    private static ArtistRegistry instance = null;
    private final Map<String, Artist> artists;

    private ArtistRegistry() {
        artists = new HashMap<>();
    }

    public static synchronized ArtistRegistry getInstance() {
        if (instance == null) {
            instance = new ArtistRegistry();
        }
        return instance;
    }

    public void registerArtist(Artist artist) {
        if (artist == null) {
            throw new RuntimeException("Cannot register null artist.");
        }
        artists.put(artist.getName(), artist);
    }

    public Optional<Artist> findArtist(String name) {
        return Optional.ofNullable(artists.get(name));
    }

    public Map<String, Artist> getAllArtists() {
        return Collections.unmodifiableMap(artists);
    }
}
