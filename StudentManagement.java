import java.util.*;

class InvalidMarksException extends Exception {
    public InvalidMarksException(String msg) { super(msg); }
}

class Student {
    private int roll;
    private String name;
    private int[] marks;

    public Student(int roll, String name, int[] marks) throws InvalidMarksException {
        this.roll = roll;
        this.name = name;
        this.marks = marks;
        validate();
    }

    private void validate() throws InvalidMarksException {
        for (int i = 0; i < 3; i++) {
            if (marks[i] < 0 || marks[i] > 100)
                throw new InvalidMarksException("Invalid marks for subject " + (i + 1) + ": " + marks[i]);
        }
    }

    public int getRoll() { return roll; }

    public void display() {
        System.out.println("Roll Number: " + roll);
        System.out.println("Student Name: " + name);
        System.out.println("Marks: " + marks[0] + " " + marks[1] + " " + marks[2]);
        double avg = (marks[0] + marks[1] + marks[2]) / 3.0;
        System.out.println("Average: " + avg);
        System.out.println("Result: " + (avg >= 40 ? "Pass" : "Fail"));
    }
}

class ResultManager {
    private Student[] list = new Student[50];
    private int count = 0;
    Scanner sc = new Scanner(System.in);

    public void addStudent() {
        try {
            System.out.print("Enter Roll Number: ");
            int r = sc.nextInt(); sc.nextLine();

            System.out.print("Enter Student Name: ");
            String n = sc.nextLine();

            int[] m = new int[3];
            for (int i = 0; i < 3; i++) {
                System.out.print("Enter marks for subject " + (i + 1) + ": ");
                m[i] = sc.nextInt();
            }

            list[count++] = new Student(r, n, m);
            System.out.println("Student added successfully. Returning to main menu...");

        } catch (InvalidMarksException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("Error: Invalid input.");
            sc.nextLine();
        }
    }

    public void showStudent() {
        try {
            System.out.print("Enter Roll Number to search: ");
            int r = sc.nextInt();

            for (int i = 0; i < count; i++) {
                if (list[i].getRoll() == r) {
                    list[i].display();
                    System.out.println("Search completed.");
                    return;
                }
            }
            System.out.println("Student not found.");

        } catch (InputMismatchException e) {
            System.out.println("Error: Invalid roll number.");
            sc.nextLine();
        }
    }

    public void menu() {
        int choice = 0;
        try {
            while (choice != 3) {
                System.out.println("\n===== Student Result Management System =====");
                System.out.println("1. Add Student");
                System.out.println("2. Show Student Details");
                System.out.println("3. Exit");
                System.out.print("Enter your choice: ");
                choice = sc.nextInt();

                switch (choice) {
                    case 1: addStudent(); break;
                    case 2: showStudent(); break;
                    case 3: System.out.println("Exiting program. Thank you!"); break;
                    default: System.out.println("Invalid choice.");
                }
            }
        } finally {
            sc.close();
            System.out.println("Scanner closed. Program terminated.");
        }
    }
}

public class StudentManagement {
    public static void main(String[] args) {
        new ResultManager().menu();
    }
}