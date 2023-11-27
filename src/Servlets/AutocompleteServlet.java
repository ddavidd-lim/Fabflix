package Servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
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

@WebServlet(name = "Servlets.AutocompleteServlet", urlPatterns = "/api/autocomplete")
public class AutocompleteServlet extends HttpServlet{
    private static final long serialVersionUID = 2L;
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();;
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        JsonArray jsonArray = new JsonArray();

        try {
            Connection dbCon = dataSource.getConnection();

            String title = request.getParameter("movietitle");
            System.out.println("Title parameter: " + title);
            if (title == null || title.trim().isEmpty()) {
                out.write(jsonArray.toString());
                dbCon.close();
                return;
            }

            String query = "SELECT id, title FROM movies WHERE MATCH(title) AGAINST " +
                    "(? IN BOOLEAN MODE) LIMIT 10;";

            PreparedStatement statement = dbCon.prepareStatement(query);

            // Need to make all words from title input into boolean full text search
            String title_keywords = "";
            String[] keywords = title.split(" ");
            for (String word : keywords)
            {
                title_keywords += "+" + word + "* ";
            }
            statement.setString(1, title_keywords);

            System.out.println("query: " + statement);

            ResultSet rs = statement.executeQuery();
            while (rs.next())
            {
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");

                System.out.println("Movie id: " + movie_id + "\tMovie title: " + movie_title);
                jsonArray.add(generateJsonObject(movie_id, movie_title));
            }

            rs.close();
            statement.close();
            dbCon.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
            System.out.println(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        finally {
            out.close();
        }
    }

    private static JsonObject generateJsonObject(String movieID, String movieName)
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", movieName);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("id", movieID);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }


}
