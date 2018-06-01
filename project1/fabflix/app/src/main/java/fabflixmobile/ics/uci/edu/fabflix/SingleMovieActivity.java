package fabflixmobile.ics.uci.edu.fabflix;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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

public class SingleMovieActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlemovie);

        HashMap<String,String> passedArg = (HashMap<String, String>) getIntent().getSerializableExtra("hashmap");


        ((TextView) findViewById(R.id.movieName)).setText(passedArg.get("title"));
        ((TextView) findViewById(R.id.year)).setText(passedArg.get("year"));
        ((TextView) findViewById(R.id.director)).setText(passedArg.get("director"));
        ((TextView) findViewById(R.id.stars)).setText(passedArg.get("stars"));
        ((TextView) findViewById(R.id.genres)).setText(passedArg.get("genres"));





    }

}
