package fabflixmobile.ics.uci.edu.fabflix;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


public class IndexActivity extends AppCompatActivity{

    // UI references.
    private EditText mSearchMovieView;
    private Button mMovieSubmitButton;

    // Executes when this app page is displayed
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        // Binds variables to ui elements
        mSearchMovieView = (EditText) findViewById(R.id.search_movie_field);
        mMovieSubmitButton = (Button) findViewById(R.id.search_submit_button);

        // Each time the user presses enter then execute attemptSearch()
        // DOESN'T WORK!!!
        mSearchMovieView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptSearch();
                    return true;
                }
                return false;
            }
        });

        mMovieSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSearch();
            }
        });
    }

    /*
    When the users initiates a search redirect only if there are search results
     */
    private void attemptSearch()
    {
        String movieTitle = mSearchMovieView.getText().toString();

        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        final StringRequest loginRequest = new StringRequest(Request.Method.GET, "https://18.188.218.0:8443/project1/MobileServlet?title=" + movieTitle,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response", response);

                        if(response.contains("Success")){ // if username exist change the page
                            Intent goToIntent = new Intent(IndexActivity.this, MovielistActivity.class);
                            goToIntent.putExtra("json", response);
                            startActivity(goToIntent);
//                            ((TextView) findViewById(R.id.response_back)).setText("HELLO");
                        }else{
                            ((TextView) findViewById(R.id.response_back)).setText("Sorry, the movies that that you searched for are not in our databases.");
                        }



                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("security.error", error.toString());
                    }
                }
        );
        queue.add(loginRequest);
    }
}
