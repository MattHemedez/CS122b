

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class MovieSuggestion
 */
@WebServlet("/movie-suggestion")
public class MovieSuggestion extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MovieSuggestion() {
        super();
        // TODO Auto-generated constructor stub
    }

    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JsonArray jsonArray = new JsonArray(); // send back out to the js

		
		try {
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
            
			String movieQuery= request.getParameter("query").trim();
			String splitQuery[] = movieQuery.split(" ");
					
					
			String query = "SELECT m.title, m.id FROM movies AS m WHERE MATCH (title) AGAINST (? IN BOOLEAN MODE) LIMIT 10;";
			PreparedStatement statement = dbcon.prepareStatement(query);

			
			String newString = "";
			for(int i=0; i<splitQuery.length; ++i) {
				newString += "+" + splitQuery[i] + "* ";
			}
			statement.setString(1, newString);
			
			
			ResultSet rs = statement.executeQuery();
			while(rs.next()) {
				String movieTitle = rs.getString("title");
				String movieId = rs.getString("id");
				
				JsonObject jsonMovieData = new JsonObject();
				jsonMovieData.addProperty("value",movieTitle );
				jsonMovieData.addProperty("data", movieId);
				
				jsonArray.add(jsonMovieData);
			}
			response.getWriter().write(jsonArray.toString());
            response.setStatus(200);
            rs.close();
            statement.close();
            dbcon.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
