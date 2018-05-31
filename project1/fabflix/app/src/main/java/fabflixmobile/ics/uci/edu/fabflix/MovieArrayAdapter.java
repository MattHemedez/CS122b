package fabflixmobile.ics.uci.edu.fabflix;
import java.util.*;
import android.content.Context;
import android.widget.ArrayAdapter;



public class MovieArrayAdapter extends ArrayAdapter<String> {

    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

    public MovieArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
        super(context, textViewResourceId, objects);

        for (int i = 0; i < objects.size(); ++i) {
            mIdMap.put(objects.get(i), i);
        }
    }

    @Override
    public long getItemId(int position) {
        String item = getItem(position);
        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}


