package Servlets;

import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Objects;

@WebServlet(name = "Servlets.AddMovieServlet", urlPatterns = "/_dashboard/api/addMovie")
public class AddMovieServlet extends HttpServlet
{
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();;
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json"); // Response mime type
        String title = request.getParameter("movieTitle");
        String year = request.getParameter("movieYear");
        String director = request.getParameter("movieDirector");
        String actor = request.getParameter("movieStar");
        String genre = request.getParameter("movieGenre");

        System.out.println("title: " + title + "\n" +
                           "year: " + year + "\n" +
                           "director: " + director + "\n" +
                           "star: " + actor + "\n" +
                           "genre: " + genre + "\n");
        JsonObject responseJsonObject = new JsonObject();
        try {
            Connection conn = dataSource.getConnection();

            String query = "CALL add_movie(?, ? , ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(query);

            statement.setString(1, title);
            statement.setString(3, director);
            statement.setString(4, actor);
            statement.setString(5, genre);

            if (!Objects.equals(year, "") && !Objects.equals(year, "null"))
            {
                statement.setInt(2, Integer.parseInt(year));
            }
            else {
                statement.setNull(2, Types.NULL);
            }

            ResultSet rs = statement.executeQuery();
            String message = "";
            while (rs.next())
            {
                message = rs.getString("message");
                System.out.println("message returned: " + message);
            }
            rs.close();
            statement.close();
            conn.close();

            responseJsonObject.addProperty("message", message);
            response.getWriter().write(responseJsonObject.toString());

            System.out.println("movie added");

            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            response.getWriter().write(jsonObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            response.getWriter().close();
            System.out.println("Closing writer");
        }
    }

}
