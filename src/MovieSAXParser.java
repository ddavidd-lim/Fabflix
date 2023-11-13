
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import Entities.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class MovieSAXParser extends DefaultHandler { // SAX PARSER IS LIKE EVENT LISTENERS LOOKING FOR CERTAIN TAGS

    List<Movie> movies;
    List<Star> stars;
    List<Genre> genres;
    List<CreditCard> cards;
    private String tempVal;

    //to maintain context
    private Movie tempMovie = new Movie();
    private Star tempStar = new Star();
    private Genre tempGenre = new Genre();
    private CreditCard tempCard;

    public MovieSAXParser() {
        movies = new ArrayList<Movie>();
        stars = new ArrayList<Star>();
        genres = new ArrayList<Genre>();
        cards = new ArrayList<CreditCard>();
    }

    public void runExample() {
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
            sp.parse("actors63.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        } catch (Exception e)
        {
            System.out.println(e);
        }
    }

    /**
     * Iterate through the list and print
     * the contents
     */
    private void printData() {

        System.out.println("No of Movies '" + movies.size() + "'.");

        Iterator<Movie> it = movies.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        // Movies
        if (qName.equalsIgnoreCase("film")) {
            //create a new instance of movie
            this.tempMovie = new Movie();
        }
        if (qName.equalsIgnoreCase("cat")) {
            this.tempGenre = new Genre();
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
        } else if (qName.equalsIgnoreCase("dirn")) {
            tempMovie.setDirector(tempVal);
        } else if (qName.equalsIgnoreCase("dirn")) {
            tempMovie.setDirector(tempVal);
        }

    }

//    public void insertIntoDB(){
//        try (Connection conn = dataSource.getConnection()) {
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
