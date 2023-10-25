package com.app.Task4;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Atm_machine {
    private static final String URL = "jdbc:mysql://localhost:3306/codsoft";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("Welcome to the ATM!");

            while (true) {
                System.out.print("Enter your account number (or 'q' to quit): ");
                String accountNumber = scanner.nextLine();

                if (accountNumber.equalsIgnoreCase("q")) {
                    break;
                }

                System.out.print("Enter your PIN: ");
                String pin = scanner.nextLine();

                if (isValidAccount(connection, accountNumber, pin)) {
                    System.out.println("Login successful.");
                    processTransactions(connection, accountNumber, scanner);
                } else {
                    System.out.println("Invalid account number or PIN. Please try again.");
                }
            }

            System.out.println("Goodbye!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean isValidAccount(Connection connection, String accountNumber, String pin) throws SQLException {
        String sql = "SELECT COUNT(*) FROM atm WHERE account_number = ? AND pin = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, accountNumber);
            preparedStatement.setString(2, pin);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            return count == 1;
        }
    }

    private static void processTransactions(Connection connection, String accountNumber, Scanner scanner) throws SQLException {
        while (true) {
        	System.out.println("");
            System.out.println("ATM Machine"+"\n");
            System.out.println("1. Check Balance");
            System.out.println("2. Withdraw");
            System.out.println("3. Deposit");
            System.out.println("4. Logout"+"\n");
            System.out.print("Choose the operation: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    checkBalance(connection, accountNumber);
                    break;
                case 2:
                    withdraw(connection, accountNumber, scanner);
                    break;
                case 3:
                    deposit(connection, accountNumber, scanner);
                    break;
                case 4:
                    System.out.println("Thank you for using the ATM!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void checkBalance(Connection connection, String accountNumber) throws SQLException {
        String sql = "SELECT balance FROM atm WHERE account_number = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, accountNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int balance = resultSet.getInt("balance");
            System.out.println("Your balance is $" + balance);
        }
    }

    private static void withdraw(Connection connection, String accountNumber, Scanner scanner) throws SQLException {
        System.out.print("Enter the amount to withdraw: $");
        int amount = scanner.nextInt();

        if (amount <= 0) {
            System.out.println("Invalid amount. Please try again.");
            return;
        }

        String checkBalanceSql = "SELECT balance FROM atm WHERE account_number = ?";
        String updateBalanceSql = "UPDATE atm SET balance = ? WHERE account_number = ?";

        try (PreparedStatement checkBalanceStatement = connection.prepareStatement(checkBalanceSql);
             PreparedStatement updateBalanceStatement = connection.prepareStatement(updateBalanceSql)) {

            connection.setAutoCommit(false);

            // Check account balance
            checkBalanceStatement.setString(1, accountNumber);
            ResultSet resultSet = checkBalanceStatement.executeQuery();
            resultSet.next();
            int balance = resultSet.getInt("balance");

            if (balance < amount) {
                System.out.println("Insufficient funds. Withdrawal canceled.");
                return;
            }

            // Update account balance
            updateBalanceStatement.setInt(1, balance - amount);
            updateBalanceStatement.setString(2, accountNumber);
            updateBalanceStatement.executeUpdate();

            connection.commit();

            System.out.println("Withdrawal successful. Your new balance is $" + (balance - amount));
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private static void deposit(Connection connection, String accountNumber, Scanner scanner) throws SQLException {
        System.out.print("Enter money to be deposited: ");
        int amount = scanner.nextInt();

        if (amount <= 0) {
            System.out.println("Invalid amount. Please try again.");
            return;
        }

        String updateBalanceSql = "UPDATE atm SET balance = balance + ? WHERE account_number = ?";

        try (PreparedStatement updateBalanceStatement = connection.prepareStatement(updateBalanceSql)) {
            updateBalanceStatement.setInt(1, amount);
            updateBalanceStatement.setString(2, accountNumber);
            int rowsAffected = updateBalanceStatement.executeUpdate();

            if (rowsAffected == 1) {
               System.out.println("Your money has been successfully depsited");
            } else {
                System.out.println("Deposit failed. Please try again.");
            }
        }
    }
}
