package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.ui.search.SearchActivity;
import edu.uci.ics.fabflixmobile.ui.singlemovie.SingleMovieActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class MovieListActivity extends AppCompatActivity {
//    private EditText movieid;

    /*
      In Android, localhost is the address of the device or the emulator.
      To connect to your machine, you need to use the below IP address
     */
    private final String host = "10.0.2.2";
    private final String port = "8080";
    private final String domain = "Fabflix_war";
    private final String baseURL = "http://" + host + ":" + port + "/" + domain;
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
                    for (int i = 0; i < moviesJSONArray.length(); i++) {
                        JSONObject movieObject = moviesJSONArray.getJSONObject(i);
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
                        ArrayList<String> genreNameArray = new ArrayList<>(Arrays.asList(top3genres.split(",")));
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
            final RequestQueue queue = NetworkManager.sharedManager(this).queue;
            // request type is POST
            final StringRequest movieRequest = new StringRequest(
                    Request.Method.GET,
                    baseURL + "/api/single-movie?id=" + movie.getMovieId(),
                    response -> {
                        // TODO: pass id to SingleMovieServlet and parse response
                        Log.d("Selection.success", response);

                        // initialize the activity(page)/destination
                        Intent SingleMoviePage = new Intent(MovieListActivity.this, SingleMovieActivity.class);
                        SingleMoviePage.putExtra("singlemovie", response);
                        // activate the list page.
                        startActivity(SingleMoviePage);

                    },
                    error -> {
                        // error
                        Log.d("Selection.error", error.toString());
                    });
            movieRequest.setRetryPolicy(new DefaultRetryPolicy(120000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            // important: queue.add is where the login request is actually sent
            queue.add(movieRequest);
        });
    }
}