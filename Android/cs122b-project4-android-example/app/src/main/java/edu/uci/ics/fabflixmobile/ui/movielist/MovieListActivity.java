package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);
        // TODO: this should be retrieved from the backend server
        // TODO: call get method based on the post from the previous search
        final ArrayList<Movie> movies = new ArrayList<>();
        Intent intent = getIntent();
        if (intent != null) {
            String moviesJSONString = intent.getStringExtra("movies");
            if (moviesJSONString != null) {
                // Process the moviesJSONString (JSON array of JSON objects)
                // For example, you can parse it into a JSONArray or JSONObject.
                try {
                    JSONArray moviesJSONArray = new JSONArray(moviesJSONString);
                    // Now you can work with the JSON array

                    // Example: iterating through the array
                    for (int i = 0; i < moviesJSONArray.length(); i++) {
                        // Get individual JSON objects from the array
                        JSONObject movieObject = moviesJSONArray.getJSONObject(i);

                        // Access properties of each movie object
                        String movieId = movieObject.getString("movie_id");
                        String title = movieObject.getString("movie_title");
                        String director = movieObject.getString("movie_director");
                        int year = movieObject.getInt("movie_year");
                        String rating = movieObject.getString("movie_rating");
                        String top3Stars = movieObject.getString("top3Stars");
                        String top3genres = movieObject.getString("genres");


                        String[] starArray = top3Stars.split(",");
                        ArrayList<String> starIdArray = new ArrayList<>();
                        ArrayList<String> starNameArray = new ArrayList<>();
                        for (String s : starArray) {
                            String[] idName = s.split(":"); // [0] = id, [1] = name
                            if (idName.length >= 2) {
                                starIdArray.add(idName[0]);
                                starNameArray.add(idName[1]);
                            }
                        }

//                        Log.d("genres: ", top3genres);
                        String[] genreArray = top3genres.split(",");
                        ArrayList<String> genreIdArray = new ArrayList<>();
                        ArrayList<String> genreNameArray = new ArrayList<>();
                        for (String s : genreArray) {
                            String[] idName = s.split(":"); // [0] = id, [1] = name
                            if (idName.length >= 2) {
                                genreIdArray.add(idName[0]);
                                genreNameArray.add(idName[1]);
                            }
                        }
                        // Do something with the movie data
                        // For instance, add it to a list, display it, etc.
                        movies.add(new Movie(movieId, title, director, year, starNameArray, genreNameArray, rating));
                        Log.d("movie_id: ", movieId);
                        Log.d("title: ", title);
                        Log.d("director: ", director);
                        Log.d("rating: ", rating);
                        Log.d("top3stars: ", top3Stars);
                        Log.d("top3Genres: ", top3genres);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // Handle JSON parsing errors here
                }
            }
        }
        MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Movie movie = movies.get(position);
            @SuppressLint("DefaultLocale") String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getTitle(), movie.getYear());
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        });
    }
}