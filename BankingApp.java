import java.util.Scanner;

class Account {
    private int accountNumber;
    private String accountHolderName;
    private double balance;
    private String email;
    private String phoneNumber;

    public Account(int accountNumber, String accountHolderName, double initialDeposit, String email, String phoneNumber) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = initialDeposit;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Deposit successful. New balance: ₹" + balance);
        } else {
            System.out.println("Invalid deposit amount. Please enter a positive value.");
        }
    }

    public void withdraw(double amount) {
        if (amount > 0) {
            if (balance >= amount) {
                balance -= amount;
                System.out.println("Withdrawal successful. New balance: ₹" + balance);
            } else {
                System.out.println("Insufficient balance.");
            }
        } else {
            System.out.println("Invalid withdrawal amount. Please enter a positive value.");
        }
    }

    public void displayAccountDetails() {
        System.out.println("\n------ Account Details ------");
        System.out.println("Account Number: " + accountNumber);
        System.out.println("Account Holder: " + accountHolderName);
        System.out.println("Balance: ₹" + balance);
        System.out.println("Email: " + email);
        System.out.println("Phone: " + phoneNumber);
        System.out.println("-----------------------------");
    }

    public void updateContactDetails(String email, String phoneNumber) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        System.out.println("Contact details updated successfully.");
    }
}

class UserInterface {
    private Account[] accounts;
    private int accountCount;
    private final Scanner scanner;
    private int nextAccountNumber = 1000;

    public UserInterface(int maxAccounts) {
        accounts = new Account[maxAccounts];
        accountCount = 0;
        scanner = new Scanner(System.in);
    }

    public void mainMenu() {
        while (true) {
            System.out.println("\n===== Banking Application Menu =====");
            System.out.println("1. Create New Account");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. View Account Details");
            System.out.println("5. Update Contact Details");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            int choice = readInt();

            switch (choice) {
                case 1:
                    createAccount();
                    break;
                case 2:
                    performDeposit();
                    break;
                case 3:
                    performWithdrawal();
                    break;
                case 4:
                    showAccountDetails();
                    break;
                case 5:
                    updateContact();
                    break;
                case 6:
                    System.out.println("Thank you for using our Banking Application. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void createAccount() {
        System.out.print("Enter Account Holder Name: ");
        String name = scanner.nextLine().trim();
        scanner.nextLine();

        System.out.print("Enter Initial Deposit Amount: ");
        double deposit = readDouble();

        System.out.print("Enter Email Address: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter Phone Number: ");
        String phone = scanner.nextLine().trim();

        nextAccountNumber++;
        Account newAccount = new Account(nextAccountNumber, name, deposit, email, phone);
        accounts[accountCount++] = newAccount;

        System.out.println("Account created successfully! Account Number: " + nextAccountNumber);
    }

    private void performDeposit() {
        Account account = findAccount();
        if (account != null) {
            System.out.print("Enter amount to deposit: ");
            double amount = readDouble();
            account.deposit(amount);
        }
    }

    private void performWithdrawal() {
        Account account = findAccount();
        if (account != null) {
            System.out.print("Enter amount to withdraw: ");
            double amount = readDouble();
            account.withdraw(amount);
        }
    }

    private void showAccountDetails() {
        Account account = findAccount();
        if (account != null) {
            account.displayAccountDetails();
        }
    }

    private void updateContact() {
        Account account = findAccount();
        if (account != null) {
            System.out.print("Enter new Email Address: ");
            String email = scanner.nextLine().trim();
            System.out.print("Enter new Phone Number: ");
            String phone = scanner.nextLine().trim();
            account.updateContactDetails(email, phone);
        }
    }

    // Utility methods for input and searching accounts
    private int readInt() {
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. Please enter a number: ");
            scanner.next(); // discard invalid input
        }
        return scanner.nextInt();
    }

    private double readDouble() {
        while (!scanner.hasNextDouble()) {
            System.out.print("Invalid input. Please enter a valid amount: ");
            scanner.next(); // discard invalid input
        }
        double value = scanner.nextDouble();
        scanner.nextLine(); // consume newline
        return value;
    }

    private Account findAccount() {
        System.out.print("Enter Account Number: ");
        int accNum = readInt();
        scanner.nextLine(); // consume newline
        for (int i = 0; i < accountCount; i++) {
            if (accounts[i].getAccountNumber() == accNum) {
                return accounts[i];
            }
        }
        System.out.println("Account not found.");
        return null;
    }
}

class BankingApp {
    public static void main(String[] args) {
        UserInterface ui = new UserInterface(100);  // Maximum 100 accounts supported
        ui.mainMenu();
    }
}