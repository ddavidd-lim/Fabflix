package Entities;

import java.util.ArrayList;

public class Movie {
    private String movieId;
    private String title;
    private int year;
    private String director;

    private ArrayList<Star> stars;
    private ArrayList<Genre> genres;
    private float rating;
    private int ratingVotes;

    public Movie(){
        stars = new ArrayList<>();
        genres = new ArrayList<>();
    }
    public String getMovieId(){
        return movieId;
    }

    public String getTitle(){
        return title;
    }

    public int getYear(){
        return year;
    }

    public String getDirector(){
        return director;
    }

    public ArrayList<Star> getStars(){
        return stars;
    }

    public ArrayList<Genre> getGenres(){
        return genres;
    }

    public float getRating(){
        return rating;
    }

    public int getRatingVotes(){
        return ratingVotes;
    }

    public void addStar(Star s){
        stars.add(s);
    }

    public void addGenre(Genre g){
        genres.add(g);
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public void setRating(float r){
        this.rating = r;
    }

    public void setRatingVotes(int v){
        this.ratingVotes = v;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Movie Details - ");
        sb.append("Title:" + getTitle());
        sb.append(", ");
        sb.append("year:" + getYear());
        sb.append(", ");
        sb.append("Director:" + getDirector());
        sb.append(".");

        return sb.toString();
    }
}
