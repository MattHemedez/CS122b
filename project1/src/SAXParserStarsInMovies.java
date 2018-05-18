
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
	
	private HashMap<String,String> starsIdMovie;

	private HashMap<String,String> starsInMovie;

    public SAXParserStarsInMovies() throws SQLException {
    	myStars = new ArrayList<StarsInMovies>();
        starsIdMovie = new HashMap<String,String>();
        starsInMovie = new HashMap<String,String>();
        initializeDS();
	    Connection conn = dataSource.getConnection();
	    initializeStars(conn);
    }
    
    public void initializeDS() {    	
    	dataSource = new MysqlDataSource();
    	// Creating the properties 
    	dataSource.setServerName("localhost");
    	dataSource.setPortNumber(3306);
    	dataSource.setDatabaseName("moviedb");
        dataSource.setUser("mytestuser");
        dataSource.setPassword("mypassword");
    
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
            System.out.println("Writing actor relations to file 'stars_in_movie.csv' ...");

            stars_in_movie = new PrintWriter(new File("stars_in_movie.csv")) ; 
            
            
            Iterator<StarsInMovies> it = myStars.iterator();
            while(it.hasNext()){
            	StarsInMovies currStar = it.next();
            	
            	String starID = getStarId(currStar.getName()); // Use the movieID and starName to find the star's ID
            	if(starID!= null && !alreadyExists(starID,currStar.getId()))
            		stars_in_movie.println(starID + "," + currStar.getId());  // StarID-MovieID format

            }
           
        }finally{
            if(stars_in_movie!=null)
            	stars_in_movie.close();

            System.out.println("Finished Parsing casts124.xml");
        }
    }
    
    
    private boolean alreadyExists(String starId, String movieId) {
    	if(starsInMovie.get(starId) == movieId) {
    		return true;
    	}
    	return false;
    	
    }

    private String getStarId(String name) {
    	if(starsIdMovie.get(name) != null) {
    		return starsIdMovie.get(name);
    	}
    	return null;
    }
    
    
    // Retrieves the StarID in that Movie
    private void initializeStars(Connection c) throws SQLException {    	
    	String query = "SELECT id,name FROM moviedb.stars;";
    	PreparedStatement stmt = c.prepareStatement(query);
    	ResultSet actor = stmt.executeQuery();
    	while(actor.next()) {
    		String id = actor.getString("id");
    		String name = actor.getString("name");
    		starsIdMovie.put(name,id);
    	}
    	query = "SELECT starId,movieId FROM moviedb.stars_in_movies;";
    	stmt = c.prepareStatement(query);
    	ResultSet inMovie = stmt.executeQuery();

    	while(inMovie.next()) {
    		String starId = inMovie.getString("starId");
    		String movieId = inMovie.getString("movieId");
    		starsInMovie.put(starId, movieId);
    		
    	}
    	
    	
    	
    	stmt.close();
    	
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
    


    public static void main(String[] args)throws FileNotFoundException, SQLException{
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
