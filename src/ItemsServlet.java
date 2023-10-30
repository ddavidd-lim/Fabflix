import com.google.gson.JsonArray;
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
import java.util.ArrayList;
import java.util.Date;

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "ItemsServlet", urlPatterns = "/api/cart")
public class ItemsServlet extends HttpServlet {
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
    /**
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json"); // Response mime type

        HttpSession session = request.getSession(); // gets session

        JsonObject responseJsonObject = new JsonObject();       // creates new json object to add id/accesstime

        ArrayList<JsonObject> previousItems = (ArrayList<JsonObject>) session.getAttribute("previousItems");    // array of items
        if (previousItems == null) {
            previousItems = new ArrayList<>();    // inits array ifndef
        }
        // Log to localhost log
        request.getServletContext().log("getting " + previousItems.size() + " items");
        JsonArray previousItemsJsonArray = new JsonArray();
        previousItems.forEach(previousItemsJsonArray::add); // converting string array to json array
        responseJsonObject.add("previousItems", previousItemsJsonArray);    // add array as element of json obj

        // write all the data into the jsonObject
        response.getWriter().write(responseJsonObject.toString());
    }

    /**
     * handles POST requests to add and show the item list information
     */
    // ADDS to the previous items list
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json"); // Response mime type
        String id = request.getParameter("movie_id");
        System.out.println(id);
        HttpSession session = request.getSession();

        PrintWriter out = response.getWriter();
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            String query = "SELECT m.title as title FROM movies as m WHERE m.id=?";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, id);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            // Iterate through each row of rs
            String movieTitle = "";
            JsonObject jsonObject = new JsonObject();
            if (rs.next()) {    // if movie exists: it probably does btw
                movieTitle = rs.getString("title");

                // Create a JsonObject based on the data we retrieve from rs
                jsonObject.addProperty("movie_title", movieTitle);
                jsonObject.addProperty("quantity", "1");
                jsonObject.addProperty("price", "10");

                System.out.println("Found one object");
            }
            // get the previous items in a ArrayList
            System.out.println("get previous items in arraylist");
            ArrayList<JsonObject> previousItems = (ArrayList<JsonObject>) session.getAttribute("previousItems");
            if (previousItems == null) {
                previousItems = new ArrayList<>();    // inits array ifndef
                previousItems.add(jsonObject);
                session.setAttribute("previousItems", previousItems);   // sets session attr to new array
                System.out.println("Added new array");
            } else {
                // prevent corrupted states through sharing under multi-threads
                // will only be executed by one thread at a time
                synchronized (previousItems) {
                    for (int i = 0; i < previousItems.size(); i++) {
                        JsonObject item = previousItems.get(i).getAsJsonObject();
                        if (movieTitle.equals(item.get("movie_title").getAsString())) {
                            // Update the quantity
                            int newQuantity = item.get("quantity").getAsInt() + 1;
                            item.addProperty("quantity", newQuantity);
                            break; // Stop searching once the item is found and updated
                        }
                        else{
                            previousItems.add(jsonObject);
                        }
                    }

                    System.out.println("Successfully added movie");
                }
            }
            rs.close();
            statement.close();
            // ------------------------------------

            JsonObject responseJsonObject = new JsonObject();
            JsonArray previousItemsJsonArray = new JsonArray();
            previousItems.forEach(previousItemsJsonArray::add); // convert string array in session to json array
            responseJsonObject.add("previousItems", previousItemsJsonArray);    // adds json array as element of response


            response.getWriter().write(responseJsonObject.toString());
            System.out.println("Wrote to response");
            // Set response status to 200 (OK)
            System.out.println("Set response 200");
            response.setStatus(200);
        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }
    }
}
