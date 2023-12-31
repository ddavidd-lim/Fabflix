package Servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


@WebServlet(
        name = "Servlets.MovieServlet",
        urlPatterns = {"/api/movie"}
)
public class MovieServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public MovieServlet() {
    }

    public void init(ServletConfig config) {
        try {
            this.dataSource = (DataSource)(new InitialContext()).lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException var3) {
            var3.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Declare our statement
            Statement statement = conn.createStatement();
/*
            String query = "SELECT * from movies as m, stars_in_movies as sim, stars as s where s.id=sim.starId and" +
                    "sim.movieId = m.id and m.id=?"; */
            String query = "SELECT m.id as movieId, r.rating as rating, m.title as title, m.year as year, m.director as director, " +
                                "GROUP_CONCAT(DISTINCT g.name ORDER BY g.name ASC SEPARATOR ', ') as genres, " +
                        "GROUP_CONCAT(DISTINCT CONCAT(s.id, ':', s.name) ORDER BY s.name ASC SEPARATOR ', ') as top3Stars " +
                        "FROM movies as m " +
                        "JOIN stars_in_movies as sim ON m.id = sim.movieId " +
                        "JOIN stars as s ON sim.starId = s.id " +
                        "JOIN genres_in_movies as gim ON m.id = gim.movieId " +
                        "JOIN genres as g ON g.id = gim.genreId " +
                        "JOIN ratings as r ON m.id = r.movieId " +
                        "WHERE r.rating IS NOT NULL " +
                        "GROUP BY m.id, r.rating " +
                        "ORDER BY r.rating DESC LIMIT 20;";


            // Perform the query
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
            conn.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }
}
