package Servlets;

import javax.naming.Context;
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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@WebServlet(name = "Servlets.SearchServlet", urlPatterns = "/api/results")

public class SearchServlet extends HttpServlet{

    private static final long serialVersionUID = 2L;
    private DataSource dataSource;
    // search will call post and then intend from search to movie where it will call post
    public SearchServlet() {
    }
//    public void init(ServletConfig config) {
//        try {
//            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
//        } catch (NamingException e) {
//            e.printStackTrace();
//        }
//    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");    // Response mime type
        System.out.println("Search Inputted");

        long startTimeTS = System.nanoTime();
        long elapsedTimeTJ = -1;

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {

//            Statement statement = dbCon.createStatement();

//            Enumeration<String> params = request.getParameterNames();
//            while(params.hasMoreElements()){
//                String paramName = params.nextElement();
//                System.out.println("Parameter Name - "+paramName+", Value - "+request.getParameter(paramName));
//            }
            String query = "";
            PreparedStatement statement;
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

            query = "SELECT m.id as movieId, r.rating as rating, m.title as title, m.year as year, m.director as director, " +
                    "GROUP_CONCAT(DISTINCT g.name ORDER BY g.name ASC SEPARATOR ', ') as genres, " +
                    "GROUP_CONCAT(DISTINCT CONCAT(s.id, ':', s.name) ORDER BY s.name ASC SEPARATOR ', ') as top3Stars " +
                    "FROM movies as m " +
                    "JOIN stars_in_movies as sim ON m.id = sim.movieId " +
                    "JOIN stars as s ON sim.starId = s.id " +
                    "JOIN genres_in_movies as gim ON m.id = gim.movieId " +
                    "JOIN genres as g ON g.id = gim.genreId " +
                    "LEFT JOIN ratings as r ON m.id = r.movieId " +
                    "WHERE ";
//                "WHERE r.rating IS NOT NULL";

            String whereQuery = "";
            String genres_query = "";
            String stars_query = "";
            Integer year_query = 0;

            String orderQuery = "title ASC, rating ASC"; // default
            String limitQuery = "";
            String pageQuery = "";

            int i = 0;
            HashMap<Integer, String> statementNumbers = new HashMap<Integer, String>();
            System.out.println("hashmap created");
            // insert title into query
            if (title != null && !Objects.equals(title, "null")) {
                if (!title.isEmpty()) {
                    if (Objects.equals(type, "browse")) {
                        if (title.contains("*")) {
                            query += " title regexp '^[^a-zA-Z0-9]'";
                        } else {
                            query +=" LOWER(title) like LOWER(?) ";
                            i += 1;
                            statementNumbers.put(i, title + "%");
                        }
                    } else if (Objects.equals(type, "search")) {
                        query += " LOWER(title) like LOWER(?) ";
                        i += 1;
                        statementNumbers.put(i, "%" + title + "%");
                    } else {
                        query += " MATCH(title) AGAINST (? IN BOOLEAN MODE) ";
                        String title_keywords = "";
                        String[] keywords = title.split(" ");
                        for (String word : keywords)
                        {
                            title_keywords += "+" + word + "* ";
                        }
                        i += 1;
                        statementNumbers.put(i, title_keywords);
                    }
                }
                else {
                    query += " LOWER(title) like LOWER(?) ";
                    i += 1;
                    statementNumbers.put(i, "%");
                }
            }
            else {
                query += " LOWER(title) like LOWER(?) ";
                i += 1;
                statementNumbers.put(i, "%");
            }
            System.out.println("title added " + i);

            // insert year into query
            if (year != null && !Objects.equals(year, "null")) {
                if (!year.isEmpty()) {
                    query += " and year = ? ";
                    year_query = Integer.parseInt(year);
                    i += 1;
                    statementNumbers.put(i, year);
                }
            }
            System.out.println("year added" + i);

            // insert director into query
            if (director != null && !Objects.equals(director, "null"))
            {
                if (director.isEmpty()) {
                    director = "";
                }
            }
            else {
                director = "";
            }
            query += " and LOWER(director) like LOWER(?) ";
            i += 1;
            statementNumbers.put(i, "%" + director + "%");
            System.out.println("director added " + i);

            // Group by and now move to genres and stars
            query += "GROUP BY m.id, r.rating HAVING ";


            if (genre != null && !Objects.equals(genre, "null")) {
                if (genre.isEmpty())
                {
                    genres_query = "%";
                }
                else {
                    genres_query = genre;
                }

            }
            else {
                genres_query = "%";
            }
            query += " genres LIKE ? AND ";
            i += 1;
            statementNumbers.put(i, genres_query);
            System.out.println("genres added " + i);


            if(star != null && !Objects.equals(star, "null"))
            {
                if (!star.isEmpty()) {
                    stars_query = "%" + star + "%";
                }
                else
                {
                    stars_query = "%";
                }
            }
            else {
                stars_query = "%";
            }
            query += "LOWER(top3Stars) like LOWER(?) ";
            i += 1;
            statementNumbers.put(i, stars_query);
            System.out.println("stars added " + i);


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
                limit = "10";
                limitQuery = "10";
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

            query += "ORDER BY " + orderQuery + " LIMIT " + limitQuery + " OFFSET " + pageQuery + ";";
            System.out.println("query: " + query);

            // Create connection here
            long startTimeTJ = System.nanoTime();
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            dataSource = (DataSource) envContext.lookup("jdbc/moviedb");
            Connection dbCon = dataSource.getConnection();
            statement = dbCon.prepareStatement(query);
            statementNumbers.forEach((key, value) -> {
                System.out.println("Key=" + key + ", Value=" + value);
                try {
                    statement.setString(key, value);
                } catch (SQLException e) {
                    System.out.println(e);
                    throw new RuntimeException(e);
                }
            });
//      }



//            query = "SELECT m.id as movieId, r.rating as rating, m.title as title, m.year as year, m.director as director, " +
//                    "GROUP_CONCAT(DISTINCT g.name ORDER BY g.name ASC SEPARATOR ', ') as genres, " +
//                    "GROUP_CONCAT(DISTINCT CONCAT(s.id, ':', s.name) ORDER BY s.name ASC SEPARATOR ', ') as top3Stars " +
//                    "FROM movies as m " +
//                    "JOIN stars_in_movies as sim ON m.id = sim.movieId " +
//                    "JOIN stars as s ON sim.starId = s.id " +
//                    "JOIN genres_in_movies as gim ON m.id = gim.movieId " +
//                    "JOIN genres as g ON g.id = gim.genreId " +
//                    "JOIN ratings as r ON m.id = r.movieId " +
//                    "WHERE r.rating IS NOT NULL and " +
//                    "year = ? and " +
//                    "LOWER(director) like LOWER(?) + ? " +
//                    "GROUP BY m.id, r.rating " +
//                    "HAVING genres LIKE ? AND " +
//                    "LOWER(top3Stars) like LOWER(?) " +
//                    "ORDER BY " + orderQuery + " LIMIT " + limitQuery + " OFFSET " + pageQuery + ";";


            // (select s.id, count(*) as count from stars_in_movies as sim, stars as s where sim.starId = s.id group by starId) as movie_count
            request.getServletContext().log("query：" + query);
            System.out.println("Full Query: " + statement);

            ResultSet rs = statement.executeQuery();

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
            System.out.println("Wrote JSON to output: " + jsonArray.toString());
            out.write(jsonArray.toString());
            System.out.println("Set response to 200");
            // Set response status to 200 (OK)
            response.setStatus(200);

            long endTimeTJ = System.nanoTime();
            elapsedTimeTJ = endTimeTJ - startTimeTJ;

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.toString());
            out.write(jsonObject.toString());
            System.out.println("Failed To Search");
            System.out.println(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        finally {
            out.close();
            System.out.println("Search Closing");
        }

        long endTimeTS = System.nanoTime();

        long elaspedTimeTS = endTimeTS - startTimeTS;

        System.out.println("TS: " + elaspedTimeTS + ", TJ: " + elapsedTimeTJ);
        System.out.println(getServletContext().getRealPath("/") + "log.txt");

        File outfile = new File (getServletContext().getRealPath("/") + "log.txt");
        FileWriter writer;
        if (outfile.createNewFile())
        {
            writer = new FileWriter(getServletContext().getRealPath("/") + outfile.getName());
        }
        else
        {
            writer = new FileWriter(getServletContext().getRealPath("/") + outfile.getName(), true);
        }
        writer.write("TS: " + elaspedTimeTS + ", TJ: " + elapsedTimeTJ + "\n");
        writer.close();
    }

}
