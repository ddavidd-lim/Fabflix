package Entities;

public class Genre {
    private int genreId;
    private String name;

    public Genre(int genreId, String name) {
        this.genreId = genreId;
        this.name = name;
    }

    public int getGenreId() {
        return genreId;
    }

    public String getName() {
        return name;
    }
}
