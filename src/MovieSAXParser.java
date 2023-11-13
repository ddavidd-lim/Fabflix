import Entities.CreditCard;
import Entities.Genre;
import Entities.Movie;
import Entities.Star;
import jakarta.servlet.ServletConfig;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.lang.System.out;

public class MovieSAXParser extends DefaultHandler { // SAX PARSER IS LIKE EVENT LISTENERS LOOKING FOR CERTAIN TAGS
    // Create a dataSource which registered in web.
    List<Movie> movies;
    List<Star> stars;
    List<Genre> genres;
    List<CreditCard> cards;
    private String tempVal;
    String max_movie_id;
    String max_star_id;

    //to maintain context
    private Movie tempMovie;
    private Star tempStar;
    private Genre tempGenre;


    public MovieSAXParser() {
        movies = new ArrayList<Movie>();
        stars = new ArrayList<Star>();
        genres = new ArrayList<Genre>();
        cards = new ArrayList<CreditCard>();
    }

    public String incrementId(String id){
        String beginner_chars = id.substring(0, 2);
        int incremented_id = Integer.parseInt(id.substring(2)) + 1;
        return beginner_chars + incremented_id;
    }

    public void getMaxMovieId(){
        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        try {
            Connection conn = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            String query = "SELECT MAX(id) as movie_id FROM movies";
            PreparedStatement statement = conn.prepareStatement(query);
            // Perform the query
            ResultSet rs = statement.executeQuery();
            // Check if query returned anything
            if (rs.next()) {
                max_movie_id = rs.getString("movie_id");
                System.out.println("max_movie_id = " + max_movie_id);
                System.out.println("Incremented Movie Id = " + incrementId(max_movie_id));
            }

            rs.close();
            statement.close();
        } catch (Exception e) {
            out.println("exception: " + e);
            out.print("SAXParser SQL Error");

        }
    }
    public void getMaxStarId(){
        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        try {
            Connection conn = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            String query = "SELECT MAX(id) as star_id FROM stars";
            PreparedStatement statement = conn.prepareStatement(query);
            // Perform the query
            ResultSet rs = statement.executeQuery();
            // Check if query returned anything
            if (rs.next()) {
                max_star_id = rs.getString("star_id");
                System.out.println("max_star_id = " + max_star_id);
                System.out.println("Incremented Star Id = " + incrementId(max_star_id));
            }

            rs.close();
            statement.close();
        } catch (Exception e) {
            out.println("exception: " + e);
            out.print("SAXParser SQL Error");

        }
    }


    public void runExample() {
        getMaxMovieId();
        getMaxStarId();
        parseDocument();
        printData();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("test.xml", this);

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

        out.println("No of Movies '" + movies.size() + "'.");

        Iterator<Movie> it = movies.iterator();
        while (it.hasNext()) {
            out.println(it.next().toString());
        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        // Movies
        if (qName.equalsIgnoreCase("film")) {
            //create a new instance of movie
            tempMovie = new Movie();
        }
        if (qName.equalsIgnoreCase("actor")) {
            //create a new instance of movie
            tempStar = new Star();
        }
        if (qName.equalsIgnoreCase("cat")) {
            tempGenre = new Genre();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("film")) {
            movies.add(tempMovie);
        }
        if (qName.equalsIgnoreCase("actor")) {
            stars.add(tempStar);
        }
        if (qName.equalsIgnoreCase("cat")) {
            tempGenre.setName(tempVal);
            genres.add(tempGenre);
            tempMovie.addGenre(tempGenre);
        } else if (qName.equalsIgnoreCase("t")) {
            tempMovie.setTitle(tempVal);
        } else if (qName.equalsIgnoreCase("dirn")) {
            tempMovie.setDirector(tempVal);
        } else if (qName.equalsIgnoreCase("year")) {
            tempMovie.setYear(Integer.parseInt(tempVal));

        } else if (qName.equalsIgnoreCase("dob")) {
            tempStar.setBirthYear(Integer.parseInt(tempVal));
        } else if (qName.equalsIgnoreCase("stagename")) {
            tempMovie.setDirector(tempVal);
        }
    }

//    public void insertIntoDB(){
    //    String loginUser = "mytestuser";
    //    String loginPasswd = "My6$Password";
    //    String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
//        try {
//            Connection conn = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
//            query = "INSERT INTO sales (customerId, movieId, salesDate) VALUES (?, ?, ?)";
//            statement = conn.prepareStatement(query); // want customer_id out of query
//            statement.setInt(1, customer_id);
//            statement.setString(2, movie_id);
//            statement.setDate(3, date);
//            System.out.println("Before Insert");
//            int rowsAffected = statement.executeUpdate();
//            if (rs.next()) {
//                // Login success:
//
//                // set this user into the session
//                request.getSession().setAttribute("user", new User(username));
//
//                responseJsonObject.addProperty("status", "success");
//                responseJsonObject.addProperty("message", "success");
//
//            }
//        }
//    }

    public static void main(String[] args) {
        MovieSAXParser spe = new MovieSAXParser();
        spe.runExample();
    }

}
