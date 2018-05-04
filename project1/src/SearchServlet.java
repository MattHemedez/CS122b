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

	 	private String getGenre(HttpServletRequest request) {
	 		ArrayList<String> genreTypes = new ArrayList<String>(Arrays.asList("action", "adult", "adventure", "animation", "comedy", "crime", 
	 															"documentary", "drama", "family", "fantasy", "horror", "music", "musical", 
	 															"mystery", "reality-tv", "romance", "sci-fi", "sport", "thriller", "war", "western"));
 			String param = request.getParameter("genre");
	 		if(param != null && genreTypes.contains(param.toLowerCase()))
	 			return param.toLowerCase();
	 		else
	 			return null;
	 	}
	 	
	    /**
	     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	     */
	    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    	
	    	String title = request.getParameter("title");
	    	String ftitle = request.getParameter("ftitle"); // Search by first letter
	    	String year = request.getParameter("eyear");
	        String director = request.getParameter("director");
	        String starname = request.getParameter("starname");
	        String limit = request.getParameter("limit");
	        String pageNum = request.getParameter("pagenum");
	        String orderBy = request.getParameter("orderBy");
	        String order = request.getParameter("order");
	        String genre = getGenre(request);    		
	        
	        if(orderBy == null || (!orderBy.equals("title") && !orderBy.equals("rating")))
	        	orderBy = "title";
	        if(order == null || (!order.equals("DESC") && !order.equals("ASC")))
        	{
	        	if(orderBy.equals("title"))
	        		order = "ASC";
	        	else
	        		order = "DESC";
        	}
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

	        String loginUrl = "jdbc:mysql://localhost:3306/moviedb?allowMultiQueries=true";

	        
	        try 
	        {
	    		Class.forName("com.mysql.jdbc.Driver").newInstance();
	    		// create database connection
	    		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
	    		// declare statement
	    		Statement statement = connection.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, 
	    				   				ResultSet.CONCUR_READ_ONLY);
	    		// prepare query
	    		String query ="FROM (SELECT DISTINCT m.id, m.title, m.director, m.year, r.numVotes, r.rating " + 
	    				"FROM movies AS m, stars AS s, stars_in_movies AS sm, ratings AS r, genres AS g, genres_in_movies AS gm " + 
	    				"WHERE m.id = sm.movieId AND sm.starId = s.id AND r.movieId = m.id AND g.id = gm.genreId AND gm.movieId = m.id AND ";
	    		if(title != null && !title.equals("")) 
    				query += "m.title LIKE '%" + title + "%' AND ";
    			
	    		if(ftitle != null && !ftitle.equals("")) 
    				query += "m.title LIKE '" + ftitle + "%' AND ";
	    		
    			if(year != null && !year.equals("")) 
    				query += "m.year ='" + year + "' AND ";

    			if(director != null && !director.equals("")) 
    				query += "m.director LIKE '%" + director + "%' AND ";	
    			
    			if(genre != null && !genre.equals("")) {
    				query += "g.name LIKE '" + genre + "' AND ";
    			}

    			if(starname != null && !starname.equals("")) {
    				String delim = "[ ]+";
    				String[] parsed = starname.split(delim);
    				query += "s.name LIKE '"+ parsed[0] + "%' " + "OR " + "s.name LIKE '%" + parsed[1];
    			}
    			
    			

	    		

	    		if(query.endsWith("AND ")) 
	    		{
	    			query = query.substring(0, query.length() - 4) + " ";
	    			
	    		}

		
	    		String selectQuery1 = "SELECT m.id, m.title, m.director, m.year, m.numVotes, m.rating, GROUP_CONCAT(DISTINCT s.id,':', s.name SEPARATOR ',') AS stars, GROUP_CONCAT(DISTINCT g.name SEPARATOR ',') AS genres " + query;
	    		String selectQuery2 = "SELECT COUNT(DISTINCT m.id) AS total " + query + ") as m;";
		
	    		String innerOrderBy = "m.";
	    		if(orderBy.equals("rating"))
	    			innerOrderBy = "r.";
	    		selectQuery1 += "ORDER BY " + innerOrderBy + orderBy + " " + order + " " + 
	    				"LIMIT " + limit + " " +
	    				"OFFSET " + offset + ") AS m, stars AS s, stars_in_movies AS SM, genres AS g, genres_in_movies AS gm " + 
	    				"WHERE m.id = SM.movieId AND SM.starId = s.id AND g.id = gm.genreId AND gm.movieId = m.id " + 
	    				"GROUP BY m.id " + 
	    				"ORDER BY m." + orderBy + " " + order + ";";
	    		
	    		
	    			    		
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
	    		
	    		
	    		String baseUrl =request.getScheme() + "://" +   // "http" + "://
	    	             request.getServerName() +       // "myhost"
	    	             ":" +                           // ":"
	    	             request.getServerPort()+        // "8080"
	    	             request.getRequestURI();        // "/people"
	    		
	    		baseUrl = baseUrl.substring(0,baseUrl.length()-13);
	    		
	    		ArrayList<String> movieTitles = new ArrayList<String>();
	    		HashMap<String, HashSet<String>> actors = new HashMap<String, HashSet<String>>();
	    		HashMap<String, HashSet<String>> genres = new HashMap<String, HashSet<String>>();
	    		HashMap<String,String> movieID = new HashMap<String, String>();
	    		HashMap<String,String> movieRating = new HashMap<String, String>();
	    		HashMap<String,String> movieDirector = new HashMap<String,String>();
	    		HashMap<String,String> movieYear = new HashMap<String,String>();

	    		
	    		while(resultSet.next()) {
	    			String movieName = resultSet.getString("title");
	    			movieTitles.add(movieName);
	    			movieID.put(movieName, resultSet.getString("id"));
	    			movieRating.put(movieName, resultSet.getString("rating"));
	    			movieDirector.put(movieName, resultSet.getString("Director"));
	    			movieYear.put(movieName, resultSet.getString("year"));
	    			
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
    		
	    		if (movieTitles.size()>0) {
	    			request.setAttribute("movieYear", movieYear);
	    			request.setAttribute("movieDirector", movieDirector);
	    			request.setAttribute("movieRating", movieRating);
	    			request.setAttribute("movieID", movieID);
	    			request.setAttribute("url", url);
	    			request.setAttribute("baseUrl", baseUrl);
	                request.setAttribute("movies", movieTitles);
	                request.setAttribute("query", query);
	                request.setAttribute("actors", actors);
	                request.setAttribute("genres", genres);
	                request.setAttribute("pageNum", pageNum);
	                request.setAttribute("totalPages", totalPages);
	                request.setAttribute("totalResults", totalResults);
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