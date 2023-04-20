package carsharing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;

public class Main {
    public static void main(String[] args) throws SQLException {
        String databaseFileName = "carsharing.mv.db";

        if (args.length > 1 && args[0].equals("-databaseFileName")) {
            databaseFileName = args[1];
        }

        String url = "jdbc:h2:./src/carsharing/db/" + databaseFileName;

        createTableIfNotExists(url);

        try (Connection connection = DriverManager.getConnection(url)) {
            connection.setAutoCommit(true);

            int choice;
            do {
                System.out.println("1. Log in as a manager");
                System.out.println("0. Exit");

                choice = readInt();
                switch (choice) {
                    case 1:
                        managerMenu(connection);
                        break;
                    case 0:
                        break;
                    default:
                        System.out.println("Invalid choice, please try again.");
                        break;
                }
            } while (choice != 0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTableIfNotExists(String url) {
        try (Connection connection = DriverManager.getConnection(url)) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS COMPANY (ID INT AUTO_INCREMENT PRIMARY KEY, NAME VARCHAR(255) UNIQUE NOT NULL)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void managerMenu(Connection connection) throws SQLException {
        int choice;
        do {
            System.out.println("1. Company list");
            System.out.println("2. Create a company");
            System.out.println("0. Back");

            choice = readInt();
            switch (choice) {
                case 1:
                    printCompanyList(connection);
                    break;
                case 2:
                    createCompany(connection);
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
                    break;
            }
        } while (choice != 0);
    }

    private static void printCompanyList(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT ID, NAME FROM COMPANY ORDER BY ID")) {
            if (!resultSet.next()) {
                System.out.println("The company list is empty!");
            } else {
                System.out.println("Company list:");
                int index = 1;
                do {
                    System.out.printf("%d. %s\n", index++, resultSet.getString("NAME"));
                } while (resultSet.next());
            }
        }
    }

    private static void createCompany(Connection connection) throws SQLException {
        System.out.println("Enter the company name:");
        String name = readString();

        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO COMPANY (NAME) VALUES (?)")) {
            statement.setString(1, name);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("The company was created!");
            } else {
                System.out.println("Failed to create the company, please try again.");
            }
        }
    }

    private static String readString() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            return reader.readLine().trim();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static int readInt() {
        String input = readString();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}





