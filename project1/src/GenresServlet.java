import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "GenresServlet", urlPatterns = "/api/genres")
public class GenresServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        try 
        {
        	// Create Driver instance
    		Class.forName("com.mysql.jdbc.Driver").newInstance();
    		
    		// create database connection
    		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
    		
    		// Declare our statement
	     	PreparedStatement statement = connection.prepareCall("SELECT DISTINCT name FROM genres ORDER BY name;");	     	
	
	     	// Perform the query
	     	ResultSet rs = statement.executeQuery();
			
	     	// Setup Json Variables
	     	JsonObject responseJsonObject = new JsonObject();
	     	JsonArray jsonGenres = new JsonArray();
	     	JsonObject jsonGenre;
	     	String genreName;
	     	
	     	while (rs.next()) 
	 		{
	     		genreName = rs.getString("name");
	 			jsonGenre = new JsonObject();
	 			jsonGenre.addProperty("name", genreName);
	 			jsonGenres.add(jsonGenre);
	 		}
	 		
	 		responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "Successfully retrieved all listed genres from database.");
            responseJsonObject.add("genres", jsonGenres);
            response.getWriter().write(responseJsonObject.toString());
	 		
	 		rs.close();
    		statement.close();
    		connection.close();
        }
        catch (Exception e)
        {
        	// Error with connection or parameter request
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", e.toString());
            response.getWriter().write(responseJsonObject.toString());
    	}
    }
}
