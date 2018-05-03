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
import java.sql.SQLException;

//Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "CartServlet", urlPatterns = "/api/cart")
public class CartServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;

	// Create a dataSource which registered in web.xml
	//@Resource(name = "jdbc/moviedb")
	//private DataSource dataSource;
	String loginUser = "mytestuser";
	String loginPasswd = "mypassword";
	String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

	public PreparedStatement updateMovies(Connection connection, String movieId, String customerId, int amount, String movieName) throws SQLException {
        String query = "INSERT INTO cart(movieId, customerId, quantity, movieTitle) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE quantity=quantity+1;";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, movieId);
        statement.setString(2, customerId);
        statement.setInt(3, 1);
        statement.setString(4, movieName);
        
        return statement;
	}
	
	public PreparedStatement returnMovies(Connection connection, String customerId) throws SQLException {
		String query = "SELECT c.movieTitle, c.quantity FROM cart AS c "
				+ "WHERE c.customerId= ?;";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, customerId);
		
		return statement;
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json"); // Response mime type

		// Retrieve parameter id from url request.
		//String id = request.getParameter("id");
		String customerId=((User) request.getSession().getAttribute("user")).getId();

		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		try {
			// Get a connection from dataSource
			//Connection dbcon = dataSource.getConnection();
			// create database connection
			Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			
			// Declare our statement
			PreparedStatement statement = returnMovies(connection, customerId);
			
			// Perform the query
			ResultSet rs = statement.executeQuery();
			
			
			JsonArray jsonShoppingCart = new JsonArray(); 
			// Iterate through each row of rs
			while(rs.next()) {
				String title = rs.getString("movieTitle");
				String amount = (String) rs.getString("quantity");
				
	            System.out.println(title + " : " + amount);

				JsonObject jsonMovieData = new JsonObject();
				jsonMovieData.addProperty("title", title);
				jsonMovieData.addProperty("quantity", amount);
				
				jsonShoppingCart.add(jsonMovieData);
			}
				
			// write JSON string to output
			out.write(jsonShoppingCart.toString());
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
