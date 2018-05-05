
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.sql.PreparedStatement;

/**
 * Servlet implementation class CartServlet
 */
@WebServlet(name = "CartServlet", urlPatterns = "/api/cart")
public class CartServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;
	// Create a dataSource which registered in web.xml
	//@Resource(name = "jdbc/moviedb")
	//private DataSource dataSource;
	String loginUser = "mytestuser";
	String loginPasswd = "mypassword";
	String loginUrl = "jdbc:mysql://localhost:3306/moviedb?allowMultiQueries=true";


	public PreparedStatement updateMovies(Connection connection, String movieId, String customerId, String movieName, String moviePoster, int changeQuant) throws SQLException {
        String query = "INSERT INTO cart(movieId, customerId, quantity, movieTitle, moviePoster) "
        		+ "VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE quantity=quantity+?;"
        		+ "DELETE FROM cart WHERE quantity<=0;";
        
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, movieId.replace("/", ""));
        statement.setString(2, customerId);
        statement.setInt(3, 1);
        statement.setString(4, movieName);
        statement.setString(5, moviePoster);
        statement.setInt(6, changeQuant);
        return statement;
	}
	
	public PreparedStatement deleteUpdate(Connection connection, String movieId, String customerId, String movieName, String moviePoster, int changeQuant) throws SQLException {
        String query = "INSERT INTO cart(movieId, customerId, quantity, movieTitle, moviePoster) "
        		+ "VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE quantity=?;"
        		+ "DELETE FROM cart WHERE quantity<=0;";
        
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, movieId.replace("/", ""));
        statement.setString(2, customerId);
        statement.setInt(3, 1);
        statement.setString(4, movieName);
        statement.setString(5, moviePoster);
        statement.setInt(6, changeQuant);
      
        return statement;
	}
	
	public PreparedStatement returnMovies(Connection connection, String customerId) throws SQLException {
		String query = "SELECT c.movieTitle, c.quantity,c.moviePoster,c.movieId FROM cart AS c "
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

        
        
        
        String customerId=((User) request.getSession().getAttribute("user")).getId();
        String movieId = request.getParameter("movieId");
        String movieName = request.getParameter("movieName");
        String moviePoster = request.getParameter("moviePoster");
      
        System.out.println("This is the MOVIEID: " + movieId);
        System.out.println("This is the movieTitle: " + movieName);

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
    		
    	        int changeQuantity = (request.getParameter("increment") != null ?1: -1);
    	        
    	        if(request.getParameter("validInput")!= null) {
    	        	
    	        	
    	        	changeQuantity =(request.getParameter("deleteButton")!=null ? 0:
    	        			Integer.parseInt(request.getParameter("inputChange")));
    	        	PreparedStatement toExecute= deleteUpdate(connection,movieId, customerId,movieName, moviePoster,changeQuantity);
    	        	toExecute.executeUpdate();
        			toExecute.close();
    	        }else {
    	        	System.out.println("COMES IN HERE CARTSERVLET INC/DEC");
    	        	PreparedStatement toExecute= updateMovies(connection,movieId, customerId,movieName, moviePoster,changeQuantity);
    	        	toExecute.executeUpdate();
        			toExecute.close();
    	        }
    			
    			connection.close();
                response.sendRedirect("/project1/shoppingcart.html"); // CHANGE THIS PATH TO cs122b-spring18-team-55/shoppingcart.html WHEN TESTING ON YOUR LOCALHOST

    		}else {
    			// need to fetch the customers data
    			PreparedStatement statement = returnMovies(connection, customerId);
    			ResultSet rs = statement.executeQuery();

    			while(rs.next()) {
    				String title = rs.getString("movieTitle");
    				String amount = (String) rs.getString("quantity");
    				String movieUrl = rs.getString("moviePoster");
    				String mId = rs.getString("movieId");
//    	            System.out.println(title + " : " + amount);

					JsonObject jsonMovieData = new JsonObject();
					jsonMovieData.addProperty("title", title);
    				jsonMovieData.addProperty("quantity", amount);
    				jsonMovieData.addProperty("moviePoster", movieUrl);
    				jsonMovieData.addProperty("movieId", movieUrl);
    				jsonMovieData.addProperty("movieId", mId);
    				jsonShoppingCart.add(jsonMovieData);
    			}
//    			System.out.println(jsonShoppingCart);
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
			response.setStatus(500);        
			}

	}
}
