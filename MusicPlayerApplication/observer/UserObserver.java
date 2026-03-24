package observer;

public class UserObserver implements ArtistObserver {
    private String name;
    public UserObserver(String name){ this.name=name; }

    public void follow(Artist a){ a.addObserver(this); }

    public void update(Artist artist, String album){
        System.out.println(name+" notified: "+artist.getName()+" released "+album);
    }
}
