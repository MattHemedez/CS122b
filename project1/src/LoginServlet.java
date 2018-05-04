import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
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


        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify username/password
        */
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        
        // get the printwriter for writing response
        PrintWriter out = response.getWriter();

        
        
        try {
    		Class.forName("com.mysql.jdbc.Driver").newInstance();
    		// create database connection
    		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
    		// declare statement
    		Statement statement = connection.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, 
    				   				ResultSet.CONCUR_READ_ONLY);
    		// get the user input for the query
    		String username = request.getParameter("username");
	        String password = request.getParameter("password");
	        
	        

	        String query = "SELECT c.firstName, c.lastName, c.id " + 
    				"FROM customers AS c " + 
    				"WHERE c.password LIKE '" + password + "' AND (c.email LIKE '"+ username + "' OR c.email LIKE '" + username +"@%');";
    		 
    		// execute query
    		ResultSet resultSet = statement.executeQuery(query);
    		
    		if (resultSet.next()) {
                // Login success:
                // set this user into the session
    			String id = resultSet.getString("id");
                request.getSession().setAttribute("user", new User(username, id));

                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");

                response.getWriter().write(responseJsonObject.toString());
            } else {
                // Login fail
                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "fail");
                
                responseJsonObject.addProperty("message", "The username password combination doesn't exist, please try again");


                response.getWriter().write(responseJsonObject.toString());
            }
    		resultSet.close();
    		statement.close();
    		connection.close();
    		

    		
        } catch (Exception e) {
    		/*
    		 * After you deploy the WAR file through tomcat manager webpage,
    		 *   there's no console to see the print messages.
    		 * Tomcat append all the print messages to the file: tomcat_directory/logs/catalina.out
    		 * 
    		 * To view the last n lines (for example, 100 lines) of messages you can use:
    		 *   tail -100 catalina.out
    		 * This can help you debug your program after deploying it on AWS.
    		 */
    		e.printStackTrace();
    		
    		out.println("<body>");
    		out.println("<p>");
    		out.println("Exception in doGet: " + e.getMessage());
    		out.println("</p>");
    		out.print("</body>");
    	}
    
        
        
        
        
        
        
        
        
     
    }
}
