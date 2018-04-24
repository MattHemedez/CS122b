import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

import javax.servlet.RequestDispatcher;
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
	        String year = request.getParameter("eyear");
	        String director = request.getParameter("director");
	        String starname = request.getParameter("starname");
	        String limit = request.getParameter("limit");
	        String pageNum = request.getParameter("pagenum");
	        String offset = ""; 
	        if(limit == null)
	        	limit = "10";
	        if(pageNum == null || Integer.parseInt(pageNum) <= 0)
	        {
	        	pageNum = "1";
	        	offset = "0";
	        }
	        else
	        	offset += ((Integer.parseInt(pageNum) - 1) * Integer.parseInt(limit));
	        
	        String loginUser = "mytestuser";
	        String loginPasswd = "mypassword";
	        String loginUrl = "jdbc:mysql://localhost/moviedb?allowMultiQueries=true";
	        
	        try 
	        {
	    		Class.forName("com.mysql.jdbc.Driver").newInstance();
	    		// create database connection
	    		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
	    		// declare statement
	    		Statement statement = connection.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, 
	    				   				ResultSet.CONCUR_READ_ONLY);
	    		// prepare query
	    		String query ="FROM movies AS m, stars AS s, stars_in_movies AS SM, ratings AS r, genres AS g, genres_in_movies AS gm " + 
	    				"WHERE m.id = SM.movieId AND SM.starId = s.id AND r.movieId = m.id AND g.id = gm.genreId AND gm.movieId = m.id AND ";
			if(title != null && !title.equals("")) 
    				query += "m.title LIKE '%" + title + "%' AND ";
    			
    			if(year != null && !year.equals("")) 
    				query += "m.year ='" + year + "' AND ";

    			if(director != null && !director.equals("")) 
    				query += "m.director LIKE '%" + director + "%' AND ";	

    			if(starname != null && !starname.equals("")) {
    				String delim = "[ ]+";
    				String[] parsed = starname.split(delim);
    				query += "s.name LIKE '"+ parsed[0] + "%' " + "OR " + "s.name LIKE '%" + parsed[1];
    			}

	    		if(query.endsWith("AND ")) 
	    		{
	    			query = query.substring(0, query.length() - 4);
	    		}
	    		
	    		String selectQuery1 = "SELECT m.id, m.title, m.year, m.director, r.rating, r.numVotes, GROUP_CONCAT(DISTINCT s.name SEPARATOR ',') AS stars, GROUP_CONCAT(DISTINCT g.name SEPARATOR ',') AS genres " + query;
	    		String selectQuery2 = "SELECT COUNT(DISTINCT m.id) AS total " + query + ";";
	    		
	    		selectQuery1 += "GROUP BY m.id "
	    				+ "ORDER BY m.year ASC "
	    				+ "LIMIT " + limit + " "
	    				+ "OFFSET " + offset + ";";
	    		
	    		boolean hasResultSets = statement.execute(selectQuery2 + selectQuery1);
	    		ResultSet resultSet = statement.getResultSet();
	    		
	    		int totalResults = 0;
	    		int totalPages = 0;
	    		while(resultSet.next())
	    		{
	    			totalResults = resultSet.getInt("total");
	    		}
	    		totalPages = (int)Math.ceil((1.0 * totalResults)/ (1.0 * Integer.parseInt(limit)));
	    		
	    		statement.getMoreResults();
	    		resultSet = statement.getResultSet();
	    		
	    		String url =request.getScheme() + "://" +   // "http" + "://
	    	             request.getServerName() +       // "myhost"
	    	             ":" +                           // ":"
	    	             request.getServerPort() +       // "8080"
	    	             request.getRequestURI() +       // "/people"
	    	             "?" +                           // "?"
	    	             request.getQueryString();
	    		
	    		ArrayList<String> movieTitles = new ArrayList<String>();
	    		HashMap<String, HashSet<String>> actors = new HashMap<String, HashSet<String>>();

	    		HashMap<String, HashSet<String>> genres = new HashMap<String, HashSet<String>>();
	    		
	    		while(resultSet.next()) {
	    			String movieName = resultSet.getString("title");
	    			movieTitles.add(movieName);
	    			actors.put(movieName, new HashSet<String>());
	    			genres.put(movieName, new HashSet<String>());
	    			
	    			StringTokenizer actorsST = new StringTokenizer(resultSet.getString("stars"),",");
	    			StringTokenizer genreST = new StringTokenizer(resultSet.getString("genres"),",");

	    			while(actorsST.hasMoreTokens()) {
	    				actors.get(movieName).add(actorsST.nextToken());
	    			}
	    			
	    			while(genreST.hasMoreTokens()) {
	    				genres.get(movieName).add(genreST.nextToken());
	    			}
	    			
	    		}
    		
	    		request.setAttribute("query", query);
	    		if (movieTitles.size()>0) {
	    			request.setAttribute("url", url);
	                request.setAttribute("movies", movieTitles);
	                request.setAttribute("query", query);
	                request.setAttribute("actors", actors);
	                request.setAttribute("genres", genres);
	                request.setAttribute("pageNum", pageNum);
	                request.setAttribute("totalPages", totalPages);
	                request.setAttribute("status", "Success");
	    			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/movielist.jsp");
	                dispatcher.forward(request, response);
	            }else {
	            	// no movies in the search
	            	request.setAttribute("movies", "Error no movies found with search criteria");

	    			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/movielist.jsp");
	                dispatcher.forward(request, response);
	            }	    		
	    		statement.close();
	    		connection.close();
	    	}
	        catch (Exception e) 
	        {
	    		e.printStackTrace();
	    	}
	    }
}
