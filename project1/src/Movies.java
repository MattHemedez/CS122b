import java.util.*;


public class Movies {

	private String id;

	private String title;
	
	private int year;

	private String director;
	
	private HashMap<String,Integer> genres; 
		
	public Movies(){
		genres = new HashMap<String,Integer>();
	}
	
	public Movies(String id, String title, int year,String Director) {
		this.id = id;
		this.title = title;
		this.year  = year;
		this.director = Director;
		genres = new HashMap<String,Integer>();
	}

	public String getId(){
		return id;
	}

	public void setId(String id){
		this.id=id;
	}

	public String getTitle(){
		return title;
	}

	public void setTitle(String title){
		this.title=title;
	}
	
	public int getYear(){
		return year;
	}
	
	public void setYear(int year){
		this.year = year;
	}

	public String getDirector(){
		return director;
	}

	public void setDirector(String director){
		this.director=director;
	}

	public HashMap<String,Integer> getGenres(){
		return genres;
	}
	
	public void setGenres( String genre,int genreID){
//		System.out.println("This is the movie: " + this.title + " AND THIS IS THE GENRE: " + genre);
		genres.put(genre, genreID);
//		this.g= genre;
	}
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Movies Details - ");
		sb.append("Id:" + getId());
		sb.append(", ");
		sb.append("Title:" + getTitle());
		sb.append(", ");
		sb.append("Year:" + getYear());
		sb.append(", ");
		sb.append("Director:" + getDirector());
		sb.append(", Genres:" + getGenres());
		sb.append(".");
		
		return sb.toString();
	}
}
