import com.google.gson.JsonObject;
import org.jasypt.util.password.StrongPasswordEncryptor;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    	String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse); // Take out after trying

        // Verify reCAPTCHA
        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {
        	JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("recaptcha", "fail");
            responseJsonObject.addProperty("status", "fail");

            responseJsonObject.addProperty("message", "recaptcha verification failed, please try again");


            response.getWriter().write(responseJsonObject.toString());
            return;
        }
    	
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
    		
    		// Create Query
    		String query = "SELECT c.firstName, c.lastName, c.id, c.password FROM customers AS c WHERE c.email LIKE ? OR c.email LIKE ?;";
    		
    		//Create Statement Connection
	        PreparedStatement statement = connection.prepareStatement(query);
    		
    		// get the user input for the query
    		String username = request.getParameter("username");
	        String password = request.getParameter("password");
	        
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
                    responseJsonObject.addProperty("recaptcha", "success");

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
        	JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");                
            responseJsonObject.addProperty("message", "The server is experience some difficulties. Please try again another time.");
            response.getWriter().write(responseJsonObject.toString());
    		e.printStackTrace();
    	}
    
        
        
        
        
        
        
        
        
     
    }
}
