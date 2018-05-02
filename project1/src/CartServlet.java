

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class CartServlet
 */
@WebServlet(name = "CartServlet", urlPatterns = "/CartServlet")
public class CartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
        HttpSession session = request.getSession(true); // Get a instance of current session on the request
        ArrayList<String> previousMovies = (ArrayList<String>) session.getAttribute("previousMovies"); // Retrieve data named "previousItems" from session
        PrintWriter out = response.getWriter();
        
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://ec2-18-220-219-13.us-east-2.compute.amazonaws.com:3306/moviedb?allowMultiQueries=true";
        
        
        
        String customerId= (String) request.getSession().getAttribute("id");
        String movieId = request.getParameter("movieId");
        String movieName = request.getParameter("movieName");
        
        String query = "INSERT INTO cart (movieId, customerId, quantity) "
        		+ "VALUES('"+ movieId + "', '" + customerId + "', '" + " 1)"
        		+ "ON DUPLICATE KEY UPDATE quantity=quantity+1;";
        
        
        try 
        {
    		Class.forName("com.mysql.jdbc.Driver").newInstance();
    		// create database connection
    		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
    		// declare statement
    		Statement statement = connection.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, 
    				   				ResultSet.CONCUR_READ_ONLY);
    		
    		ResultSet resultSet = statement.executeQuery(query);

        }
        catch(Exception e){
        	e.printStackTrace();
        }
        
        
        
        
        
        
        
        
        
//        if (previousMovies == null) {
//        	previousMovies = new ArrayList<>();
//            session.setAttribute("previousMovies", previousMovies); // Add the newly created ArrayList to session, so that it could be retrieved next time
//        }
//        
//        String newMovie = request.getParameter("movieName"); // Get parameter that sent by GET request url
        
        
        
//        
//        JsonArray jsonArrayGenres = new JsonArray();
//		JsonObject jsonMovies = new JsonObject();
//
//		for(String movie: previousMovies)
//		{
//			jsonMovies.addProperty("movie", movie);
//			jsonArrayGenres.add(jsonMovies);
//		}

		// Create a JsonObject based on the data we retrieve from rs
//		JsonObject jsonObject = new JsonObject();
//		jsonObject.addProperty("movie_id", movieId);
//		jsonObject.addProperty("movie_title", movieTitle);
//		jsonObject.addProperty("movie_year", movieYear);
//		jsonObject.addProperty("movie_director", movieDirector);
//		jsonObject.addProperty("movie_rating", movieRating);
//		jsonObject.addProperty("movie_num_votes", movieNumVotes);
//		jsonObject.add("movie_genres", jsonArrayGenres);
//		jsonObject.add("movie_stars", jsonArrayStars);
		
        // write JSON string to output
//        out.write(jsonMovies.toString());
        // set response status to 200 (OK)
//        response.setStatus(200);

        
        
        
	}
}
