import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import java.sql.PreparedStatement;

/**
 * Servlet implementation class CartServlet
 */
@WebServlet(name = "CartServlet", urlPatterns = "/api/cart")
public class CartServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;
    
	
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

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
        //HttpSession session = request.getSession(true); // Get a instance of current session on the request
        //ArrayList<String> previousMovies = (ArrayList<String>) session.getAttribute("previousMovies"); // Retrieve data named "previousItems" from session
		response.setContentType("application/json"); // Response mime type

		PrintWriter out = response.getWriter();
        
		String id = request.getParameter("id");


        
        
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://ec2-18-188-219-180.us-east-2.compute.amazonaws.com:3306/moviedb?allowMultiQueries=true";
        
        
        
        String customerId=((User) request.getSession().getAttribute("user")).getId();
        String movieId = request.getParameter("movieId");
        String movieName = request.getParameter("movieName");
        
      
        
//        String query = "INSERT INTO cart (movieId, customerId, quantity) "
//        		+ "VALUES('"+ movieId + "', '" + customerId + "', " + " 1)"
//        		+ "ON DUPLICATE KEY UPDATE quantity=quantity+1;";
        
        
        try 
        {
    		Class.forName("com.mysql.jdbc.Driver").newInstance();
    		// create database connection
    		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
    		// declare statement
//    		Statement statement = connection.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, 
//    				   				ResultSet.CONCUR_READ_ONLY);
			JsonArray jsonShoppingCart = new JsonArray();

    		if(movieId != null || movieName != null) {
    			// used to update quantity of the movie 
    			int incrementBy = 1;
    			PreparedStatement toExecute= updateMovies(connection,movieId, customerId, incrementBy,movieName);
    			toExecute.executeUpdate();
    			toExecute.close();
    		}else {
    			// need to fetch the customers data
    			PreparedStatement statement = returnMovies(connection, id);
    			ResultSet rs = statement.executeQuery();

    			while(rs.next()) {
    				String title = rs.getString("movieTitle");
    				String amount = (String) rs.getString("quantity");
    				
    	            System.out.println(title + " : " + amount);

					JsonObject jsonMovieData = new JsonObject();
					jsonMovieData.addProperty("title", title);
    				jsonMovieData.addProperty("quantity", amount);
    				
    				jsonShoppingCart.add(jsonMovieData);
    			}
    			System.out.println(jsonShoppingCart);
    			out.write(jsonShoppingCart.toString());
                response.setStatus(200);
                rs.close();
                statement.close();
    		}

    		
  
    		
    		
    		
			connection.close();
        }
        catch(Exception e){
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);        }
        
 
        
	}
}
