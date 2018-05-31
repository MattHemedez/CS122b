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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;


public class MovielistActivity extends AppCompatActivity{


    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movelist_page);

        String passedArg = getIntent().getExtras().getString("json");

        try{
            JSONObject jsonObj = new JSONObject(passedArg.toString());
            Iterator keys = jsonObj.keys();

            while (keys.hasNext()) {
                Object key = keys.next();
                JSONObject value = jsonObj.getJSONObject((String) key);
                String component = value.getString("component");
                System.out.println(component);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }






            listView = (ListView) findViewById(R.id.list);

        final ArrayList<Movie> movie = new ArrayList<>();
        movie.add(new Movie("Peter Anteater", 1965));
        movie.add(new Movie("John Doe", 1975));


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
}
