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
import java.util.Enumeration;
import java.util.Objects;

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

//            Enumeration<String> params = request.getParameterNames();
//            while(params.hasMoreElements()){
//                String paramName = params.nextElement();
//                System.out.println("Parameter Name - "+paramName+", Value - "+request.getParameter(paramName));
//            }

            String title = request.getParameter("movietitle");
            System.out.println("Title: " + title);
            String year = request.getParameter("movieyear");
            System.out.println("Year:" + year);
            String director = request.getParameter("director");
            System.out.println("Director:" + director);
            String star = request.getParameter("moviestar");
            System.out.println("Star:" + star);
            String genre = request.getParameter("genre");
            System.out.println("Genre: " + genre);
            String type = request.getParameter("type");
            System.out.println("Type: " + type);
            String sort = request.getParameter("sort");
            System.out.println("Sorting: " + sort);
            String limit = request.getParameter("limit");
            System.out.println("Limit: " + limit);
            String page = request.getParameter("page");
            System.out.println("Page Number: " + page);
            String whereQuery = "";
            String havingQuery = "";
            String orderQuery = "title ASC, rating ASC"; // default
            String limitQuery = "";
            String pageQuery = "";

            if (genre != null && !Objects.equals(genre, "null")) {
                havingQuery += String.format("genres LIKE '%%%s%%' ", genre);
            }
            else {
                havingQuery += "genres LIKE '%%' ";
            }
            if (title != null && !Objects.equals(title, "null")) {
                if (!title.isEmpty()) {
                    if (Objects.equals(type, "browse")) {
                        if (title.contains("*")) {
                            whereQuery += "and title regexp '^[^a-zA-Z0-9]'";
                        } else {
                            whereQuery += String.format(" and LOWER(title) like LOWER('%s%%') ", title);
                        }
                    } else {
                        whereQuery += String.format(" and LOWER(title) like LOWER('%%%s%%') ", title);
                    }
                }
            }
            if (year != null && !Objects.equals(year, "null")) {
                if (!year.isEmpty()) {
                    whereQuery += String.format(" and year = %s ", year);
                }
            }
            if (director != null && !Objects.equals(director, "null"))
            {
                if (!director.isEmpty()) {
                    whereQuery = String.format(" and LOWER(director) like LOWER('%%%s%%') ", director);
                }
            }
            if(star != null && !Objects.equals(star, "null"))
            {
                if (!star.isEmpty()) {
                    havingQuery += String.format(" and LOWER(top3Stars) like LOWER('%%%s%%') ", star);
                }
            }
            if (sort != null && !Objects.equals(sort, "null"))
            {
                if (sort.contains("AscTitleDecRating"))
                {
                    orderQuery = "title ASC, rating DESC";
                }
                if (sort.contains("DecTitleAscRating"))
                {
                    orderQuery = "title DESC, rating ASC";
                }
                if (sort.contains("DecTitleDecRating"))
                {
                    orderQuery = "title DESC, rating DESC";
                }
                if (sort.contains("AscRatingAscTitle"))
                {
                    orderQuery = "rating ASC, title ASC";
                }
                if (sort.contains("AscRatingDecTitle"))
                {
                    orderQuery = "rating ASC, title DESC";
                }
                if (sort.contains("DecRatingAscTitle"))
                {
                    orderQuery = "rating DESC, title ASC";
                }
                if (sort.contains("DecRatingDecTitle"))
                {
                    orderQuery = "rating DESC, title DESC";
                }
            }

            if (limit != null && !Objects.equals(limit, "null"))
            {
                limitQuery = limit;
            }
            else {
                limit = "25";
                limitQuery = "25";
            }
            if (page != null && !Objects.equals(page, "null"))
            {
                var offset = Integer.parseInt(page) - 1; // page will start at 1
                var limit_num = Integer.parseInt(limit);
                offset = offset * limit_num;
                pageQuery = Integer.toString(offset);
            } else if (Objects.equals(page, "1")) {
                pageQuery = "0";
            }
            else {
                pageQuery = "0";
            }


            System.out.println("Search: WHERE - " + whereQuery + " HAVING - " + havingQuery);

            String query = "SELECT m.id as movieId, r.rating as rating, m.title as title, m.year as year, m.director as director, " +
                    "GROUP_CONCAT(DISTINCT g.name ORDER BY g.name ASC SEPARATOR ', ') as genres, " +
                    "GROUP_CONCAT(DISTINCT CONCAT(s.id, ':', s.name) ORDER BY s.name ASC SEPARATOR ', ') as top3Stars " +
                    "FROM movies as m " +
                    "JOIN stars_in_movies as sim ON m.id = sim.movieId " +
                    "JOIN stars as s ON sim.starId = s.id " +
                    "JOIN genres_in_movies as gim ON m.id = gim.movieId " +
                    "JOIN genres as g ON g.id = gim.genreId " +
                    "JOIN ratings as r ON m.id = r.movieId " +
                    "WHERE r.rating IS NOT NULL " + whereQuery +
                    "GROUP BY m.id, r.rating HAVING " + havingQuery +
                    "ORDER BY " + orderQuery + " LIMIT " + limitQuery + " OFFSET " + pageQuery + ";";

            // (select s.id, count(*) as count from stars_in_movies as sim, stars as s where sim.starId = s.id group by starId) as movie_count
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
            dbCon.close();

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
            System.out.println("Failed To Search");

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        finally {
            out.close();
            System.out.println("Search Closing");
        }
    }

}
