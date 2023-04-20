package carsharing;

import java.sql.*;
import java.util.Scanner;

public class Main {
    static CompanyDAO companyDAO;
    public static void main(String[] args) throws SQLException {
        String databaseFileName = "carsharing.mv.db";

        if (args.length > 1 && args[0].equals("-databaseFileName")) {
            databaseFileName = args[1];
        }

        String url = "jdbc:h2:./src/carsharing/db/" + databaseFileName;

        companyDAO = new CompanyDAO(url);
        companyDAO.initialize();
        mainMenu();

    }



    private static void mainMenu() {
        System.out.println("1. Log in as a manager\n" +
                "0. Exit");
        Scanner scanner = new Scanner(System.in);
        int input = Integer.parseInt(scanner.nextLine());
        while (true) {
            if (input == 1) {
                loggedInAsManager();
            }
            if (input == 0) {
                break;
            }
        }

    }

    private static void loggedInAsManager() {
        System.out.println("1. Company list\n" +
                "2. Create a company\n" +
                "0. Back");
        Scanner scanner = new Scanner(System.in);
        int input = Integer.parseInt(scanner.nextLine());
        if (input == 1) {
            companyDAO.getCompanyList();
        }
        if (input == 2) {
            System.out.println("Enter the company name:");
            String companyName = scanner.nextLine();
            companyDAO.insertCompany(companyName);
        }
        if (input == 0) {
            mainMenu();
        }

    }

}
