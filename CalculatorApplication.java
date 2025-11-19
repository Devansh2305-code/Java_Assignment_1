import java.util.Scanner;
class Calculator {
    public double add(double a, double b, int num3) {
        return a + b;
    }
    public int add(int a, int b, int c) {
        return a + b + c;
    }
    public int subtract(int a, int b) {
        return a - b;
    }
    public double multiply(double a, double b) {
        return a * b;
    }
    public int divide(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("Division by zero is not allowed.");
        }
        return a / b;
    }
}
class UserInterface {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Calculator calculator = new Calculator();
        while (true) {
            System.out.println("Calculator Menu:");
            System.out.println("1. Addition");
            System.out.println("2. Subtraction");
            System.out.println("3. Multiplication");
            System.out.println("4. Division");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> performAddition(scanner, calculator);
                case 2 -> performSubtraction(scanner, calculator);
                case 3 -> performMultiplication(scanner, calculator);
                case 4 -> performDivision(scanner, calculator);
                case 5 -> {
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    public static void performAddition(Scanner scanner, Calculator calculator) {
        System.out.print("Enter the first number: ");
        double num1 = scanner.nextDouble();
        System.out.print("Enter the second number: ");
        double num2 = scanner.nextDouble();
        double result = calculator.add(num1, num2, 0);
        System.out.println("Addition Result: " + result);
        System.out.print("Do you want to add three integers? (yes/no): ");
        String response = scanner.next();
        if (response.equalsIgnoreCase("yes")) {
            System.out.print("Enter the third integer: ");
            int num3 = scanner.nextInt();
            int additionResult = calculator.add((int) num1, (int) num2, num3);
            System.out.println("Addition Result: " + additionResult);
        }
    }
    public static void performSubtraction(Scanner scanner, Calculator calculator) {
        System.out.print("Enter the first integer: ");
        int num1 = scanner.nextInt();
        System.out.print("Enter the second integer: ");
        int num2 = scanner.nextInt();
        int result = calculator.subtract(num1, num2);
        System.out.println("Subtraction Result: " + result);
    }
    public static void performMultiplication(Scanner scanner, Calculator calculator) {
        System.out.print("Enter the first double: ");
        double num1 = scanner.nextDouble();
        System.out.print("Enter the second double: ");
        double num2 = scanner.nextDouble();
        double result = calculator.multiply(num1, num2);
        System.out.println("Multiplication Result: " + result);
    }
    public static void performDivision(Scanner scanner, Calculator calculator) {
        System.out.print("Enter the dividend: ");
        int dividend = scanner.nextInt();
        System.out.print("Enter the divisor: ");
        int divisor = scanner.nextInt();
        try {
            int result = calculator.divide(dividend, divisor);
            System.out.println("Division Result: " + result);
        } catch (ArithmeticException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
public class CalculatorApplication {
    public static void main(String[] args) {
        UserInterface.main(args);
    }
}