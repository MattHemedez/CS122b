package fabflixmobile.ics.uci.edu.fabflix;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;


public class MovielistActivity extends AppCompatActivity{


    ListView listView;
    TextView totalResultsView;
    TextView pageNumView;
    Button nextButton;
    Button prevButton;
    int pageNum;
    String movieSearchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movelist_page);

        String passedArg = getIntent().getExtras().getString("json");

        // Set view variables
        totalResultsView = (TextView) findViewById(R.id.totalResults);
        pageNumView = (TextView) findViewById(R.id.pageNum);
        listView = (ListView) findViewById(R.id.list);
        nextButton = (Button) findViewById(R.id.nextButton);
        prevButton = (Button) findViewById(R.id.prevButton);
        pageNum = 1;
        movieSearchQuery = "";

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSearch(1);
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSearch(-1);
            }
        });


        updatePage(passedArg);


    }

    public void updatePage(String jsonResponse)
    {
        final ArrayList<Movie> movie = new ArrayList<>();
        int totalPages = 1;
        int totalResults = 1;
        try{

            String title = "";
            int year = 0;
            JSONObject jsonObj = new JSONObject(jsonResponse.toString());

            movieSearchQuery = jsonObj.getString("searchQuery");
            pageNum = jsonObj.getInt("pageNum");
            totalPages = jsonObj.getInt("totalPages");
            totalResults = jsonObj.getInt("totalResults");

            JSONArray movieList = jsonObj.getJSONArray("movies");
            for(int i = 0; i < movieList.length(); ++i)
            {
                String director = "";
                String genres = "";
                String stars = "";

                JSONObject jsonMovie = movieList.getJSONObject(i);
                title = jsonMovie.getString("title");
                year = jsonMovie.getInt("year");
                director = jsonMovie.getString("director");

                JSONArray genreList = jsonMovie.getJSONArray("genres");
                for(int j = 0; j < genreList.length(); ++j)
                {
                    JSONObject jsonGenre = genreList.getJSONObject(j);
                    genres += jsonGenre.getString("genreName") + ", ";
                }
                genres = genres.replaceAll(", +$", "");

                JSONArray starList = jsonMovie.getJSONArray("stars");
                for(int j = 0; j < starList.length(); ++j)
                {
                    JSONObject jsonStar = starList.getJSONObject(j);
                    stars += jsonStar.getString("starName") + ", ";
                }
                stars = stars.replaceAll(", +$", "");

                movie.add(new Movie(title, year, director, genres, stars));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        pageNumView.setText("Page " + pageNum + "/" + totalPages);
        totalResultsView.setText("Total Results: " + totalResults);
        if(pageNum == 1)
        {
            prevButton.setClickable(false);
            prevButton.setEnabled(false);
        }
        else
        {
            prevButton.setClickable(true);
            prevButton.setEnabled(true);
        }
        if(pageNum == totalPages)
        {
            nextButton.setClickable(false);
            nextButton.setEnabled(false);
        }
        else
        {
            nextButton.setClickable(true);
            nextButton.setEnabled(true);
        }

        final MovieArrayAdapter adapter = new MovieArrayAdapter(this, movie);
        ListView listView = (ListView)findViewById(R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie m = movie.get(position);
                String message = String.format("Clicked on position: %d, name: %s, %d", position, m.getTitle(), m.getYear());
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void attemptSearch(int nextPage)
    {
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        final StringRequest loginRequest = new StringRequest(Request.Method.GET, "https://18.188.218.0:8443/project1/MobileServlet?title=" + movieSearchQuery + "&pagenum=" + (pageNum + nextPage),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response", response);
                        if(response.contains("Success")){ // if search works
                            updatePage(response);
                        }else{
                            Log.d("Server Response", "Success wasn't found");
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
