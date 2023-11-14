import Entities.CreditCard;
import Entities.Genre;
import Entities.Movie;
import Entities.Star;
import com.mysql.cj.x.protobuf.MysqlxPrepare;
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
import java.util.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static java.lang.System.out;

public class MovieSAXParser extends DefaultHandler { // SAX PARSER IS LIKE EVENT LISTENERS LOOKING FOR CERTAIN TAGS
    // Create a dataSource which registered in web.
    HashMap<String, Movie> movies;
    HashMap<String, Star> stars;
    List<Genre> genres;
    List<CreditCard> cards;
    private String tempVal;
    String max_movie_id; //max_movie_id = tt499480
    String max_star_id; //max_star_id = nm9423082

    //to maintain context

    private Movie tempMovie;
    private Star tempStar;
    private Genre tempGenre;
    private int numStarErrors = 0;
    private int numMovieErrors = 0;


    public MovieSAXParser() {
        movies = new HashMap<String, Movie>();
        stars = new HashMap<String, Star>();
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
//                System.out.println("Incremented Movie Id = " + incrementId(max_movie_id));
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
//                System.out.println("Incremented Star Id = " + incrementId(max_star_id));
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
//        parseDocument("test.xml");
        parseDocument("mains243.xml");
        parseDocument("actors63.xml");
        parseDocument("casts124.xml");
//        printData();
        out.println(" ! BEGINNING INSERTION");
        insertIntoDB();
    }

    private void parseDocument(String file) {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();
            //parse the file and also register this class for call backs
            sp.parse(file, this);

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
    private void writeToReport(String line) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("InconsistencyReport.txt", true))) {
            writer.write(line);
            writer.newLine();

//            System.out.println(line + " appended to file");

        } catch (IOException e) {
            e.printStackTrace();
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
        if (qName.equalsIgnoreCase("actor")) {
            //create a new instance of star
            tempStar = new Star();
        }
        if (qName.equalsIgnoreCase("m")) {
            //create a new instance of star
            tempStar = new Star();
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
            if (!tempMovie.getGenres().isEmpty() && tempMovie.getDirector() != null && tempMovie.getYear() != 0 && tempMovie.getTitle() != null){
                tempMovie.setMovieId(incrementId(max_movie_id));
                max_movie_id = incrementId(max_movie_id);
                movies.put(tempMovie.getFID(), tempMovie);
            }
            else{
                String element = "Movie Error: " + tempMovie.getTitle() + " | Year: " + tempMovie.getYear() + " | Director: " + tempMovie.getDirector();
                writeToReport(element);
                numMovieErrors++;
            }
        }
        if (qName.equalsIgnoreCase("actor")){
            if (tempStar.getName() != null){
                tempStar.setStarId(incrementId(max_star_id));
                max_star_id = incrementId(max_star_id);
                stars.put(tempStar.getName(), tempStar);
            }
            else{
                String element = "Star Error: " + tempStar.getName();
                numStarErrors++;
                writeToReport(element);
            }
        }
        if (qName.equalsIgnoreCase("m")) {  // casts
            Movie movie = movies.get(tempStar.getFID());
            if (movie != null){
                Star star = stars.get(tempStar.getName());
                if (star != null){
                    if (star.getName().equalsIgnoreCase(tempStar.getName())){
                        movie.addStar(star);
                    }
                }

            }
        }

        // movie xml -----------------
        if (qName.equalsIgnoreCase("cat")) {
            String gen =  null;
            if (tempVal.equalsIgnoreCase("dram")){
                tempGenre.setGenreId(9);
                gen = "Drama";
            }if (tempVal.equalsIgnoreCase("Advt")){
                tempGenre.setGenreId(3);
                gen = "Adventure";
            }if (tempVal.equalsIgnoreCase("Docu")){
                tempGenre.setGenreId(8);
                gen = "Documentary";
            }if (tempVal.equalsIgnoreCase("Fant")){
                tempGenre.setGenreId(11);
                gen = "Fantasy";
            }if (tempVal.equalsIgnoreCase("Comd")){
                tempGenre.setGenreId(6);
                gen = "Comedy";
            }if (tempVal.equalsIgnoreCase("Epic")){
                tempGenre.setGenreId(21);
                gen = "Thriller";
            }if (tempVal.equalsIgnoreCase("Biop")){
                tempGenre.setGenreId(5);
                gen = "Biography";
            }if (tempVal.equalsIgnoreCase("Susp")){
                tempGenre.setGenreId(16);
                gen = "Mystery";
            }if (tempVal.equalsIgnoreCase("Musc")){
                tempGenre.setGenreId(15);
                gen = "Musical";
            }if (tempVal.equalsIgnoreCase("cnrb")){
                tempGenre.setGenreId(7);
                gen = "Crime";
            }if (tempVal.equalsIgnoreCase("Romt")){
                tempGenre.setGenreId(18);
                gen = "Romance";
            }if (tempVal.equalsIgnoreCase("Actn")){
                tempGenre.setGenreId(1);
                gen = "Action";
            }if (tempVal.equalsIgnoreCase("West")){
                tempGenre.setGenreId(23);
                gen = "Western";
            }if (tempVal.equalsIgnoreCase("Horr")){
                tempGenre.setGenreId(13);
                gen = "Horror";
            }if (tempVal.equalsIgnoreCase("txx")) {
                tempGenre.setGenreId(17);
                gen = "Reality-TV";
            }if (tempVal.equalsIgnoreCase("Romt Comd")){
                tempGenre.setGenreId(6);
                gen = "Romantic Comedy";
            }
            else if (gen == null){
                return;
            }


            tempGenre.setName(gen);
            genres.add(tempGenre);
            tempMovie.addGenre(tempGenre);
        } // genres
        else if (qName.equalsIgnoreCase("t")) {
            tempMovie.setTitle(tempVal);
        } else if (qName.equalsIgnoreCase("fid")) {
            tempMovie.setFID(tempVal);
        } else if (qName.equalsIgnoreCase("dirn")) {
            tempMovie.setDirector(tempVal);
        } else if (qName.equalsIgnoreCase("year")) {
            try{
                tempMovie.setYear(Integer.parseInt(tempVal));
            } catch (Exception e){
                String element = "Movie Year Error: " + tempVal + "</year>";
                numMovieErrors++;
                writeToReport(element);
            }

        }
        // actor xml ----------------------
        else if (qName.equalsIgnoreCase("dob")) {
            try{
                tempStar.setBirthYear(Integer.parseInt(tempVal));
            }
            catch (Exception e){
                return;
            }
        } else if (qName.equalsIgnoreCase("stagename")) {
            tempStar.setName(tempVal);
        }
        // cast xml ------------------------
        else if (qName.equalsIgnoreCase("f")) {
            tempStar.setFID(tempVal);
        } else if (qName.equalsIgnoreCase("a")) {
            tempStar.setName(tempVal);
        }
    }

    public void insertIntoDB(){ // modifications starts at movieid = tt499470 hitchcock
        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        String query = null;

        int moviesAdded = 0;
        int starsAdded = 0;
        int genresAdded = 0;
        int rowsAffected = 0;
        try {
            Connection conn = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            // setting up PreparedStatements
            query = "INSERT INTO movies (id, title, year, director) VALUES (?, ?, ?, ?)";
            PreparedStatement movieStatement = conn.prepareStatement(query);
            query = "INSERT INTO stars (id, name, birthYear) VALUES (?, ?, ?)";
            PreparedStatement starStatement = conn.prepareStatement(query);
            query = "INSERT INTO genres (name) VALUES (?)";
            PreparedStatement genreStatement = conn.prepareStatement(query);
            query = "INSERT INTO stars_in_movies (starId, movieId) VALUES (?, ?)";
            PreparedStatement starInMoviesStatement = conn.prepareStatement(query);
            query = "INSERT INTO genres_in_movies (genreId, movieId) VALUES (?, ?)";
            PreparedStatement genreInMoviesStatement = conn.prepareStatement(query);

            // ------------INSERTING STARS -------------------
            for (Map.Entry<String, Star> entry : stars.entrySet()){
                Star s = entry.getValue();
                starStatement.setString(1, s.getStarId());
                starStatement.setString(2, s.getName());
                starStatement.setInt(3, s.getBirthYear());
                starsAdded += starStatement.executeUpdate();
            }
            if (starsAdded > 0) {
                out.println("Successfully added " + starsAdded + " stars");
            }
            // ------------INSERTING MOVIES -------------------
            for (Map.Entry<String, Movie> entry : movies.entrySet()){

                Movie m = entry.getValue();
                movieStatement.setString(1, m.getMovieId());
                movieStatement.setString(2, m.getTitle());
                movieStatement.setInt(3, m.getYear());
                movieStatement.setString(4, m.getDirector());
                moviesAdded += movieStatement.executeUpdate();

                for (Star s : m.getStars()){
                    starInMoviesStatement.setString(1, s.getStarId());
                    starInMoviesStatement.setString(2, m.getMovieId());
                    rowsAffected += starInMoviesStatement.executeUpdate();
                }
                for (Genre g : m.getGenres()){
                    genreInMoviesStatement.setInt(1, g.getGenreId());
                    genreInMoviesStatement.setString(2, m.getMovieId());
                    genresAdded += genreInMoviesStatement.executeUpdate();
                }

            }
            if (moviesAdded > 0) {
                out.println("Successfully added " + moviesAdded + " movies");
            }
            if (rowsAffected > 0) {
                out.println("Successfully added " + genresAdded + " genres");
            }
            if (rowsAffected > 0) {
                out.println("Successfully added " + rowsAffected + " stars in movies");
                rowsAffected = 0;
            }
            writeToReport(numMovieErrors + " Inconsistent Movies");
            writeToReport(numStarErrors + " Missing Stars");
        } catch (Exception e) {
            out.println("exception: " + e);
            out.print("SAXParser SQL Error");

        }
    }

    public static void main(String[] args) {
        MovieSAXParser spe = new MovieSAXParser();
        spe.runExample();
    }

}
