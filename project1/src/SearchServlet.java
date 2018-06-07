import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonObject;

@WebServlet("/SearchServlet")
public class SearchServlet extends HttpServlet {
	 private static final long serialVersionUID = 1L;

	 	private String getGenre(HttpServletRequest request) {
 			String param = request.getParameter("genre");
	 		return param;
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
	    		
	    		// prepare query
	    		String innerOrderBy = "m.";
	    		
	    		
	    		
	    		if(orderBy.equals("rating"))
	    			innerOrderBy = "r.";
	    		
	    		String query ="SELECT COUNT(DISTINCT m.id) AS total "
	    				+ "FROM (SELECT DISTINCT m.id, m.title, m.director, m.year, r.numVotes, r.rating "
	    				+ "FROM movies AS m LEFT JOIN ratings AS r ON m.id = r.movieId LEFT JOIN stars_in_movies AS sm ON m.id = sm.movieId LEFT JOIN stars AS s ON sm.starId = s.id LEFT JOIN genres_in_movies AS gm ON m.id = gm.movieId LEFT JOIN genres AS g ON gm.genreId = g.id " 
	    				+ "WHERE (MATCH (title) AGAINST (? IN BOOLEAN MODE)) AND m.year LIKE ? AND m.director LIKE ? AND IFNULL(g.name, '') LIKE ? AND IFNULL(s.name, '') LIKE ? ) AS m;";
	    				
	    				
	    		String query2 ="SELECT m.id, m.title, m.director, m.year, m.numVotes, m.rating, GROUP_CONCAT(DISTINCT s.id,':', s.name SEPARATOR ',') AS stars, GROUP_CONCAT(DISTINCT g.name SEPARATOR ',') AS genres "
	    				+ "FROM (SELECT DISTINCT m.id, m.title, m.director, m.year, r.numVotes, r.rating "
	    				+ "FROM movies AS m LEFT JOIN ratings AS r ON m.id = r.movieId LEFT JOIN stars_in_movies AS sm ON m.id = sm.movieId LEFT JOIN stars AS s ON sm.starId = s.id LEFT JOIN genres_in_movies AS gm ON m.id = gm.movieId LEFT JOIN genres AS g ON gm.genreId = g.id "
	    				+ "WHERE (MATCH (title) AGAINST (? IN BOOLEAN MODE)) AND m.year LIKE ? AND m.director LIKE ? AND IFNULL(g.name, '') LIKE ? AND IFNULL(s.name, '')LIKE ? "
	    				+ "ORDER BY "+innerOrderBy+orderBy  +" " +order +" LIMIT ? OFFSET ?) AS m LEFT JOIN stars_in_movies AS sm ON m.id = sm.movieId LEFT JOIN stars AS s ON sm.starId = s.id LEFT JOIN genres_in_movies AS gm ON m.id = gm.movieId LEFT JOIN genres AS g ON gm.genreId = g.id "
	    				+ "GROUP BY m.id ORDER BY m."+orderBy + " "+order + ";";
	    		
	            PreparedStatement statement = dbcon.prepareStatement(query);
	            PreparedStatement statement2 = dbcon.prepareStatement(query2);

	            // Setting the title 
	    		if(title != null && !title.equals("")) {
//	    			query += "m.title LIKE '%" + title + "%' AND ";
	    			statement.setString(1, "+" + title + "*");
	    			statement2.setString(1, "+" + title + "*");

	    			
	    		}else if(ftitle != null && !ftitle.equals("")) {
//	    			query += "m.title LIKE '" + ftitle + "%' AND ";
	    			query = query.replace("(MATCH (title) AGAINST (? IN BOOLEAN MODE))", "m.title LIKE ?");
	    			query2 = query2.replace("(MATCH (title) AGAINST (? IN BOOLEAN MODE))", "m.title LIKE ?");
	    			statement = dbcon.prepareStatement(query);
	    			statement2 = dbcon.prepareStatement(query2);
	    			statement.setString(1, ftitle + "%");
	    			statement2.setString(1, ftitle + "%");

	    		}else {
	    			statement.setString(1, "*a*");
	    			statement2.setString(1, "*a*");
	    		}
	    		statement.setString(2, (year == null || year.equals("")? "%%": year)); // Year 
	    		statement2.setString(2, (year == null || year.equals("")? "%%": year)); // Year 

	    		statement.setString(3, (director == null || director.equals("")? "%%": "%" + director + "%")); // Director 
	    		statement2.setString(3, (director == null || director.equals("")? "%%": "%" + director + "%")); // Director 

	    		statement.setString(4, (genre == null || genre.equals("")? "%%": genre)); // Genre
	    		statement2.setString(4, (genre == null || genre.equals("")? "%%": genre)); // Genre

	    		statement.setString(5, (starname == null || starname.equals("")? "%%": "%" + starname + "%")); // Star Name 
	    		statement2.setString(5, (starname == null || starname.equals("")? "%%": "%" + starname + "%")); // Star Name 

	    		
	    		statement2.setInt(6, Integer.parseInt(limit));
	    		statement2.setInt(7, Integer.parseInt(offset));

	    		ResultSet resultSet = statement.executeQuery();
	    		
	    		System.out.println(statement.toString());
	    		
	    		int totalResults = 0;
	    		int totalPages = 0;
	    		while(resultSet.next())
	    		{
	    			totalResults = resultSet.getInt("total");
	    		}
	    		totalPages = (int)Math.ceil((1.0 * totalResults)/ (1.0 * Integer.parseInt(limit)));
	    		
//	    		statement.getMoreResults();
	    		System.out.println(totalResults);
	    		System.out.println("Q2: " + statement2.toString());

	    		resultSet = statement2.executeQuery();

	    		String url = "SearchServlet?" +
	    	             request.getQueryString();
	    		
	    		
	    		String baseUrl = "SearchServlet";
	    		
	    		baseUrl = baseUrl.substring(0,baseUrl.length()-13);
	    		
	    		ArrayList<String> movieIDs = new ArrayList<String>();
	    		HashMap<String, HashSet<String>> actors = new HashMap<String, HashSet<String>>();
	    		HashMap<String, HashSet<String>> genres = new HashMap<String, HashSet<String>>();
	    		HashMap<String,String> movieTitles = new HashMap<String, String>();
	    		HashMap<String,String> movieRating = new HashMap<String, String>();
	    		HashMap<String,String> movieDirector = new HashMap<String,String>();
	    		HashMap<String,String> movieYear = new HashMap<String,String>();
	    		
	    		String temp;
	    		
	    		while(resultSet.next()) {
	    			String movieID = resultSet.getString("id");
	    			movieTitles.put(movieID, resultSet.getString("title"));
	    			
	    			movieIDs.add(resultSet.getString("id"));
	    			
	    			temp = resultSet.getString("rating");
	    			if(resultSet.wasNull())
	    				movieRating.put(movieID, "N/A");
	    			else 
	    				movieRating.put(movieID, temp);
	    			
	    			temp = resultSet.getString("Director");
	    			if(resultSet.wasNull())
	    				movieDirector.put(movieID, "N/A");
	    			else
	    				movieDirector.put(movieID, temp);
	    			
	    			temp = resultSet.getString("year");
	    			if(resultSet.wasNull())
	    				movieYear.put(movieID, "N/A");
	    			else
	    				movieYear.put(movieID, temp);
	    			
	    			actors.put(movieID, new HashSet<String>());
	    			genres.put(movieID, new HashSet<String>());
	    			
	    			temp = resultSet.getString("stars");
	    			if(!resultSet.wasNull())
	    			{
		    			StringTokenizer actorsST = new StringTokenizer(temp,",");
		    			while(actorsST.hasMoreTokens()) {
		    				actors.get(movieID).add(actorsST.nextToken());
		    			}
	    			}
	    			else
	    				actors.get(movieID).add("N/A");
	    			
	    			temp = resultSet.getString("genres"); 
	    			if(!resultSet.wasNull())
	    			{
		    			StringTokenizer genreST = new StringTokenizer(temp,",");
		    			while(genreST.hasMoreTokens()) {
		    				genres.get(movieID).add(genreST.nextToken());
		    			}
	    			}
	    			else
	    				genres.get(movieID).add("N/A");
	    			
	    		}
    		
	    		if (movieIDs.size()>0) {
	    			request.setAttribute("movieYear", movieYear);
	    			request.setAttribute("movieDirector", movieDirector);
	    			request.setAttribute("movieRating", movieRating);
	    			request.setAttribute("movieIDs", movieIDs);
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
	            	response.sendRedirect("/project1/noResults.html");
	            	
//	            	request.setAttribute("movies", "Error no movies found with search criteria");
	            	
//	    			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/movielist.jsp");
//	                dispatcher.forward(request, response);
	            }	    		
	    		statement.close();
	    		statement2.close();
	    		dbcon.close();
	    	}
	        catch (Exception e) 
	        {
	    		e.printStackTrace();
	    	}
	    }
}