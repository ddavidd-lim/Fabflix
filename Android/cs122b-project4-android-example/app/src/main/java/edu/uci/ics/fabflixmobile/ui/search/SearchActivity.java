package edu.uci.ics.fabflixmobile.ui.search;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
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
    private final String host = "10.0.2.2";
    private final String port = "8080";
    private final String domain = "Fabflix_war";
    private final String baseURL = "http://" + host + ":" + port + "/" + domain;

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
                Request.Method.POST,
                baseURL + "/api/results",
                response -> {
                    // TODO: should parse the json response to redirect to appropriate functions
                    //  upon different response value.
                    JSONObject JSONresponse = null;
                    String status = null;
                    try {
                        JSONresponse = new JSONObject(response);
//                        Log.d("check Json", JSONresponse.toString());
                        status = JSONresponse.getString("status");
                    } catch (JSONException e) {
                        message.setText("How did this happen");
                        e.printStackTrace();
                    }
                    if (status.equals("success")){
                        message.setText("Search Success");
                        Log.d("search.success", response);
                        //Complete and destroy login activity once successful
                        finish();
                        // initialize the activity(page)/destination
                        Intent MovieListPage = new Intent(SearchActivity.this, MovieListActivity.class);
                        // activate the list page.
                        startActivity(MovieListPage);
                    }
                    else{
                        message.setText("Invalid Login Credentials");
                    }
                },
                error -> {
                    // error
                    Log.d("login.error", error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                // POST request form data
                final Map<String, String> params = new HashMap<>();
                params.put("movietitle", movietitle.getText().toString());
                return params;
            }
        };
        // important: queue.add is where the login request is actually sent
        queue.add(searchRequest);
    }
}

