
import java.io.IOException;
import java.util.*;
import java.io.*;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;
// For Datasource connection and Resultset/Prepared statement
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;


import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SAXParserStarsInMovies extends DefaultHandler {
	
    List<StarsInMovies> myStars;

    private String tempVal;

    private StarsInMovies tempStars;

	private static MysqlDataSource dataSource;
	

    public SAXParserStarsInMovies() {
    	myStars = new ArrayList<StarsInMovies>();
        initializeDS();
    }
    
    public void initializeDS() {    	
    	dataSource = new MysqlDataSource();
    	// Creating the properties 
    	dataSource.setServerName("localhost");
    	dataSource.setPortNumber(3306);
    	dataSource.setDatabaseName("moviedb");
        dataSource.setUser("root");
        dataSource.setPassword("asd123");
    
    }
    public void runExample(Connection c)throws FileNotFoundException, SQLException{
        parseDocument();
//        printData();
        writeData(c);
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            javax.xml.parsers.SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("casts124.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Iterate through the list and print
     * the contents
     */
    private void printData() {

        System.out.println("No of Movies '" + myStars.size() + "'.");

        Iterator<StarsInMovies> it = myStars.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

    private void writeData(Connection c)throws FileNotFoundException, SQLException{
        PrintWriter stars_in_movie = null;
        try{
            System.out.println("Writing actors to file 'actors.csv' ...");

            stars_in_movie = new PrintWriter(new File("stars_in_movie.csv")) ; 
            
            
            Iterator<StarsInMovies> it = myStars.iterator();
            while(it.hasNext()){
            	StarsInMovies currStar = it.next();
            	
            	String starID = getStarId(c, currStar.getId(), currStar.getName()); // Use the movieID and starName to find the star's ID
            	if(!starID.equals("null") && !alreadyExists(c,starID, currStar.getId()))
            		stars_in_movie.println(starID + "," + currStar.getId());  // StarID-MovieID format

            }
           
        }finally{
            if(stars_in_movie!=null)
            	stars_in_movie.close();

            System.out.println("Finished Parsing actors63.xml");
        }
    }
    
    
    private boolean alreadyExists(Connection c, String starId, String movieId) throws SQLException {
    	String query="SELECT * FROM moviedb.stars_in_movies WHERE starId= ? AND movieID=?;";
    	PreparedStatement stmt = c.prepareStatement(query);
    	stmt.setString(1, starId);
    	stmt.setString(2, movieId);
    	ResultSet actors = stmt.executeQuery();
    	if(actors.next())
    		return true; // if star in movies already exists
 
    	return false;
    }

    // Retrieves the StarID in that Movie
    private String getStarId(Connection c,String movieId, String starName) throws SQLException {    	
    	String query = "SELECT sm.id " + 
    					"FROM(SELECT s.id FROM movies AS m, stars AS s " + 
    					"WHERE m.id= ? AND s.name=?)AS sm;";
    	
    	PreparedStatement stmt = c.prepareStatement(query);
    	stmt.setString(1, movieId);
    	stmt.setString(2, starName);
    	

    	ResultSet actor = stmt.executeQuery();
    	if(actor.next())
    		return actor.getString("id"); // if movie exist return true
    	return "null";
    }
    

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("m")) {
            //create a new instance of cast member
            tempStars = new StarsInMovies();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("m")) {
            //add it to the list
            myStars.add(tempStars);

        } else if (qName.equalsIgnoreCase("f")) {
        	tempStars.setId(tempVal);
        } else if (qName.equalsIgnoreCase("a")) {
        	tempStars.setName(tempVal);
        }
        	
        	
        	
        	
        

    }
    


    public static void main(String[] args)throws FileNotFoundException{
    	SAXParserStarsInMovies spe = new SAXParserStarsInMovies();

    	
    	
    	Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
    	try {
	      conn = dataSource.getConnection();
	      
	      
	      
	      spe.runExample(conn);
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally {
    		try {
		        if (rs != null) {
		            rs.close();
		          }
				if (stmt != null) {
				  stmt.close();
				}
				if (conn != null) {
				  conn.close();
				}
    			
    		}catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    	
    	
    }

}
