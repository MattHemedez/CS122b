import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

@WebServlet("/SearchServlet")
public class SearchServlet extends HttpServlet {
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
    
	        PrintWriter out = response.getWriter();
	        out.println("<html>");
	        out.println("<head><title>ZotFlix</title></head>");
	        out.println("<body>");
	        
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
	    				     + "WHERE m.id = SM.movieId AND SM.starId = s.id AND ";
    		
    			if(title != null) 
    			{
    				query += "m.title LIKE '%" + title + "%' AND ";
    			}
    			else if(year != null) 
    			{
    				query += "m.year ='" + year + "' AND ";
    			}
    			else if(director != null) 
    			{
    				query += "m.director LIKE '%" + director + "%' AND ";	
    			}
    			else if(starname != null) 
    			{
    				String delim = "[ ]+";
    				String[] parsed = starname.split(delim);
    				query += "s.name LIKE '"+ parsed[0] + "%' " + "OR " + "s.name LIKE '%" + parsed[1];
    			}
	    		
	    		if(query.endsWith("AND ")) 
	    		{
	    			query = query.substring(0, query.length() - 4);
	    		}
	    		
	    		query += ";";
	    		
	    		out.println("<h3> ");
    			out.println(query);
    			out.println("</h3>");
	    		
	    		ResultSet resultSet = statement.executeQuery(query);
	    		while(resultSet.next()) {
	    			String movieName = resultSet.getString("title");
	    			out.println("<h3> ");
	    			out.println(movieName);
	    			out.println("</h3>");
	    		}
	    		
	    		statement.close();
	    		connection.close();
	    	}
	        catch (Exception e) 
	        {
	    		e.printStackTrace();	
	    		out.println("<h3> ");
    			out.println(e.getMessage());
    			out.println("</h3>");
	    	}
	        out.println("</body>");
	        out.println("</html>");
	    }
}
