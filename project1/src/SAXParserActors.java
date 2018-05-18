
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

public class SAXParserActors extends DefaultHandler {
	private String currentID; 
	
    List<Actors> myActors;

    private String tempVal;

    private Actors tempActors;

	private static MysqlDataSource dataSource;
	

    public SAXParserActors() {
    	myActors = new ArrayList<Actors>();
        initializeDS();
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
            sp.parse("actors63.xml", this);

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

        System.out.println("No of Movies '" + myActors.size() + "'.");

        Iterator<Actors> it = myActors.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

    private void writeData(Connection c)throws FileNotFoundException, SQLException{
        PrintWriter actors = null;
        try{
            System.out.println("Writing actors to file 'actors.csv' ...");

            actors = new PrintWriter(new File("actors.csv")) ; 
            
            
            Iterator<Actors> it = myActors.iterator();
            while(it.hasNext()){
            	Actors currActor = it.next();
            	
                if(!existingActor(c, currActor.getName(), currActor.getDOB())) { // If new actor add here
                	
                	actors.println( newID() + "," + currActor.getName() + "," + currActor.getDOB());
                }
            }
            

        }finally{
            if(actors!=null)
            	actors.close();

            System.out.println("Finished Parsing actors63.xml");
        }
    }
    
    private String newID() {
    	int incrementedID = Integer.parseInt(currentID.substring(2)) +1;
    	currentID = currentID.substring(0, 2) + String.valueOf(incrementedID);
    	
    	return currentID;
    }
    
    // Checks for duplicate movies, skip if already in database
    private boolean existingActor(Connection c,String starName, String DOB) throws SQLException {
    	if(DOB == null) {
    		tempActors.setDOB("0");
    		DOB = "0";
    	}
    		
    	ResultSet actor = null;
    	
    	if(DOB.equals("0")) {
    		String query = "SELECT * FROM moviedb.stars AS s WHERE s.name=? AND s.birthYear IS NULL;";
        	PreparedStatement stmt = c.prepareStatement(query);

        	stmt.setString(1, starName);

        	actor = stmt.executeQuery();
    		
    	}else {
    		String query = "SELECT * FROM moviedb.stars AS s WHERE s.name=? AND s.birthYear = ?;";
        	PreparedStatement stmt = c.prepareStatement(query);

        	stmt.setString(1, starName);
        	stmt.setInt(2, Integer.parseInt(DOB));

        	actor = stmt.executeQuery();

    	}
    	
    	if(actor!= null && actor.next())
    		return true; // if movie exist return true
    	return false;
    }
    
    public void maxId(Connection c) throws SQLException {
    	String query = "SELECT MAX(id) FROM moviedb.stars;";
    	
    	PreparedStatement stmt = c.prepareStatement(query);
    	
    	ResultSet maxId = stmt.executeQuery();
    	if(maxId.next())
    		currentID = maxId.getString("MAX(id)");
    	
    	stmt.close();
    }


    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("actor")) {
            //create a new instance of employee
            tempActors = new Actors();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("actor")) {
            //add it to the list
            myActors.add(tempActors);

        } else if (qName.equalsIgnoreCase("stagename")) {
        	tempActors.setName(tempVal);
        } else if (qName.equalsIgnoreCase("dob")) {
        	if(tempVal.matches("[0-9]+") && tempVal.length() >0)
        		tempActors.setDOB(tempVal);
        	else
        		tempActors.setDOB("0");
        }
    }
    


    public static void main(String[] args)throws FileNotFoundException{
    	SAXParserActors spe = new SAXParserActors();

    	
    	
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
