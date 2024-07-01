import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
class User {
    private String username;
    private int userID;
    private String email;
    private String pin;
    public User(String username, int userID, String email, String pin) {
        this.username = username;
        this.userID = userID;
        this.email = email;
        this.pin = pin;
    }
    public int getUserID() {
        return userID;
    }
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    public String getPin() {
        return pin;
    }
    public void setPin(String pin) {
        this.pin = pin;
    }
}
class Logger {
    public static void log(String message) {
        System.out.println("Logging: " + message);
    }
}
class Transaction {
    private static int transactionCounter = 0;
    private int transactionID;
    private int userID;
    private String username;
    private String email;
    private float amount;
    private String type;
    public Transaction(User user, float amount, String type)
    {this.transactionID = ++transactionCounter;
        this.userID = user.getUserID();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.amount = amount;
        this.type = type;
    }
    public int getTransactionID() {
        return transactionID;
    }
    public int getUserID() {
        return userID;
    }
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    public float getAmount() {
        return amount;
    }
    public String getType() {
        return type;
    }
}
class BankAccount {
    protected float balance;
    protected int accountNumber;
    protected List<Transaction> transactionHistory;
    protected User user;
    protected Logger logger;
    public BankAccount(User user, Logger logger) {
        this.balance = 0;
        this.accountNumber = user.getUserID();
        this.user = user;
        this.logger = logger;
        this.transactionHistory = new ArrayList<>();
    }
    public float getBalance() {
        return balance;
    }
    public int getAccountNumber() {
        return accountNumber;
    }
    public void addTransaction(Transaction transaction) {
        transactionHistory.add(transaction);
    }
    public List<Transaction> getTransactionHistory() {
        return transactionHistory;
    }
}
class ATM extends BankAccount {
    private int failedAttempts = 0;
    private static final int MAX_FAILED_ATTEMPTS = 3;
    public ATM(User user, Logger logger) {
        super(user, logger);
    }
    public void checkUsername() {
        String enteredUsername = JOptionPane.showInputDialog("Enter Your Username:");
        if (enteredUsername == null) {
            JOptionPane.showMessageDialog(null, "Invalid input. Please try again.");
            checkUsername();
            return;
        }
        if (!enteredUsername.equals(user.getUsername())) {
            JOptionPane.showMessageDialog(null, "Invalid username.");
            checkUsername();
            return;
        }
        checkPIN();
    }
    public void checkPIN() {
        if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            JOptionPane.showMessageDialog(null, "Account locked. Please contact customer support.");
            System.exit(0);
            return;
        }
        String enteredPIN = JOptionPane.showInputDialog("Enter Your PIN:");
        if (enteredPIN == null) {
            JOptionPane.showMessageDialog(null, "Invalid input. Please try again.");
            checkPIN();
            return;
        }
        if (!enteredPIN.equals(user.getPin())) {
            System.out.println("Invalid PIN. Please try again.");
            failedAttempts++;
            checkPIN();
        } else {
            failedAttempts = 0;
            menu();
        }
    }
    public void menu() {
        String choice = JOptionPane.showInputDialog("Enter Your Choice:\n1. Check Your Account Balance\n2. Withdraw Money\n3. Deposit Money\n4. Fund Transfer\n5. View Transaction History\n6. Change PIN\n7. Exit");
        if (choice == null) {
            return;
        }
        int opt = Integer.parseInt(choice);
        switch (opt) {
            case 1:
                checkBalance();
                break;
            case 2:
                withdrawMoney();
                break;
            case 3:
                depositMoney();
                break;
            case 4:
                fundTransfer();
                break;
            case 5:
                viewTransactionHistory();
                break;
            case 6:
                changePIN();
                break;
            case 7:
                System.exit(0);
                break;
            default:
                System.out.println("Invalid Choice");
                menu();
        }
    }
    public void checkBalance() {
        JOptionPane.showMessageDialog(null, "Your Account Balance is: " + balance);
        menu();
    }
    public void withdrawMoney() {
        float amount = Float.parseFloat(JOptionPane.showInputDialog("Enter Amount to Withdraw:"));
        if (amount > balance) {
            JOptionPane.showMessageDialog(null, "Insufficient Balance");
            withdrawMoney();
        } else {
            String transferChoice = JOptionPane.showInputDialog("Where will the money be sent?\n1. User ID\n2. Username");
            if (transferChoice == null) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please try again.");
                withdrawMoney();
                return;
            }
            String recipientInfo = "";
            User recipientUser = null;

            if (transferChoice.equals("1")) {
                int recipientUserID = Integer.parseInt(JOptionPane.showInputDialog("Enter Recipient's User ID:"));
                for (User user : AtmBankingSystem.getUsers()) {
                    if (user.getUserID() == recipientUserID) {
                        recipientUser = user;
                        recipientInfo = "User ID: " + recipientUserID;
                        break;
                    }
                }
            } else if (transferChoice.equals("2")) {
                String recipientUsername = JOptionPane.showInputDialog("Enter Recipient's Username:");
                for (User user : AtmBankingSystem.getUsers()) {
                    if (user.getUsername().equals(recipientUsername)) {
                        recipientUser = user;
                        recipientInfo = "Username: " + recipientUsername;
                        break;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Invalid choice. Please try again.");
                withdrawMoney();
                return;
            }
            if (recipientUser == null) {
                JOptionPane.showMessageDialog(null, "Recipient not found. Please try again.");
                withdrawMoney();
                return;
            }
            int confirmation = JOptionPane.showConfirmDialog(null, "Send " + amount + " to " + recipientInfo + "?");
            if (confirmation == JOptionPane.YES_OPTION) {
                balance -= amount;
                Transaction transaction = new Transaction(user, amount, "Withdrawal to " + recipientInfo);
                addTransaction(transaction);
                logger.log("Withdrawal of " + amount + " to " + recipientInfo + " successful");
                JOptionPane.showMessageDialog(null, "Withdrawal Successful");
            } else {
                JOptionPane.showMessageDialog(null, "Withdrawal Cancelled");
            }
            menu();
        }
    }public void depositMoney() {
        float amount = Float.parseFloat(JOptionPane.showInputDialog("Enter Amount to Deposit:"));
        balance += amount;
        Transaction transaction = new Transaction(user, amount, "Deposit");
        addTransaction(transaction);
        logger.log("Deposit of " + amount + " successful");
        JOptionPane.showMessageDialog(null, "Deposit Successful");
        menu();
    }
    public void fundTransfer() {
        int recipientAccountNumber = Integer.parseInt(JOptionPane.showInputDialog("Enter Recipient's Account Number:"));
        float amount = Float.parseFloat(JOptionPane.showInputDialog("Enter Amount to Transfer:"));
        if (amount > balance) {
            JOptionPane.showMessageDialog(null, "Insufficient Balance");
            fundTransfer();
        } else {
            balance -= amount;
            Transaction transaction = new Transaction(user, amount, "Fund Transfer");
            addTransaction(transaction);
            logger.log("Fund Transfer of " + amount + " to Account " + recipientAccountNumber + " successful");
            JOptionPane.showMessageDialog(null, "Fund Transfer Successful");
            menu();
        }
    }public void viewTransactionHistory() {
        StringBuilder transactionHistory = new StringBuilder("Transaction History:\n");
        for (Transaction transaction : getTransactionHistory()) {
            transactionHistory.append("Transaction ID: ").append(transaction.getTransactionID()).append("\n");
            transactionHistory.append("User ID: ").append(transaction.getUserID()).append("\n");
            transactionHistory.append("Username: ").append(transaction.getUsername()).append("\n");
            transactionHistory.append("Email: ").append(transaction.getEmail()).append("\n");
            transactionHistory.append("Amount: ").append(transaction.getAmount()).append("\n");
            transactionHistory.append("Type: ").append(transaction.getType()).append("\n\n");
        }
        JOptionPane.showMessageDialog(null, transactionHistory.toString());
        menu();
    }public void changePIN() {
        String enteredUsername = JOptionPane.showInputDialog("Enter Your Username:");
        if (enteredUsername == null || !enteredUsername.equals(user.getUsername())) {
            JOptionPane.showMessageDialog(null, "User ID not found");
            menu();
            return;
        }
        String oldPIN = JOptionPane.showInputDialog("Enter Your Old PIN:");
        if (!oldPIN.equals(user.getPin())) {
            JOptionPane.showMessageDialog(null, "Invalid Old PIN");
            changePIN();
        } else {
            String newPIN = JOptionPane.showInputDialog("Enter Your New PIN:");
            user.setPin(newPIN);
            logger.log("PIN changed successfully");
            JOptionPane.showMessageDialog(null, "PIN changed successfully");
            menu();
        }
    }
}

public class AtmBankingSystem {
    private static List<User> users = new ArrayList<>();

    public static List<User> getUsers() {
        return users;
    }

    public static void main(String[] args) {
        Logger logger = new Logger();

        // Pre-create a user for testing.\\
        User user1 = new User("Rakesh", 1234, "Rakesh@gmail.com", "5674");
        users.add(user1);

        // Create main frame.\\

        JFrame frame = new JFrame("ATM Banking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);

        // Create panel for buttons.\\
        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel, logger);

        // Set frame visibility.\\
        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel, Logger logger) {
        panel.setLayout(null);


        // Create Login button .\\
        JButton loginButton = new JButton("Login");

        loginButton.setBounds(50, 70, 120, 25);
        panel.add(loginButton);

        // Create Account button.\\
        JButton createAccountButton = new JButton("Create Account");
        createAccountButton.setBounds(200, 70, 120, 25);
        panel.add(createAccountButton);

        // Adding Action listener for Login Button.\\
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = JOptionPane.showInputDialog("Enter Your Username:");
                if (username != null) {
                    for (User user : users) {
                        if (user.getUsername().equals(username)) {
                            ATM atm = new ATM(user, logger);
                            atm.checkPIN();
                            return;
                        }
                    }
                    JOptionPane.showMessageDialog(null, "User not found.");
                }
            }
        });

        // Adding Action listener for create Account Button.//
        createAccountButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = JOptionPane.showInputDialog("Enter Username:");
                String email = JOptionPane.showInputDialog("Enter Email:");
                String pin = JOptionPane.showInputDialog("Enter PIN:");
                if (username != null && email != null && pin != null) {
                    int userID = users.size() + 1; // Simple userID generation logic
                    User newUser = new User(username, userID, email, pin);
                    users.add(newUser);
                    JOptionPane.showMessageDialog(null, "Account created successfully. Your User ID is " + userID);
                } else {
                    JOptionPane.showMessageDialog(null, "All fields are required.");
                }

            }
        });
    }
}