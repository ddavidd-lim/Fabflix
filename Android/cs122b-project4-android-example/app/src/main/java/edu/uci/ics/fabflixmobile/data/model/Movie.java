package edu.uci.ics.fabflixmobile.data.model;

/**
 * Movie class that captures movie information for movies retrieved from MovieListActivity
 */
import java.util.ArrayList;

public class Movie {
    private String movieId;
    private String title;
    private int year;
    private String director;

    private ArrayList<String> top3stars;
    private ArrayList<String> top3genres;
    private String rating;
    public Movie(String movieID, String title, String director, int year, ArrayList<String> top3stars
        , ArrayList<String> top3genres, String rating){
        this.movieId = movieID;
        this.title = title;
        this.director = director;
        this.year = year;
        this.rating = rating;
        this.top3stars = top3stars;
        this.top3genres = top3genres;
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

    public ArrayList<String> getStars(){
        return top3stars;
    }

    public ArrayList<String> getGenres(){
        return top3genres;
    }

    public String getRating(){
        return rating;
    }

    public void addStar(String s){
        top3stars.add(s);
    }

    public void addGenre(String g){
        top3genres.add(g);
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

    public void setRating(String r){
        this.rating = r;
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