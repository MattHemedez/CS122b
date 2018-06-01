import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@WebServlet("/MobileServlet")
public class MobileSearchServlet extends HttpServlet {
	 private static final long serialVersionUID = 1L;

	    /**
	     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	     */
	    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    	
	    	String title = request.getParameter("title");
	    	String pageNum = request.getParameter("pagenum");
	        String limit = "10";
	        String order = "title";
	        
	        String loginUser = "mytestuser";
	        String loginPasswd = "mypassword";

	        String loginUrl = "jdbc:mysql://localhost:3306/moviedb?allowMultiQueries=true";

	        
	        try 
	        {
	    		Class.forName("com.mysql.jdbc.Driver").newInstance();
	    		// create database connection
	    		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
	    		
	    		String countQuery ="SELECT COUNT(m.id) AS total FROM movies AS m WHERE MATCH (title) AGAINST (? IN BOOLEAN MODE);";
	    				
		    		String getMoviesQuery ="SELECT m.id, m.title, m.director, m.year, r.numVotes, r.rating, " + 
		    				"		GROUP_CONCAT(DISTINCT s.id,':', s.name SEPARATOR ',') AS stars, " + 
		    				"       GROUP_CONCAT(DISTINCT g.name SEPARATOR ',') AS genres " + 
		    				"	FROM (SELECT mov.title, mov.id, mov.director, mov.year FROM movies AS mov WHERE MATCH (title) AGAINST (? IN BOOLEAN MODE) ORDER BY mov.title LIMIT ? OFFSET ?) AS m" + 
		    				"		LEFT JOIN ratings AS r ON m.id = r.movieId" + 
		    				"       LEFT JOIN stars_in_movies AS sm ON m.id = sm.movieId" + 
		    				"		LEFT JOIN stars AS s ON sm.starId = s.id " + 
		    				"		LEFT JOIN genres_in_movies AS gm ON m.id = gm.movieId" + 
		    				"		LEFT JOIN genres AS g ON gm.genreId = g.id" + 
		    				"	GROUP BY m.id ORDER BY m.title;";
	    		
	            PreparedStatement countStatement = connection.prepareStatement(countQuery);
	            PreparedStatement getMoviesStatement = connection.prepareStatement(getMoviesQuery);

	            // Setting the title 
	    		if(title != null && !title.equals("")) {
//	    			query += "m.title LIKE '%" + title + "%' AND ";
	    			countStatement.setString(1, "+" + title + "*");
	    			getMoviesStatement.setString(1, "+" + title + "*");
	    		}
	    		else {
	    			countStatement.setString(1, "");
	    			getMoviesStatement.setString(1, "");
	    		}
 
	    		getMoviesStatement.setInt(2, Integer.parseInt(limit)); // limit
	    		
	    		int intPageNum;
	    		int offset = 0;
	    		try
	    		{
	    			intPageNum = Integer.parseInt(pageNum);
	    			if(intPageNum <= 0)
	    			{
	    				intPageNum = 1;
	    				pageNum = "1";
	    			}
	    			offset = Integer.parseInt(limit) * (intPageNum - 1);
	    		}
	    		catch(Exception e)
	    		{
	    			pageNum = "1";
	    			intPageNum = 1;
	    			offset = 0;
	    		}

	    		ResultSet resultSet = countStatement.executeQuery();
	    		
	    		System.out.println(countStatement.toString());
	    		
	    		int totalResults = 0;
	    		int totalPages = 0;
	    		while(resultSet.next())
	    		{
	    			totalResults = resultSet.getInt("total");
	    		}
	    		totalPages = (int)Math.ceil((1.0 * totalResults)/ (1.0 * Integer.parseInt(limit)));
	    		if(totalResults < offset)
	    		{
	    			offset = 0;
	    			pageNum = "1";
	    		}
	    		
	    		
	    		getMoviesStatement.setInt(3, offset); // offset
	    		// statement.getMoreResults();
	    		System.out.println(totalResults);
	    		System.out.println("Q2: " + getMoviesStatement.toString());

	    		resultSet = getMoviesStatement.executeQuery();

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
	    		
	    		String websiteUrl =request.getScheme() + "://" +   // "http" + "://
			             request.getServerName() +       // "myhost"
			             ":" +                           // ":"
			             request.getServerPort()+        // "8080"
			             request.getRequestURI();        // "/people"
				websiteUrl = websiteUrl.replace("/MobileServlet", "");
	    		
	    		baseUrl = baseUrl.substring(0,baseUrl.length()-13);
	    		
	    		ArrayList<String> movieIDs = new ArrayList<String>();
	    		HashMap<String, HashSet<String>> actors = new HashMap<String, HashSet<String>>();
	    		HashMap<String, HashSet<String>> genres = new HashMap<String, HashSet<String>>();
	    		HashMap<String,String> movieTitles = new HashMap<String, String>();
	    		HashMap<String,String> movieRating = new HashMap<String, String>();
	    		HashMap<String,String> movieDirector = new HashMap<String,String>();
	    		HashMap<String,String> movieYear = new HashMap<String,String>();

	    		String temp;
	    		
	    		JsonArray jsonArrayMovies = new JsonArray();
	    		JsonObject jsonMovie;
	    		JsonArray jsonArrayActors;
	    		JsonArray jsonArrayGenres;
	    		
	    		while(resultSet.next()) {
	    			jsonMovie = new JsonObject();
	    			
	    			jsonMovie.addProperty("movieID", resultSet.getString("id"));
	    			jsonMovie.addProperty("title", resultSet.getString("title"));
	    			
	    			temp = resultSet.getString("rating");
	    			if(resultSet.wasNull())
	    				jsonMovie.addProperty("rating", "N/A");
	    			else 
	    				jsonMovie.addProperty("rating", temp);
	    			
	    			temp = resultSet.getString("Director");
	    			if(resultSet.wasNull())
	    				jsonMovie.addProperty("director", "N/A");
	    			else
	    				jsonMovie.addProperty("director", temp);
	    			
	    			temp = resultSet.getString("year");
	    			if(resultSet.wasNull())
	    				jsonMovie.addProperty("year", "N/A");
	    			else
	    				jsonMovie.addProperty("year", temp);
	    			
	    			jsonArrayActors = new JsonArray();
	    			JsonObject jsonActor = new JsonObject();
	    			jsonArrayGenres = new JsonArray();
	    			JsonObject jsonGenre = new JsonObject();
	    			
	    			int count = 0;
	    			temp = resultSet.getString("stars");
	    			if(!resultSet.wasNull())
	    			{
		    			StringTokenizer actorsST = new StringTokenizer(temp,",");
		    			while(actorsST.hasMoreTokens()) {
		    				++count;
		    				
		    				jsonActor = new JsonObject();
		    				String actorAndId = actorsST.nextToken();
		    				StringTokenizer actorsAndIdST = new StringTokenizer(actorAndId,":");
		    				jsonActor.addProperty("starID", actorsAndIdST.nextToken());
		    				jsonActor.addProperty("starName", actorsAndIdST.nextToken());

		    				jsonArrayActors.add(jsonActor);
		    			}
	    			}
	    			else
	    			{
	    				jsonActor = new JsonObject();
	    				jsonActor.addProperty("starID", "");
	    				jsonActor.addProperty("starName", "N/A");
	    				jsonArrayActors.add(jsonActor);
	    			}
	    			jsonMovie.add("stars", jsonArrayActors);
	    			jsonMovie.addProperty("totalStars", count);
	    			
	    			count = 0;
	    			temp = resultSet.getString("genres"); 
	    			if(!resultSet.wasNull())
	    			{
		    			StringTokenizer genreST = new StringTokenizer(temp,",");
		    			while(genreST.hasMoreTokens()) {
		    				++count;
		    				jsonGenre = new JsonObject();
		    				jsonGenre.addProperty("genreName", genreST.nextToken());

		    				jsonArrayGenres.add(jsonGenre);
		    			}
	    			}
	    			else
	    			{
	    				jsonGenre = new JsonObject();
	    				jsonGenre.addProperty("genreName", "N/A");
	    				jsonArrayGenres.add(jsonGenre);
	    			}
	    			jsonMovie.add("genres", jsonArrayGenres);
	    			jsonMovie.addProperty("totalGenres", count);
	    			
	    			jsonArrayMovies.add(jsonMovie);
	    		}
	    		
	    		JsonObject responseJsonObject = new JsonObject();
	    		if (jsonArrayMovies.size()>0) {
	                
	    			responseJsonObject.addProperty("url", url);
	    			responseJsonObject.addProperty("baseUrl", baseUrl);
	    			responseJsonObject.addProperty("websiteUrl", websiteUrl);
	                responseJsonObject.addProperty("query", getMoviesQuery);
	                responseJsonObject.addProperty("pageNum", pageNum);
	                responseJsonObject.addProperty("totalPages", totalPages);
	                responseJsonObject.addProperty("totalResults", totalResults);
	                responseJsonObject.addProperty("searchQuery", title);
	                responseJsonObject.addProperty("status", "Success");
	                responseJsonObject.addProperty("message", "Some movies were found");
	                responseJsonObject.add("movies", jsonArrayMovies);
	                
	                response.getWriter().write(responseJsonObject.toString());
	            }else if(jsonArrayMovies.size() == 0) {
	            	// no movies in the search
	            	responseJsonObject.addProperty("url", url);
	    			responseJsonObject.addProperty("baseUrl", baseUrl);
	    			responseJsonObject.addProperty("websiteUrl", websiteUrl);
	                responseJsonObject.addProperty("query", getMoviesQuery);
	                responseJsonObject.addProperty("pageNum", pageNum);
	                responseJsonObject.addProperty("totalPages", totalPages);
	                responseJsonObject.addProperty("totalResults", totalResults);
	                responseJsonObject.addProperty("searchQuery", title);
	                responseJsonObject.addProperty("status", "Error");                
	                responseJsonObject.addProperty("message", "Sorry, the movies that that you searched for are not in our databases.");
	            	response.getWriter().write(responseJsonObject.toString());
	            }
	            else
	            {
	            	responseJsonObject.addProperty("url", url);
	    			responseJsonObject.addProperty("baseUrl", baseUrl);
	    			responseJsonObject.addProperty("websiteUrl", websiteUrl);
	                responseJsonObject.addProperty("query", getMoviesQuery);
	                responseJsonObject.addProperty("pageNum", pageNum);
	                responseJsonObject.addProperty("totalPages", totalPages);
	                responseJsonObject.addProperty("totalResults", totalResults);
	                responseJsonObject.addProperty("searchQuery", title);
	            	responseJsonObject.addProperty("status", "Error");                
	                responseJsonObject.addProperty("message", "Sorry for the inconvenience, but our servers are experiencing difficulties. Please try again later.");
	            	response.getWriter().write(responseJsonObject.toString());
	            }
	    		countStatement.close();
	    		getMoviesStatement.close();
	    		resultSet.close();
	    		connection.close();
	    	}
	        catch (Exception e) 
	        {
	        	JsonObject responseJsonObject = new JsonObject();
	        	responseJsonObject.addProperty("status", "Error");                
                responseJsonObject.addProperty("message", e.toString());
            	response.getWriter().write(responseJsonObject.toString());
	    		e.printStackTrace();
	    	}
	    }
}