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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


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


            String query = "SELECT * FROM creditcards as c WHERE " +
                    "c.id=?, c.firstName=?, c.lastName=?, c.expiration=?;";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            statement.setString(1, cardnumber);
            statement.setString(2, firstname);
            statement.setString(3, lastname);
            statement.setString(4, expdate);

            // Perform the query
            ResultSet rs = statement.executeQuery(query);
            String isValid = "false";
            // Check if query returned anything
            if (rs.next()) {
                isValid = "true";
            }
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
}
