import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "GenresServlet", urlPatterns = "/api/genres")
public class GenresServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try 
        {
        	// the following few lines are for connection pooling
            // Obtain our environment naming context
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                System.out.println("envCtx is NULL");
            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/TestDB");
            if (ds == null)
                System.out.println("ds is null.");
            Connection dbcon = ds.getConnection();
            if (dbcon == null)
                System.out.println("dbcon is null.");
    		
    		// Declare our statement
	     	PreparedStatement statement = dbcon.prepareCall("SELECT DISTINCT name FROM genres ORDER BY name;");	     	
	
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
    		dbcon.close();
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
