import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

//
@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;


    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
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
    		
    		String query = "SELECT c.firstName, c.lastName FROM customers AS c WHERE c.password LIKE '" + password + "' AND c.email LIKE '" + username + "@%' OR c.password LIKE '" + password + "' AND c.email LIKE '" + username + "';";
    		
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
                responseJsonObject.addProperty("message", "The username password combination doesn't exist" + resultSet.getFetchSize());
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
}
