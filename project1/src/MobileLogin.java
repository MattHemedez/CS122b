import java.io.IOException;
import java.io.PrintWriter;
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

import org.jasypt.util.password.StrongPasswordEncryptor;

import com.google.gson.JsonObject;

/**
 * Servlet implementation class MobileLogin
 */
@WebServlet(name = "MobileLogin", urlPatterns = "/api/mobile-login")
public class MobileLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MobileLogin() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Get the username and password from the mobile form
		String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        // get the printwriter for writing response
        PrintWriter out = response.getWriter();

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
    		
    		// Create Query
    		String query = "SELECT c.firstName, c.lastName, c.id, c.password FROM customers AS c WHERE c.email LIKE ? OR c.email LIKE ?;";
    		
    		//Create Statement Connection
	        PreparedStatement statement = dbcon.prepareStatement(query);
    		
	        //Modify Prepared Statement
	        statement.setString(1, username);
	        username += "@%";
	        statement.setString(2, username);
	        
    		// execute query
    		ResultSet resultSet = statement.executeQuery();
    		boolean success = false;
    		
    		if (resultSet.next()) {
                // Login success:
                // set this user into the session
    			String encryptedPassword = resultSet.getString("password");
    			success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
    			if(success == true)
    			{
    				String id = resultSet.getString("id");
                    request.getSession().setAttribute("user", new User(username, id));

                    JsonObject responseJsonObject = new JsonObject();
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");

                    response.getWriter().write(responseJsonObject.toString());
    			}
    			else
    			{
    				// Password Is Incorrect
                    JsonObject responseJsonObject = new JsonObject();
                    responseJsonObject.addProperty("status", "fail");                
                    responseJsonObject.addProperty("message", "Entered incorrect password, please try again");
                    response.getWriter().write(responseJsonObject.toString());
    			}
            } else {
                // Username Not Found
                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "fail");                
                responseJsonObject.addProperty("message", "The username doesn't exist, please try again");
                response.getWriter().write(responseJsonObject.toString());
            }
    		resultSet.close();
    		statement.close();
    		dbcon.close();
    		
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
        	JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");                
            responseJsonObject.addProperty("message", "The server is experience some difficulties. Please try again another time.");
            response.getWriter().write(responseJsonObject.toString());
    		e.printStackTrace();
    	}
	}

}
