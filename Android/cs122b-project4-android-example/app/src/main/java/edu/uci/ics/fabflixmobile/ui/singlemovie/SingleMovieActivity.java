package edu.uci.ics.fabflixmobile.ui.singlemovie;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListViewAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class SingleMovieActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_movie);
        // TODO: this should be retrieved from the backend server
        // TODO: put the attributes from the response into the views
        final ArrayList<Movie> movies = new ArrayList<>();
        Intent intent = getIntent();
        if (intent != null) {
            String moviesJSONString = intent.getStringExtra("singlemovie");
            if (moviesJSONString != null) {
                try {
                    JSONArray moviesJSONArray = new JSONArray(moviesJSONString);
                    JSONObject movieObject = moviesJSONArray.getJSONObject(0);
                    String movieId = movieObject.getString("movie_id");
                    String title = movieObject.getString("movie_title");
                    String director = movieObject.getString("movie_director");
                    String year = movieObject.getString("movie_year");
                    String rating = movieObject.getString("movie_rating");
                    String genre = movieObject.getString("genre");
                    ArrayList<String> genreNameArray = new ArrayList<>(Arrays.asList(genre.split(",")));
                    ArrayList<String> starNameArray = new ArrayList<>();

                    for (int i = 0; i < moviesJSONArray.length(); i++) {
                        movieObject = moviesJSONArray.getJSONObject(i);
                        String star = movieObject.getString("star_name");
                        starNameArray.add(star);
                    }

                    StringBuilder stars = new StringBuilder();
                    for (int i = 0; i < starNameArray.size(); i++) {
                        stars.append(starNameArray.get(i)); // Append the actor name

                        // Add a comma if it's not the last actor in the list
                        if (i < starNameArray.size() - 1) {
                            stars.append(", ");
                        }
                    }
                    StringBuilder genres = new StringBuilder();
                    for (int i = 0; i < genreNameArray.size(); i++) {
                        genres.append(genreNameArray.get(i)); // Append the actor name

                        // Add a comma if it's not the last actor in the list
                        if (i < genreNameArray.size() - 1) {
                            genres.append(", ");
                        }
                    }

                    String starsString = stars.toString();
                    String genreString = genres.toString();

                    Log.d("title: ", title);
                    Log.d("year: ", year);
                    Log.d("director: ", director);
                    Log.d("rating: ", rating);
                    Log.d("genres: ", genreString);
                    Log.d("stars: ", starsString);
                    // Find TextViews by their IDs
                    TextView titleTextView = findViewById(R.id.movietitle);
                    TextView yearTextView = findViewById(R.id.movieyear);
                    TextView ratingTextView = findViewById(R.id.movierating);
                    TextView directorTextView = findViewById(R.id.moviedirector);
                    TextView genresTextView = findViewById(R.id.moviegenres);
                    TextView starsTextView = findViewById(R.id.moviestars);

                    // Set retrieved data to respective TextViews
                    titleTextView.setText(title);
                    yearTextView.setText("Year" + year);
                    ratingTextView.setText("Rating: " + rating);
                    directorTextView.setText("Director: " + director);
                    genresTextView.setText("Genres: " + genreString);
                    starsTextView.setText("Stars: " + starsString);
                } catch (JSONException e) {
                    e.printStackTrace();
                    // Handle JSON parsing errors here
                }
            }
        }}
}
