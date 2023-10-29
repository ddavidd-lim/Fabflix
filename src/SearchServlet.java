import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
@WebServlet(name = "SearchServlet", urlPatterns = "/api/results")

public class SearchServlet extends HttpServlet{

    private static final long serialVersionUID = 2L;
    private DataSource dataSource;

    public SearchServlet() {
    }
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");    // Response mime type
        System.out.println("Search Inputted");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();


        try {
            Connection dbCon = dataSource.getConnection();
            Statement statement = dbCon.createStatement();

            String title = request.getParameter("movietitle");
//            System.out.println("Title: " + title);
            String year = request.getParameter("movieyear");
//            System.out.println("Year:" + year);
            String director = request.getParameter("director");
//            System.out.println("Director:" + director);
            String star = request.getParameter("moviestar");
//            System.out.println("Star:" + star);
            String formQuery = "";

            if (!title.isEmpty()) {
                formQuery += String.format("title like '%%%s%%' ", title);
                if (!year.isEmpty() || !director.isEmpty() || !star.isEmpty())
                {
                    formQuery += "and ";
                }
            }
            if (!year.isEmpty()) {
                formQuery += String.format("year = %s ", year);
                if (!director.isEmpty() || !star.isEmpty())
                {
                    formQuery += "and ";
                }
            }
            if (!director.isEmpty())
            {
                formQuery = String.format("director like '%%%s%%' ", director);
                if (!star.isEmpty())
                {
                    formQuery += "and ";
                }
            }
            if(!star.isEmpty())
            {
                formQuery += String.format("s.name like '%%%s%%' ", star);
            }

            System.out.println("Search: " + formQuery);

            String query = "SELECT m.id as movieId, r.rating as rating, m.title as title, m.year as year, m.director as director, " +
                    "GROUP_CONCAT(DISTINCT g.name ORDER BY g.name ASC SEPARATOR ', ') as genres, " +
                    "GROUP_CONCAT(DISTINCT CONCAT(s.id, ':', s.name) ORDER BY s.name ASC SEPARATOR ', ') as top3Stars " +
                    "FROM movies as m " +
                    "JOIN stars_in_movies as sim ON m.id = sim.movieId " +
                    "JOIN stars as s ON sim.starId = s.id " +
                    "JOIN genres_in_movies as gim ON m.id = gim.movieId " +
                    "JOIN genres as g ON g.id = gim.genreId " +
                    "JOIN ratings as r ON m.id = r.movieId " +
                    "WHERE r.rating IS NOT NULL and " + formQuery +
                    "GROUP BY m.id, r.rating " +
                    "ORDER BY r.rating;";

            request.getServletContext().log("query：" + query);
            System.out.println("Full Query: " + query);

            ResultSet rs = statement.executeQuery(query);

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String movie_id = rs.getString("movieId");
                String movie_title = rs.getString("title");
                String movie_director = rs.getString("director");
                String movie_year = rs.getString("year");
                String movie_rating = rs.getString("rating");
                String top3Stars = rs.getString("top3Stars");
                String genres = rs.getString("genres");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_rating", movie_rating);
                jsonObject.addProperty("top3Stars", top3Stars);
                jsonObject.addProperty("genres", genres);

                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.toString());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        finally {
            out.close();
        }
    }

}
