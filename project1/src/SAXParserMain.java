
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

public class SAXParserMain extends DefaultHandler {
	private HashMap<String, Integer> knownGenres = new HashMap<String, Integer>() {{
	    put("Action",1);
	    put("Adult",2);
	    put("Adventure",3);
	    put("Animation",4);
	    put("Biography",5);
	    put("Comedy",6);
	    put("Crime",7);
	    put("Documentary",8);
	    put("Drama",9);
	    put("Family",10);
	    put("Fantasy",11);
	    put("History",12);
	    put("Horror",13);
	    put("Music",14);
	    put("Musical",15);
	    put("Mystery",16);
	    put("Reality-TV",17);
	    put("Romance",18);
	    put("Sci-Fi",19);
	    put("Sport",20);
	    put("Thriller",21);
	    put("War", 22);
	    put("Western",23);

	}};
 
    List<Movies> myMovies;

    private String tempVal;

    private Movies tempMovie;

    private ArrayList<String> movieGenres;
    
	private static MysqlDataSource dataSource;
	
	private int maxGenreID;

    public SAXParserMain() {
        myMovies = new ArrayList<Movies>();
        movieGenres = new ArrayList<String>();
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
            sp.parse("mains243.xml", this);

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

        System.out.println("No of Movies '" + myMovies.size() + "'.");

        Iterator<Movies> it = myMovies.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

    private void writeData(Connection c)throws FileNotFoundException, SQLException{
        PrintWriter movies = null;
        PrintWriter genres = null;
        PrintWriter genres_in_movies = null;
        try{
            System.out.println("Writing movies to file 'movies.csv' genres_in_movies to 'genres_in_movies.csv'...");

            movies = new PrintWriter(new File("movies.csv")) ; 
            genres_in_movies = new PrintWriter(new File("genres_in_movies.csv"));
            
            
            Iterator<Movies> it = myMovies.iterator();
            while(it.hasNext()){
                Movies currMovie = it.next();
                if(!existingMovie(c, currMovie.getTitle())) {
                	HashMap <String, Integer> movieGenres = currMovie.getGenres();
                	
                	for(Map.Entry<String, Integer> e: movieGenres.entrySet()) {
                		genres_in_movies.println(knownGenres.get(e.getKey()) + "," + currMovie.getId()); // Grabs the genreID->movieID pair
                	}
                	
                	movies.println( currMovie.getId() + "," + currMovie.getTitle().replaceAll(",", "")+ "," +currMovie.getYear()+ "," +currMovie.getDirector());
                }
            }
            
            System.out.println("Writing genres to file 'genres.csv'...");
            genres = new PrintWriter(new File("genres.csv")) ;
            for(Map.Entry<String, Integer> e: knownGenres.entrySet()){
            	if(e.getValue() > 23 ) // Don't include the original 23 id's
            		genres.println(e.getValue()+ "," + e.getKey());
            }
            

        }finally{
            if(movies!=null)
            	movies.close();
            if(genres != null)
            	genres.close();
            if(genres_in_movies != null)
            	genres_in_movies.close();
            System.out.println("Finished Parsing mains243.xml");
        }
    }
    
    // Checks for duplicate movies, skip if already in database
    private boolean existingMovie(Connection c,String movieTitle) throws SQLException {
    	String query = "SELECT * FROM moviedb.movies as m WHERE m.title=?;";
    	PreparedStatement stmt = c.prepareStatement(query);
    	stmt.setString(1, movieTitle);
    	ResultSet movie = stmt.executeQuery();
    	
    	if(movie.next())
    		return true; // if movie exist return true
    	return false;
    }
    
    public void maxId(Connection c) throws SQLException {
    	String query = "SELECT MAX(id) FROM moviedb.genres;";
    	
    	PreparedStatement stmt = c.prepareStatement(query);
    	
    	ResultSet maxId = stmt.executeQuery();
    	if(maxId.next())
    		maxGenreID = maxId.getInt("MAX(id)");
    	
    	System.out.println("THIS IS THE CURRENT MAX ID: " + maxGenreID);
    	stmt.close();
    }

    // Checks if we need to add new genre to database or its already there
    public HashMap<String,Integer> existingGenre(String currentGenre){
        String fixedGenre = currentGenre.substring(0,1).toUpperCase() + currentGenre.substring(1).toLowerCase(); // Normalizing the genre capitalization
        fixedGenre = fixedGenre.trim();
        HashMap<String,Integer> tempMap = new HashMap<String,Integer>();
        for(Map.Entry<String, Integer> e: knownGenres.entrySet()){
            if(e.getKey().substring(0,3).equals(fixedGenre.substring(0,3))) {
            	tempMap.put(e.getKey(), e.getValue());
            	return tempMap;                 // Return the full genre name for this table 

            }
        }
        
        return tempMap; // terminate and let us know that the genre is new and needs to be created
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("film")) {
            //create a new instance of employee
            tempMovie = new Movies();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("film")) {
            //add it to the list
            myMovies.add(tempMovie);

        } else if (qName.equalsIgnoreCase("fid")) {
            tempMovie.setId(tempVal);
        } else if (qName.equalsIgnoreCase("t")) {
            tempMovie.setTitle(tempVal);
        } else if (qName.equalsIgnoreCase("year")) {
            tempVal= tempVal.replaceAll("[^1-9 ]", "0");
            tempMovie.setYear(Integer.parseInt(tempVal));
        } else if(qName.equalsIgnoreCase("dirn")){
            tempMovie.setDirector(tempVal);
        } else if(qName.equalsIgnoreCase("cat") && tempVal.length() >=3){
            String fixedGenre = tempVal.substring(0,1).toUpperCase() + tempVal.substring(1).toLowerCase(); // Normalizing the genre capitalization  
            fixedGenre= fixedGenre.trim();
        	HashMap<String,Integer> validGenre = existingGenre(fixedGenre);
        	
        	
        	if(validGenre.size() <= 0) { // New genre cache the genre and add to database
        		knownGenres.put(fixedGenre, ++maxGenreID); 
        		tempMovie.setGenres(fixedGenre, maxGenreID);
        	}else {
         		Map.Entry<String,Integer> entry = validGenre.entrySet().iterator().next(); // Do not have Pairs in java 7, so had to use HashMap
        		tempMovie.setGenres(entry.getKey(), entry.getValue()); // Existing genre in our database, use full genre name
        	}
        	
        	
        }

    }
    


    public static void main(String[] args)throws FileNotFoundException{
    	SAXParserMain spe = new SAXParserMain();

    	
    	
    	Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
    	try {
	      conn = dataSource.getConnection();
	      spe.maxId(conn);
	      
	      
	      
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
