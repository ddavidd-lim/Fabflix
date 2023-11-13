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
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


@WebServlet(
        name = "Servlets.DashboardServlet",
        urlPatterns = "/_dashboard/api/dashboard"
)
public class DashboardServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public DashboardServlet() {
    }

    public void init(ServletConfig config) {
        try {
            this.dataSource = (DataSource)(new InitialContext()).lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("getting metadata");
        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        try {
            Connection conn = dataSource.getConnection();
            DatabaseMetaData metadata = conn.getMetaData();

            JsonArray results = new JsonArray();

            ResultSet rs = metadata.getTables(null, "moviedb", null, new String[]{"TABLE"});
            while (rs.next())
            {
                JsonObject info = new JsonObject();
                String table_name = rs.getString("TABLE_NAME");
                System.out.println(table_name);
                info.addProperty("table_name", table_name);
                JsonArray content = new JsonArray();

                ResultSet rs_columns = metadata.getColumns(null, null, table_name, null);
                while (rs_columns.next())
                {
                    JsonObject content_info = new JsonObject();
                    String column_name = rs_columns.getString("COLUMN_NAME");
                    String column_type = rs_columns.getString("TYPE_NAME");
                    System.out.println("Column name: " + column_name);
                    System.out.println("Column type: " + column_type);

                    content_info.addProperty("name", column_name);
                    content_info.addProperty("type", column_type);

                    content.add(content_info);
                }
                info.add("content", content);
                results.add(info);
            }

//            JsonObject jsonObject = new JsonObject();
//            jsonObject.add("tables", results);
            System.out.println(results.toString());
            out.write(results.toString());

            rs.close();
            conn.close();
            System.out.println("done getting metadata");

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
