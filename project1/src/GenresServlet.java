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
