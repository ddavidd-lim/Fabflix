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

@WebServlet(name = "Servlets.LoginServlet", urlPatterns = "/api/addStar")
public class AddStarServlet extends HttpServlet {
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
        String starName = request.getParameter("starName");
        String starYear = request.getParameter("starYear");

        System.out.println("Star Name: " + starName + "\nStar Year: " + starYear);

        PrintWriter out = response.getWriter();
        JsonObject responseJsonObject = new JsonObject();
        try {
            Connection conn = dataSource.getConnection();

            // INSERT INTO stars(id, name, birthYear) VALUES (@starId, movieStar, NULL);
            String query = "CALL add_star(?, ?)";
            Integer starYearInt;
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, starName);
            if (!Objects.equals(starYear, "") && !Objects.equals(starYear, "null"))
            {
                starYearInt = Integer.parseInt(starYear);
                System.out.println("Star year set to " + starYearInt);
                statement.setInt(2, starYearInt);
            }
            else {;
                System.out.println("Star year set to null");
                statement.setNull(2, Types.NULL);
            }

            ResultSet rs = statement.executeQuery();

            String starId = "";
            while (rs.next())
            {
                starId = rs.getString("@starId");
                System.out.println("star id found: " + starId);
            }
            rs.close();
            statement.close();
            conn.close();

            out.write(starId);

            System.out.println("star added");

            // Set response status to 200 (OK)
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
        }

    }
}
