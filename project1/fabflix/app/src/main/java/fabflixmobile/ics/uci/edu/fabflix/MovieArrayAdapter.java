package fabflixmobile.ics.uci.edu.fabflix;
import java.util.*;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class MovieArrayAdapter extends ArrayAdapter<Movie> {

    private ArrayList<Movie> movies;

    public MovieArrayAdapter(Context context, ArrayList<Movie> movies) {
        super(context, R.layout.activity_movielist_row, movies);

        this.movies = movies;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.activity_movielist_row, parent, false);

        Movie movie = movies.get(position);

        TextView titleView = (TextView)view.findViewById(R.id.title);
        TextView subtitleView = (TextView)view.findViewById(R.id.subtitle);
        TextView directorView = (TextView)view.findViewById(R.id.director);
        TextView genresView = (TextView)view.findViewById(R.id.genres);
        TextView starsView = (TextView)view.findViewById(R.id.stars);

        titleView.setText(movie.getTitle());
        subtitleView.setText(movie.getYear().toString());
        directorView.setText(movie.getDirector());
        genresView.setText(movie.getGenres());
        starsView.setText(movie.getStars());

        return view;
    }

}


