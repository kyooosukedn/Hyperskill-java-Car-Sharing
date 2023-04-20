package carsharing;

import java.sql.*;
public class CompanyDAO {
    public static Connection connection;
    public CompanyDAO(String url) throws SQLException {
        this.connection = DriverManager.getConnection(url);

    }

    public void initialize() {
        try {
            connection.setAutoCommit(true);
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE COMPANY(ID INT PRIMARY KEY AUTO_INCREMENT, NAME UNIQUE VARCHAR(255) NOT NULL)");

        } catch (SQLException e) {
            System.out.println("Error creating database: " + e.getMessage());
        }
    }
    public static void insertCompany(String companyName) {
        String sql = "INSERT INTO COMPANY(name) VALUES(?)";
        try (PreparedStatement st = connection.prepareStatement(sql)) {

            st.setString(1, companyName);

            System.out.println("The company was created!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getCompanyList() {
        String sql = "SELECT * FROM COMPANY";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();

            // Extract data from result set
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                return id + ". " + name;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "The company list is empty!";
    }

}
