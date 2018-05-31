package fabflixmobile.ics.uci.edu.fabflix;

public class Movie {
    String movieTitle;
    int year;
    String director;
    String genres;
    String stars;

    public Movie(String title, int year, String director, String genres, String stars){
        this.movieTitle= title;
        this.year = year;
        this.director = director;
        this.genres = genres;
        this.stars = stars;
    }

    public String getTitle(){
        return movieTitle;
    }

    public Integer getYear(){
        return year;
    }

    public String getDirector(){
        return director;
    }

    public String getGenres(){
        return "Genres: " + genres;
    }

    public String getStars(){
        return "Stars: " + stars;
    }
}
