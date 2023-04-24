package carsharing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS CAR (ID INT AUTO_INCREMENT PRIMARY KEY, NAME VARCHAR(255) UNIQUE NOT NULL, COMPANY_ID INT NOT NULL, FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY(ID))");
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
                    System.out.println("Choose a company:");
                    printCompanyList(connection);
                    List<Company> companies = new ArrayList<>();
                    Scanner scanner = new Scanner(System.in);
                    int whichCompany = Integer.parseInt(scanner.nextLine());
                    Company selectedCompany = getSelectedCompany(connection, whichCompany);
                    companyMenu(connection, selectedCompany);
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

    private static Company getSelectedCompany(Connection connection, int whichCompany) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT ID, NAME FROM COMPANY ORDER BY ID")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.absolute(whichCompany);
            return new Company(resultSet.getInt("ID"), resultSet.getString("NAME"));
        }
    }

    private static void companyMenu(Connection connection, Company company) throws SQLException{
        int choice;
        do {
            System.out.printf("%s company:\n", company.getName());
            System.out.println("1. Car list");
            System.out.println("2. Create a car");
            System.out.println("0. Back");

            choice = readInt();
            switch (choice) {
                case 1:
                    printCarList(connection, company);
                    break;
                case 2:
                    createCar(connection);
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
                    break;
            }
        } while (choice != 0);
    }

    private static void printCompanyList(Connection connection) throws SQLException {
        List<Company> companies = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT ID, NAME FROM COMPANY ORDER BY ID")) {
            if (!resultSet.next()) {
                System.out.println("The company list is empty!");
            } else {
                System.out.println("Company list:");
                int index = 1;
                do {
                    Company company = new Company(index, resultSet.getString("NAME"));
                    companies.add(company);
                    System.out.printf("%d. %s\n", index++, resultSet.getString("NAME"));
                } while (resultSet.next());
            }
        }
    }

    private static void printCarList(Connection connection, Company company) throws SQLException {
        List<Car> cars = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT ID, NAME FROM CAR WHERE COMPANY_ID = ? ORDER BY ID")) {
            statement.setInt(1, company.getId());
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                System.out.printf("The %s car list is empty!\n", company.getName());
            } else {
                System.out.printf("%s cars:\n", company.getName());
            }
            do {
                int id = resultSet.getInt("ID");
                String name = resultSet.getString("NAME");
                cars.add(new Car(id, name));
            } while (resultSet.next());
            for (Car car : cars) {
                System.out.printf("- %s\n", car.getName());
            }
        }
    }


    private static void createCompany(Connection connection) throws SQLException {
        System.out.println("Enter the car name:");
        String name = readString();

        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO COMPANY (NAME) VALUES (?)")) {
            statement.setString(1, name);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("The car was created!");
            } else {
                System.out.println("Failed to create the car, please try again.");
            }
        }
    }

    private static void createCar(Connection connection) throws SQLException {
        System.out.println("Enter the company name:");
        String name = readString();

        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO CAR (NAME) VALUES (?)")) {
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





