package fabflixmobile.ics.uci.edu.fabflix;

public class Movie {
    String movieTitle;
    int year;

    public Movie(String title, int year){
        this.movieTitle= title;
        this.year = year;
    }

    public String getTitle(){
        return movieTitle;
    }

    public Integer getYear(){
        return year;
    }
}
