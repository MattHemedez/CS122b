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

import java.io.Serializable;
import java.util.*;


public class MovielistActivity extends AppCompatActivity{


    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movelist_page);

        String passedArg = getIntent().getExtras().getString("json");

        final ArrayList<Movie> movie = new ArrayList<>();

        try{
            JSONObject jsonObj = new JSONObject(passedArg.toString());
            String title = "";
            int year = 0;

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


        listView = (ListView) findViewById(R.id.list);


        final MovieArrayAdapter adapter = new MovieArrayAdapter(this, movie);
        ListView listView = (ListView)findViewById(R.id.list);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie m = movie.get(position);
                HashMap<String,String> movieInfo = new HashMap<String,String>();

                movieInfo.put("title", m.getTitle());
                movieInfo.put("year", Integer.toString(m.getYear()));
                movieInfo.put("director", m.getDirector());
                movieInfo.put("genres", m.getGenres());
                movieInfo.put("stars", m.getStars());

                Intent goToIntent = new Intent(MovielistActivity.this, SingleMovieActivity.class);
                goToIntent.putExtra("hashmap", movieInfo);
                startActivity(goToIntent);



//                String message = String.format("Clicked on position: %d, name: %s, %d", position, m.getTitle(), m.getYear());
//                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });


    }
}
