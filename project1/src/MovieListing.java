/**
 * This User class only has the username field in this example.
 * <p>
 * However, in the real project, this User class can contain many more things,
 * for example, the user's shopping cart items.
 */
import java.util.ArrayList;
import java.util.TreeSet;
public class MovieListing 
{
    private final String movieId;
    private final String title;
    private final int year;
    private final String director;
    private final double rating;
    private final ArrayList<String> stars;
    private final ArrayList<String> genres;
    
    public MovieListing(String id, String title, int year, String director, double rating, ArrayList<String> stars, ArrayList<String> genres) 
    {
        this.movieId = id;
        this.title = title;
        this.year = year;
        this.director = director;
        this.rating = rating;
        this.stars = removeDuplicates(stars);
        this.genres = removeDuplicates(genres);
    }

    private ArrayList<String> removeDuplicates(ArrayList<String> list)
    {
    	ArrayList<String> result = new ArrayList<String>();
    	TreeSet<String> temp = new TreeSet();
    	for(String item: list)
    	{
    		temp.add(item);
    	}
    	result = new ArrayList<String>(temp);
    	return result;
    }
    
    public String getId() 
    {
        return this.movieId;
    }
    public String getTitle() 
    {
        return this.title;
    }
    public int getYear() 
    {
        return this.year;
    }
    public String getDirector() 
    {
        return this.director;
    }
    public double getRating() 
    {
        return this.rating;
    }
    public ArrayList<String> getStars() 
    {
        return this.stars;
    }
    public ArrayList<String> getGenres() 
    {
        return this.genres;
    }

}
