import com.google.gson.JsonObject;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

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
    		
    		// get the user input for the query
    		String creditCardNum = request.getParameter("creditcardnumber");
	        String firstName = request.getParameter("firstname");
	        String lastName = request.getParameter("lastname");
	        String expDate = request.getParameter("expirationdate");
	        String userId = ((User)request.getSession().getAttribute("user")).getId();
	        		
	        // Declare our statement
	     	PreparedStatement statement = dbcon.prepareCall("{call checkout(?, ?, ?, ?, ?)}");

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
    		dbcon.close();
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
