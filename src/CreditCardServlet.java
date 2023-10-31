import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mysql.cj.x.protobuf.MysqlxPrepare;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Date;


@WebServlet(
        name = "CreditCardServlet",
        urlPatterns = {"/api/creditcard"}
)

public class CreditCardServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public CreditCardServlet() {
    }

    public void init(ServletConfig config) {
        try {
            this.dataSource = (DataSource)(new InitialContext()).lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException var3) {
            var3.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type
        String cardnumber = request.getParameter("cardnumber");
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String expdate = request.getParameter("expdate");


        System.out.println(cardnumber);
        System.out.println(firstname);
        System.out.println(lastname);
        System.out.println(expdate);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {


            String query = "SELECT * FROM creditcards WHERE id = ? AND firstName = ? AND lastName = ? AND expiration = ?";
            System.out.println("So far: 1");
            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);
            System.out.println("So far: 2");
            statement.setString(1, cardnumber);
            statement.setString(2, firstname);
            statement.setString(3, lastname);
            statement.setString(4, expdate);
            System.out.println("So far before rs");

            // Perform the query
            ResultSet rs = statement.executeQuery();
            String isValid = "false";
            System.out.println("So far: " + isValid);
            // Check if query returned anything
            if (rs.next()) {
                isValid = "true";
            }
            System.out.println("Now: " + isValid);
            rs.close();
            statement.close();


            // Write JSON string to output
            out.write(isValid);
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
    }
    /**
     * handles POST requests to add and show the item list information
     */
    // ADDS to the previous items list
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json"); // Response mime type
        String cardnumber = request.getParameter("cardnumber");
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
//        String expdate = request.getParameter("expdate");


        HttpSession session = request.getSession();
        ArrayList<JsonObject> previousItems = (ArrayList<JsonObject>) session.getAttribute("previousItems");

        PrintWriter out = response.getWriter();
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // for item in previous items, get movieid and insert into query
            PreparedStatement statement = null;
            ResultSet rs = null;
            for (JsonObject obj : previousItems){
                JsonObject item = obj.getAsJsonObject();
                String movie_id = String.valueOf(item.get("movie_id"));
                // Construct a query with parameter represented by "?"
                String query = "SELECT c.cid as customer_id FROM movies as m, customers as c " +
                        "WHERE c.ccId = ? AND m.id = ? AND c.firstName = ? AND c.lastName = ?";

                // Declare our statement
                statement = conn.prepareStatement(query); // want customer_id out of query

                // Set the parameter represented by "?" in the query to the id we get from url,
                statement.setString(1, cardnumber);
                statement.setString(2, movie_id);
                statement.setString(3, firstname);
                statement.setString(4, lastname);

                // Perform the query
                rs = statement.executeQuery();

                // Iterate through each row of rs
                if (rs.next()) {    // if movie exists: it probably does btw
                    int customer_id = rs.getInt("customer_id");
                    // movie_id acquired
                    // Sale date
                    long millis = System.currentTimeMillis();
                    java.sql.Date date = new java.sql.Date(millis);

                    // insert sale into table
                    query = "INSERT INTO sales (customerId, movie_id, sale_date) VALUES (?, ?, ?)";
                    statement = conn.prepareStatement(query); // want customer_id out of query
                    statement.setInt(1, customer_id);
                    statement.setString(2, movie_id);
                    statement.setDate(3, date);
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
