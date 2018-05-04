import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;

	// Create a dataSource which registered in web.xml
	//@Resource(name = "jdbc/moviedb")
	//private DataSource dataSource;
	String loginUser = "mytestuser";
    String loginPasswd = "mypassword";

    String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

 
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json"); // Response mime type

		// Retrieve parameter id from url request.
		String id = request.getParameter("id");

		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		try {
			// Get a connection from dataSource
			//Connection dbcon = dataSource.getConnection();
			// create database connection
    		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

			// Construct a query with parameter represented by "?"
    		String query = "SELECT m.id, m.title, m.director, m.year, r.numVotes, r.rating, GROUP_CONCAT(DISTINCT s.id,':', s.name SEPARATOR ', ') AS stars, GROUP_CONCAT(DISTINCT g.name SEPARATOR ', ') AS genres FROM movies AS m, stars AS s, stars_in_movies AS sm, ratings AS r, genres AS g, genres_in_movies AS gm WHERE m.id = ? AND m.id = sm.movieId AND sm.starId = s.id AND r.movieId = m.id AND g.id = gm.genreId AND gm.movieId = m.id GROUP BY m.id;";

			// Declare our statement
			PreparedStatement statement = connection.prepareStatement(query);

			// Set the parameter represented by "?" in the query to the id we get from url,
			// num 1 indicates the first "?" in the query
			statement.setString(1, id);

			// Perform the query
			ResultSet rs = statement.executeQuery();

			// Iterate through each row of rs
			rs.next();

			String movieId = rs.getString("id");
			String movieTitle = rs.getString("title");
			String movieYear = rs.getString("year");
			String movieDirector = rs.getString("director");
			String movieRating = rs.getString("rating");
			String movieNumVotes = rs.getString("numVotes");
			String movieGenres = rs.getString("genres");
			String movieStars = rs.getString("stars");
			
			JsonArray jsonArrayGenres = new JsonArray();
			for(String genre: movieGenres.split(", "))
			{
				JsonObject jsonGenre = new JsonObject();
				jsonGenre.addProperty("genre_name", genre);
				jsonArrayGenres.add(jsonGenre);
			}
			JsonArray jsonArrayStars = new JsonArray();
			for(String idNameStr: movieStars.split(", "))
			{
				String[] idNameArr = idNameStr.split(":");
				JsonObject jsonStar = new JsonObject();
				jsonStar.addProperty("star_id", idNameArr[0]);
				jsonStar.addProperty("star_name", idNameArr[1]);
				jsonArrayStars.add(jsonStar);
			}
			
			// Create a JsonObject based on the data we retrieve from rs

			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("movie_id", movieId);
			jsonObject.addProperty("movie_title", movieTitle);
			jsonObject.addProperty("movie_year", movieYear);
			jsonObject.addProperty("movie_director", movieDirector);
			jsonObject.addProperty("movie_rating", movieRating);
			jsonObject.addProperty("movie_num_votes", movieNumVotes);
			jsonObject.add("movie_genres", jsonArrayGenres);
			jsonObject.add("movie_stars", jsonArrayStars);
			
            // write JSON string to output
            out.write(jsonObject.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

			rs.close();
			statement.close();
			connection.close();
		} catch (Exception e) {
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);
		}
		out.close();

	}

}
