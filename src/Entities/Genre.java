package Entities;

public class Genre {
    private int genreId;
    private String name;

    public Genre() {
    }

    public int getGenreId() {
        return genreId;
    }

    public String getName() {
        return name;
    }

    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }

    public void setName(String name) {
        this.name = name;
    }
}
