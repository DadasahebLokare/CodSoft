package com.app.Task3;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class StudentManagementSystem {

    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/codsoft", "root", "root");

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("Options:");
                System.out.println("1. add a new dtudent");
                System.out.println("2. remove student");
                System.out.println("3. searching for a student,");
                System.out.println("4. display all students");
                System.out.println("5. exiting");

                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        System.out.print("Enter student name: ");
                        String name = scanner.next();
                        System.out.print("Enter student roll number: ");
                        int rollNumber = scanner.nextInt();
                        System.out.print("Enter student grade: ");
                        String grade = scanner.next();
                        addStudent(connection, name, rollNumber, grade);
                        System.out.println("Student added successfully!");
                        break;

                    case 2:
                        System.out.print("Enter roll number of the student to remove: ");
                        int removeRollNumber = scanner.nextInt();
                        removeStudent(connection, removeRollNumber);
                        break;

                    case 3:
                        System.out.print("Enter roll number of the student to search: ");
                        int searchRollNumber = scanner.nextInt();
                        Student foundStudent = searchStudent(connection, searchRollNumber);
                        if (foundStudent != null) {
                            System.out.println("Student found - Name: " + foundStudent.getName() + ", Grade: " + foundStudent.getGrade());
                        } else {
                            System.out.println("Student not found.");
                        }
                        break;

                    case 4:
                        displayAllStudents(connection);
                        break;

                    case 5:
                        connection.close();
                        System.out.println("Exiting program.");
                        System.exit(0);

                    default:
                        System.out.println("Invalid choice. Please enter a valid option.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addStudent(Connection connection, String name, int rollNumber, String grade) throws SQLException {
        String insertQuery = "INSERT INTO students (name, roll_number, grade) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, rollNumber);
            preparedStatement.setString(3, grade);
            preparedStatement.executeUpdate();
        }
    }

    public static void removeStudent(Connection connection, int rollNumber) throws SQLException {
        String deleteQuery = "DELETE FROM students WHERE roll_number = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setInt(1, rollNumber);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Student with roll number " + rollNumber + " removed.");
            } else {
                System.out.println("Student not found.");
            }
        }
    }

    public static Student searchStudent(Connection connection, int rollNumber) throws SQLException {
        String selectQuery = "SELECT * FROM students WHERE roll_number = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setInt(1, rollNumber);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String grade = resultSet.getString("grade");
                    return new Student(name, rollNumber, grade);
                }
            }
        }
        return null;
    }

    public static void displayAllStudents(Connection connection) throws SQLException {
        String selectQuery = "SELECT * FROM students";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int rollNumber = resultSet.getInt("roll_number");
                String name = resultSet.getString("name");
                String grade = resultSet.getString("grade");
                System.out.println("Roll Number: " + rollNumber + ", Name: " + name + ", Grade: " + grade);
            }
        }
    }
}

class Student {
    private String name;
    private int rollNumber;
    private String grade;

    public Student(String name, int rollNumber, String grade) {
        this.name = name;
        this.rollNumber = rollNumber;
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public int getRollNumber() {
        return rollNumber;
    }

    public String getGrade() {
        return grade;
    }
}
