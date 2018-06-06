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

@WebServlet(name = "DashboardServlet", urlPatterns = "/api/_dashboard")
public class DashboardServlet extends HttpServlet {
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
    		
    		// get request type and pass it off to handler function
    		String requestType = request.getParameter("Request");
    		
    		if(requestType.equals("addMovie"))
    			handleAddMovieRequest(dbcon, request, response);
    		else if(requestType.equals("addStar"))
    			handleAddStarRequest(dbcon, request, response);
    		else if(requestType.equals("getMetadata"))
    			handleMetadataRequest(dbcon, request, response);
    			
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
    
    // Handle an addStar request from client. Interact with database and return status message to user
    private final void handleAddStarRequest(Connection con, HttpServletRequest request, HttpServletResponse response) throws IOException
    {	
    	try
    	{
	    	// get the user input for the query
			String starName = request.getParameter("starName");
	        String starBirthYear = request.getParameter("starBirthYear");
	        
	        // Check for error in params
	    	if(starName.equals("")) // No name was given for star
	    	{
	    		JsonObject responseJsonObject = new JsonObject();
	            responseJsonObject.addProperty("status", "fail");
	            responseJsonObject.addProperty("message", "No name was given to the star to be added");
	            response.getWriter().write(responseJsonObject.toString());
	    	}
	    	else
	    	{
	    		// Declare our statement
		     	PreparedStatement statement = con.prepareCall("{call add_star(?, ?)}");
		     	
		     	// Prepares the statement
		     	statement.setString(1, starName);
		     	if(!starBirthYear.equals(""))
		     		statement.setString(2, starBirthYear);
		     	else //Set statement to NULL if it the birth year is empty
		     		statement.setNull(2, java.sql.Types.INTEGER);
		     	
		
		     	// Perform the query
		     	ResultSet rs = statement.executeQuery();
				
		 		if (rs.next()) 
				{
		            // Added Star successfully ran on database:
					String status = rs.getString("status");
					String message = rs.getString("message");
					if(status.equals("ERROR")) // Shouldn't happen unless something extremely wrong occurred
					{
						JsonObject responseJsonObject = new JsonObject();
		                responseJsonObject.addProperty("status", "fail");
		                responseJsonObject.addProperty("message", "Unexpected error occurred in result set. Error Message: " + message);
		                response.getWriter().write(responseJsonObject.toString());
					}
					else // Handle Success message
					{
		                JsonObject responseJsonObject = new JsonObject();
		                responseJsonObject.addProperty("status", "success");
		                responseJsonObject.addProperty("message", message);
		                response.getWriter().write(responseJsonObject.toString());
					}
				} 
				else // Error retrieving result sets
				{
		            JsonObject responseJsonObject = new JsonObject();
		            responseJsonObject.addProperty("status", "fail");
		            responseJsonObject.addProperty("message", "Database Error: Received no result sets");
		            response.getWriter().write(responseJsonObject.toString());
		        }
		 		
		 		rs.close();
	    		statement.close();
	    	}
    	}
    	catch(Exception e)
    	{
    		JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "Database Error: Error creating/sending query to database");
            response.getWriter().write(responseJsonObject.toString());
    	}
    }
    
    // Handle an addMovie request from client. Interact with database and return status message to user
    private final void handleAddMovieRequest(Connection con, HttpServletRequest request, HttpServletResponse response) throws IOException
    {
    	try
    	{
	    	// get the user input for the query
			String movieTitle = request.getParameter("movieTitle");
	        String movieYear = request.getParameter("movieYear");
	        String movieDirector = request.getParameter("movieDirector");
	        String movieStarName = request.getParameter("movieStarName");
	        String movieGenre = request.getParameter("movieGenre");
	    	
	        // Double Check parameters
	        if(movieTitle.equals("") || movieYear.equals("") || movieDirector.equals("") || movieStarName.equals("") || movieGenre.equals(""))
	        {
	        	String message = "";
	        	if(movieTitle.equals(""))
	        		message += "Movie title field is left blank. ";
	        	if(movieYear.equals(""))
	        		message += "Year field is left blank. ";
	        	if(movieDirector.equals(""))
	        		message += "Director field is left blank. ";
	        	if(movieStarName.equals(""))
	        		message += "Star name field is left blank. ";
	        	if(movieGenre.equals(""))
	        		message += "Movie genre name field is left blank. ";
	        	
	        	JsonObject responseJsonObject = new JsonObject();
	            responseJsonObject.addProperty("status", "fail");
	            responseJsonObject.addProperty("message", message);
	            response.getWriter().write(responseJsonObject.toString());
	        }
	        else // Params look ok send to database
	        {
	        	// Declare our statement
		     	PreparedStatement statement = con.prepareCall("{call add_movie(?, ?, ?, ?, ?)}");
		     	
		     	// Prepares the statement
		     	statement.setString(1, movieTitle);
		     	statement.setString(2, movieYear);
		     	statement.setString(3, movieDirector);
		     	statement.setString(4, movieStarName);
		     	statement.setString(5, movieGenre);
		
		     	// Perform the query
		     	ResultSet rs = statement.executeQuery();
				
		 		if (rs.next()) 
				{
		            // Added Star successfully ran on database:
					String status = rs.getString("status");
					String message = rs.getString("message");
					if(status.equals("ERROR")) // Occurs when there is a duplicate movie in the database
					{
						JsonObject responseJsonObject = new JsonObject();
		                responseJsonObject.addProperty("status", "fail");
		                responseJsonObject.addProperty("message", message);
		                response.getWriter().write(responseJsonObject.toString());
					}
					else // Handle Success message
					{
		                JsonObject responseJsonObject = new JsonObject();
		                responseJsonObject.addProperty("status", "success");
		                responseJsonObject.addProperty("message", message);
		                response.getWriter().write(responseJsonObject.toString());
					}
				} 
				else // Error retrieving result sets
				{
		            JsonObject responseJsonObject = new JsonObject();
		            responseJsonObject.addProperty("status", "fail");
		            responseJsonObject.addProperty("message", "Database Error: Received no result sets");
		            response.getWriter().write(responseJsonObject.toString());
		        }
		 		
		 		rs.close();
	    		statement.close();
	        }
    	}
    	catch(Exception e)
    	{
    		JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "Database Error: Error creating/sending query to database");
            response.getWriter().write(responseJsonObject.toString());
    	}
    }
    
    // Handle a getMetaData request from client. Interact with database and return status message and/or relevant data to user
    /*
     * {"status":"success/fail"
     * 	"message":"Error or success message"
     * 	"tables":[
     * 		{"tableName":"thisTableName"
     * 		 "columns":[
     * 			{"columnName":"nameOfColumn"}, 
     * 			{"columnType":"VARCHAR(num) || INT(num) || etc."},
     * 			{"isNullable":"YES || NO"} ] } ] }
     */
    private final void handleMetadataRequest(Connection con, HttpServletRequest request, HttpServletResponse response) throws IOException
    { 
    	try
    	{	
        	// Declare our statement
	     	PreparedStatement statement = con.prepareCall("SELECT TABLE_NAME, COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'moviedb' ORDER BY TABLE_NAME, ORDINAL_POSITION;");
	     	
	     	// Perform the query
	     	ResultSet rs = statement.executeQuery();
	     	
	     	// Create Json response Items
	     	JsonObject responseJsonObject = new JsonObject();
	     	JsonArray jsonTablesArray = new JsonArray();
	     	JsonObject jsonTableMetaData = new JsonObject();
            JsonArray jsonColumnsArray = new JsonArray();
            JsonObject columnData = new JsonObject();
	     	
	     	String previousTableName = null;
	     	
	 		while (rs.next()) 
	 		{
	 			String tableName = rs.getString("TABLE_NAME");
	 			if(previousTableName == null)
	 			{
	 				jsonTableMetaData = new JsonObject();
	                jsonColumnsArray = new JsonArray();
	 			}
	 			else if(!previousTableName.equals(tableName))
	 			{
	 				jsonTableMetaData.addProperty("tableName", previousTableName);
    				jsonTableMetaData.add("columns", jsonColumnsArray);
    				jsonTablesArray.add(jsonTableMetaData);
	 				
	 				jsonTableMetaData = new JsonObject();
	                jsonColumnsArray = new JsonArray();
	 			}
 				columnData = new JsonObject();
        		String columnName = rs.getString("COLUMN_NAME");
				String columnType = rs.getString("COLUMN_TYPE");
				String isNullable = rs.getString("IS_NULLABLE");
				
				columnData.addProperty("columnName", columnName);
				columnData.addProperty("columnType", columnType);
				columnData.addProperty("isNullable", isNullable);
				jsonColumnsArray.add(columnData);
				
				previousTableName = tableName;
	 		}
	 		jsonTableMetaData.addProperty("tableName", previousTableName);
			jsonTableMetaData.add("columns", jsonColumnsArray);
			jsonTablesArray.add(jsonTableMetaData);
	 		
	 		responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "Successfully retrieved metadata for tables.");
            responseJsonObject.add("tables", jsonTablesArray);
            response.getWriter().write(responseJsonObject.toString());
			
	 		rs.close();
    		statement.close();
    	}
    	catch(Exception e)
    	{
    		JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "Database Error: Error creating/sending query to database");
            response.getWriter().write(responseJsonObject.toString());
    	}
    }
}
