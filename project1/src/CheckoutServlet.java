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

//
@WebServlet(name = "CheckoutServlet", urlPatterns = "/api/checkout")
public class CheckoutServlet extends HttpServlet {
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

        try 
        {
    		Class.forName("com.mysql.jdbc.Driver").newInstance();
    		// create database connection
    		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
    		
    		// get the user input for the query
    		String creditCardNum = request.getParameter("creditcardnumber");
	        String firstName = request.getParameter("firstname");
	        String lastName = request.getParameter("lastname");
	        String expDate = request.getParameter("expirationdate");
	        String userId = ((User)request.getSession().getAttribute("user")).getId();
	        		
	        // Declare our statement
	     	PreparedStatement statement = connection.prepareCall("{call checkout(?, ?, ?, ?, ?)}");

	     	// Prepares the statement
	     	statement.setString(1, userId);
	     	statement.setString(2, creditCardNum);
	     	statement.setString(3, firstName);
	     	statement.setString(4, lastName);
	     	statement.setString(5, expDate);

	     	// Perform the query
	     	ResultSet rs = statement.executeQuery();
    		
     		if (rs.next()) 
    		{
                // Checkout success:
                // set this user into the session
    			String status = rs.getString("status");
    			String message = rs.getString("message");
    			if(status.equals("ERROR"))
    			{
    				JsonObject responseJsonObject = new JsonObject();
	                responseJsonObject.addProperty("status", "fail");
	                responseJsonObject.addProperty("message", message);
	                response.getWriter().write(responseJsonObject.toString());
    			}
    			else
    			{
	                JsonObject responseJsonObject = new JsonObject();
	                responseJsonObject.addProperty("status", "success");
	                responseJsonObject.addProperty("message", message);
	                response.getWriter().write(responseJsonObject.toString());
    			}
    		} 
    		else 
    		{
                // Checkout fail
                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "Database Error");
                response.getWriter().write(responseJsonObject.toString());
            }
    		
    		rs.close();
    		statement.close();
    		connection.close();
        } 
        
        catch (Exception e) 
        {
        	// Checkout fail
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", e.toString());

            response.getWriter().write(responseJsonObject.toString());
    	}
    }
}
