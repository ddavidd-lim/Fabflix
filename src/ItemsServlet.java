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
            e.printStackTrace();
        }
    }
    /**
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
        String action = request.getParameter("action"); // Add = 2, Decrement = 1, Delete = 0
        System.out.println(id);
        System.out.println(action);
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

                if (action.equals("add")){
                    // Create a JsonObject based on the data we retrieve from rs
                    jsonObject.addProperty("movie_id", id);
                    jsonObject.addProperty("movie_title", movieTitle);
                    jsonObject.addProperty("quantity", "1");
                    jsonObject.addProperty("price", "10");
                }
                System.out.println("FOUND: Movie: " + movieTitle);
            }
            // get the previous items in a ArrayList
            ArrayList<JsonObject> previousItems = (ArrayList<JsonObject>) session.getAttribute("previousItems");
            switch (action) {
                case "add":
                    if (previousItems == null) {
                        previousItems = new ArrayList<>();    // inits array ifndef
                        previousItems.add(jsonObject);
                        session.setAttribute("previousItems", previousItems);   // sets session attr to new array
                        System.out.println("Added Movie: " + movieTitle);
                    } else {
                        // prevent corrupted states through sharing under multi-threads
                        // will only be executed by one thread at a time
                        int found = 0;
                        synchronized (previousItems) {
                            for (int i = 0; i < previousItems.size(); i++) {
                                JsonObject item = previousItems.get(i).getAsJsonObject();
                                if (movieTitle.equals(item.get("movie_title").getAsString())) {
                                    // Update the quantity
                                    int newQuantity = item.get("quantity").getAsInt() + 1;
                                    item.addProperty("quantity", newQuantity);
                                    found = 1;
                                    break; // Stop searching once the item is found and updated
                                }
                            }
                            if (found == 0) {
                                previousItems.add(jsonObject);
                            }

                            System.out.println("Successfully added movie");
                        }
                    }
                    break;
                case "remove":
                    synchronized (previousItems) {
                        for (int i = 0; i < previousItems.size(); i++) {
                            JsonObject item = previousItems.get(i).getAsJsonObject();
                            if (movieTitle.equals(item.get("movie_title").getAsString())) {
                                // Update the quantity
                                int newQuantity = item.get("quantity").getAsInt() - 1;
                                if (newQuantity == 0) {
                                    previousItems.remove(i);
                                } else {
                                    item.addProperty("quantity", newQuantity);
                                }
                                break;
                            }
                        }

                        System.out.println("Successfully decremented movie");
                    }
                    break;
                case "delete":
                    synchronized (previousItems) {
                        for (int i = 0; i < previousItems.size(); i++) {
                            JsonObject item = previousItems.get(i).getAsJsonObject();
                            if (movieTitle.equals(item.get("movie_title").getAsString())) {
                                // Update the quantity
                                item.addProperty("quantity", 0);
                                previousItems.remove(i);
                                System.out.println("Successfully deleted movie");
                                break;
                            }
                        }
                    }
                    break;
            }


            rs.close();
            statement.close();
            // ------------------------------------

            JsonObject responseJsonObject = new JsonObject();
            JsonArray previousItemsJsonArray = new JsonArray();
            previousItems.forEach(previousItemsJsonArray::add); // convert string array in session to json array
            responseJsonObject.add("previousItems", previousItemsJsonArray);    // adds json array as element of response


            response.getWriter().write(responseJsonObject.toString());
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