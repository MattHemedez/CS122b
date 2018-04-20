import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

public class SearchServlet {
	 private static final long serialVersionUID = 1L;


	    /**
	     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	     */
	    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
	
	    	
	    	String title = request.getParameter("title");
	        String year = request.getParameter("year");
	        String director = request.getParameter("director");
	        String starname = request.getParameter("starname");
	        
	        String loginUser = "mytestuser";
	        String loginPasswd = "mypassword";
	        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

	        /* This example only allows username/password to be test/test
	        /  in the real project, you should talk to the database to verify username/password
	        */        
	        try 
	        {
	    		Class.forName("com.mysql.jdbc.Driver").newInstance();
	    		// create database connection
	    		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
	    		// declare statement
	    		Statement statement = connection.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, 
	    				   				ResultSet.CONCUR_READ_ONLY);
	    		// prepare query
	    		String query = "SELECT m.title "
	    				     + "FROM movies AS m, stars AS s, stars_in_movies as SM "
	    				     + "WHERE ";
	    		
	    		while(true) {
	    			if(title != null) {
	    				query += "m.title =" + title + " AND ";
	    			}else if(year != null) {
	    				query += "m.year =" + year + " AND ";
	    			}else if(director != null) {
	    				query += "m.director =" + director + " AND ";
	    			}else if(starname != null) {
	    				String delim = "[ ]+";
	    				String[] parsed = starname.split(delim);
	    				
	    				query += "s.name LIKE '"+ parsed[0] + "%' " + "OR " + "s.name LIKE '%" + parsed[1] + "';";
	    			}
	    		}
	    		
	    		if(query.endsWith("AND ")) {
	    			
	    		}
	    		
//	    		String query = "SELECT c.firstName, c.lastName FROM customers AS c WHERE c.password LIKE '" + password + "' AND (c.email LIKE '" + username + "@%' OR c.email LIKE '" + username + "');";
	    		
	    		// execute query
	    		ResultSet resultSet = statement.executeQuery(query);
	    		if(resultSet.next())
	    		{
	    			// Login success:

	                // set this user into the session
	                request.getSession().setAttribute("user", new User(username));

	                JsonObject responseJsonObject = new JsonObject();
	                responseJsonObject.addProperty("status", "success");
	                responseJsonObject.addProperty("message", "success");

	                response.getWriter().write(responseJsonObject.toString());
	    		}
	    		else
	    		{
	    			// Login fail
	                JsonObject responseJsonObject = new JsonObject();
	                responseJsonObject.addProperty("status", "fail");
	                responseJsonObject.addProperty("message", "The username password combination doesn't exist");
	                response.getWriter().write(responseJsonObject.toString());
	    		}
	    		resultSet.close();
	    		statement.close();
	    		connection.close();
	    	}
	        catch (Exception e) 
	        {
	    		e.printStackTrace();	
	    		
	    		// Login fail
	            JsonObject responseJsonObject = new JsonObject();
	            responseJsonObject.addProperty("status", "fail");
	            responseJsonObject.addProperty("message", "Communication with server error");
	            response.getWriter().write(responseJsonObject.toString());
	    	}
	
	
	
}
