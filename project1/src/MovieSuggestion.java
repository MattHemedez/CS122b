

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class MovieSuggestion
 */
@WebServlet("/movie-suggestion")
public class MovieSuggestion extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MovieSuggestion() {
        super();
        // TODO Auto-generated constructor stub
    }

    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String loginUser = "mytestuser";
		String loginPasswd = "mypassword";
		String loginUrl = "jdbc:mysql://localhost:3306/moviedb?allowMultiQueries=true";

		JsonArray jsonArray = new JsonArray(); // send back out to the js

		
		try {
			// Establish the connection 
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			String movieQuery= request.getParameter("query");

			String query = "SELECT m.title, m.id FROM movies AS m WHERE MATCH (title) AGAINST (? IN BOOLEAN MODE);";
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, movieQuery);
			
			ResultSet rs = statement.executeQuery();
			while(rs.next()) {
				String movieTitle = rs.getString("title");
				String movieId = rs.getString("id");
				
				JsonObject jsonMovieData = new JsonObject();
				jsonMovieData.addProperty("value",movieTitle );
				jsonMovieData.addProperty("data", movieId);
				
				jsonArray.add(jsonMovieData);
			}
			response.getWriter().write(jsonArray.toString());
            response.setStatus(200);
            rs.close();
            statement.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
