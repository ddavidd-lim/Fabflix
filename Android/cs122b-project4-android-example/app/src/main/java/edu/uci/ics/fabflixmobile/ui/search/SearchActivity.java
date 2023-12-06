package edu.uci.ics.fabflixmobile.ui.search;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.databinding.ActivitySearchBinding;
import edu.uci.ics.fabflixmobile.ui.login.LoginActivity;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    private EditText movietitle;
    private TextView message;

    /*
      In Android, localhost is the address of the device or the emulator.
      To connect to your machine, you need to use the below IP address
     */
    private final String host = "54.241.114.175";
    private final String port = "8443";
    private final String domain = "Fabflix";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivitySearchBinding binding = ActivitySearchBinding.inflate(getLayoutInflater());
        // upon creation, inflate and initialize the layout
        setContentView(binding.getRoot());

        movietitle = binding.movietitle;
        message = binding.message;
        final Button searchButton = binding.search;

        //assign a listener to call a function to handle the user request when clicking a button
        searchButton.setOnClickListener(view -> search());
    }

    @SuppressLint("SetTextI18n")
    public void search() {
        message.setText("Searching for Title");
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // request type is POST
        final StringRequest searchRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/api/results?movietitle=" + movietitle.getText().toString(),
                response -> {
                    // TODO: pass the response(JSONarray of JSONobjects as string) and pass it to
                    //  MovieListActivity using putExtra
                    //  [{movie_id:, movie_title:, movie_director:, movie_year:, movie_rating:,
                    //     top3stars:[s1,s2,s3], genres:[g1,g2,g3]}, {..}]
                        message.setText("Search Success");
                        Log.d("search.success", response);

                        // initialize the activity(page)/destination
                        Intent MovieListPage = new Intent(SearchActivity.this, MovieListActivity.class);
                        MovieListPage.putExtra("movies", response);
                        MovieListPage.putExtra("searchQuery", movietitle.getText().toString());
                        // activate the list page.
                        startActivity(MovieListPage);

                },
                error -> {
                    // error
                    Log.d("search.error", error.toString());
                }) {
//            @Override
//            protected Map<String, String> getParams() {
//                // POST request form data
//                final Map<String, String> params = new HashMap<>();
//                params.put("movietitle", movietitle.getText().toString());
//                return params;
//            }
        };
        searchRequest.setRetryPolicy(new DefaultRetryPolicy(120000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // important: queue.add is where the login request is actually sent
        queue.add(searchRequest);
    }
}

