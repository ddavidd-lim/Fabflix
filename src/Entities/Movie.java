package Entities;

import java.util.ArrayList;

public class Movie {
    private final String movieId;
    private final String title;
    private final int year;
    private final String director;

    private ArrayList<Star> stars;
    private ArrayList<Genre> genres;
    private float rating;
    private int ratingVotes;

    public Movie(String movieId, String title, int year, String director){
        this.movieId = movieId;
        this.title = title;
        this.year = year;
        this.director = director;
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

    public void setRating(float r){
        this.rating = r;
    }

    public void setRatingVotes(int v){
        this.ratingVotes = v;
    }
}
