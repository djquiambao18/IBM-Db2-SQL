import java.util.HashSet;
import java.util.Properties;
import java.util.Scanner;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class P1 {
  // Connection properties
	private static String driver;
	private static String url;
	private static String username;
	private static String password;
  private static Scanner sc;

  // JDBC Objects
	private static Connection conn;
	private static Statement stmt;
	private static ResultSet rs;
  public static void main(String [] args){
    if(args.length < 1){
      System.out.println("Need database properties filename");
    }
    else{
      System.out.println("Welcome to the Self Services Banking System! - Main Menu");
      /*Screen # 1 (Title - Welcome to the Self Services Banking System! - Main Menu)
      1.	New Customer
      2.	Customer Login
      3.	Exit

      For #1, prompt for Name, Gender, Age, and Pin.  System will return a customer ID if successful.
      For #2, prompt for customer ID and pin to authenticate the customer.  If user enters 0 for both customer ID & pin, then you will go straight to Screen #4.
      */
      
      BankingSystem.init(args[0]);
      BankingSystem.testConnection();
      try{
        init(args[0]);
        Class.forName(driver);
			  conn = DriverManager.getConnection(url, username, password);
      }catch(SQLException e){
        System.out.println("SQL Error: " + e.getMessage());
        System.out.println("Try running program again...Goodbye!");
        return;
      }catch(ClassNotFoundException e){
        System.out.println("Class Not Found Error: " + e.getMessage());
        System.out.println("Exiting program...");        
        return;
      }
      System.out.println();
      sc = new Scanner(System.in);
      int choice = 0;

        System.out.println("1. New Customer");
        System.out.println("2. Customer Login");
        System.out.println("3. Exit");
        System.out.println("Enter your choice: ");
      try{
        choice = Integer.parseInt(sc.nextLine());
        while(choice != 3){
            switch(choice){
              case 1 ->{
                screenTwo();
              }
              case 2 -> {
                int customerID = 0, pin = 0;
                System.out.println("Enter your customer ID: ");
                customerID = Integer.parseInt(sc.nextLine());
                System.out.println("Enter your pin: ");
                pin = Integer.parseInt(sc.nextLine());
                screenThree(customerID, pin);
              }
              case 3 -> {
                System.out.println("Exiting program...");
              }
              default ->{
                System.out.println("Choose a valid option!");
              }
            }
            System.out.println("1. New Customer");
            System.out.println("2. Customer Login");
            System.out.println("3. Exit");
            System.out.println("Enter your choice: ");
            choice = Integer.parseInt(sc.nextLine());
          }
        System.out.println("Thank you for using the program!");
        sc.close(); 
      }catch(Exception e){
        e.printStackTrace();
      }
    }
  }

  // New Customer Screen:
  static void screenTwo(){
    // Scsc = new Scanner(System.in);
      String gender = "", name = "", age = "", pin = "";
      System.out.print("\nEnter Name (no longer than 15 characters): ");
        try{
          name = sc.nextLine();
          while(name.length() > 15){
            System.out.println("Name is too long.  Please enter a name that is less than 15 characters.");
            name = sc.nextLine();
          }
        }catch(Exception e){
          System.out.println("EXCEPTION CAUGHT - Invalid name input. Returning to main menu...");
          
        }
        System.out.print("\nEnter gender (M or F): ");
        try{
          gender = sc.nextLine();
          if(gender.isEmpty()){
            throw new NullPointerException();
          }
        }catch(Exception e){
          System.out.println("EXCEPTION CAUGHT - invalid gender input. Returning to main menu...");
          return;
        }
        if(gender.length() > 1 && (gender.charAt(0) != 'M' || gender.charAt(0) != 'F'))
          while(gender.charAt(0) != 'M' && gender.length() > 1){
            System.out.println("Please provide a valid gender (M or F): ");
            gender = sc.nextLine();
          }
        System.out.print("\nEnter age: ");
        try{
          age = sc.nextLine();
        }catch(Exception e){
          System.out.println("EXCEPTION CAUGHT - Invalid age. Returning to main menu...");
          
          return;
        }
        System.out.print("\nEnter pin: ");
        try{
          pin = sc.nextLine();
        }
        catch(Exception e){
          System.out.println("EXCEPTION CAUGHT - Invalid pin. Returning to main menu...");
          return;
        }
        BankingSystem.newCustomer(name, gender, age, pin);
       
  }    

  static void screenThree(int customerID, int pin){
      /* 
        Screen # 3 (Title - Customer Main Menu)
        1.	Open Account
        2.	Close Account
        3.	Deposit
        4.	Withdraw
        5.	Transfer
        6.	Account Summary
        7.	Exit

        For #1, prompt for customer ID, account type, and balance (Initial deposit).  System will return an account number if successful.
        For #2, prompt for account #, change the status attribute to 'I' and empty the balance for that account. 
        For #3, prompt for account # and deposit amount.
        For #4, prompt for account # and withdraw amount.
        For #5, prompt for the source and destination account #s and transfer amount.
        For #6, display each account # and its balance for same customer and the total balance of all accounts.
        For #7, go back to the previous menu.
      */
      if(customerID == 0 && pin == 0){
        adminScreen();
      }
      else{
        try{
          stmt = conn.createStatement();
          String query = "SELECT * FROM P1.customer WHERE id = " + customerID + " AND pin = " + pin;
          rs = stmt.executeQuery(query);
          if(rs.next()){
            System.out.println("Welcome " + rs.getString("name") + "!");
            System.out.println("1. Open Account");
            System.out.println("2. Close Account");
            System.out.println("3. Deposit");
            System.out.println("4. Withdraw");
            System.out.println("5. Transfer");
            System.out.println("6. Account Summary");
            System.out.println("7. Exit");  
            System.out.print("Enter your choice: ");
            int choice = Integer.parseInt(sc.nextLine());
              while(choice != 7){
                switch(choice){
                  case 1 -> {
                    System.out.println("Open Account");
                    try{
                      System.out.print("\nEnter customer ID: ");
                      String cid = sc.nextLine();
                      System.out.print("\nEnter account type ('C' for Checking or 'S' for Savings): ");
                      String type = sc.nextLine();
                      System.out.print("\nEnter balance (initial deposit): ");
                      String balance = sc.nextLine();
                      BankingSystem.openAccount(cid, type, balance);
                    }catch(Exception e){
                      System.out.println("EXCEPTION CAUGHT - Invalid input. Returning to main menu...");
                      
                      return;
                    }
                    
                  }
                  case 2 -> {
                    System.out.println("Close Account");
                    try{
                      stmt = conn.createStatement();
                      query = "SELECT number FROM P1.account WHERE customer_id = " + customerID;
                      rs = stmt.executeQuery(query);
                      HashSet<String> accountNumbers = new HashSet<String>();
                      while(rs.next()){
                        accountNumbers.add(rs.getString(1));
                      }
                      System.out.print("\nEnter account number: ");
                      String account = sc.nextLine();
                      
                      if(accountNumbers.contains(account)){
                        BankingSystem.closeAccount(account);
                      }
                      else{
                        System.out.println("Invalid account number.");
                      }
                      
                    }catch(Exception e){
                      System.out.println("EXCEPTION CAUGHT - Invalid input. Returning to main menu...");
                      
                      return;
                    }
                    
                  }
                  case 3 -> {
                    System.out.println("Deposit");
                    try{
                      stmt = conn.createStatement();
                      query = "SELECT number FROM P1.account";
                      rs = stmt.executeQuery(query);
                      HashSet<String> accountNumbers = new HashSet<String>();
                      while(rs.next()){
                        accountNumbers.add(rs.getString(1));
                      }
                      System.out.print("\nEnter account number: ");
                      String account = sc.nextLine();
                      if(accountNumbers.contains(account)){
                        System.out.print("\nEnter deposit amount: ");
                        String amount = sc.nextLine();
                        BankingSystem.deposit(account, amount);
                      }
                      else{
                        System.out.println("Invalid account number.");
                      }
                    }catch(Exception e){
                      System.out.println("EXCEPTION CAUGHT - Invalid input. Returning to main menu...");
                      return;
                    }
                  }
                  case 4 -> {
                    System.out.println("Withdraw");
                    try{
                      stmt = conn.createStatement();
                      query = "SELECT number FROM P1.account WHERE id = " + customerID;
                      rs = stmt.executeQuery(query);
                      HashSet<String> accountNumbers = new HashSet<String>();
                      while(rs.next()){
                        accountNumbers.add(rs.getString(1));
                      }
                      System.out.print("\nEnter account number: ");
                      String account = sc.nextLine();
                      if(accountNumbers.contains(account)){
                        System.out.print("\nEnter withdraw amount: ");
                        String amount = sc.nextLine();
                        BankingSystem.withdraw(account, amount);
                      }
                      else{
                        System.out.println("Invalid account number.");
                      }
                    }catch(Exception e){
                      System.out.println("EXCEPTION CAUGHT - Invalid input. Returning to main menu...");
                      return;
                    }
                  }
                  case 5 -> {
                    System.out.println("Transfer");
                    try{
                      stmt = conn.createStatement();
                      query = "SELECT number FROM P1.account";
                      rs = stmt.executeQuery(query);
                      HashSet<String> accountNumbers = new HashSet<String>();
                      while(rs.next()){
                        accountNumbers.add(rs.getString(1));
                      }
                      stmt = conn.createStatement();
                      query = "SELECT number FROM P1.account WHERE id = " + customerID;
                      rs = stmt.executeQuery(query);
                      HashSet<String> customerAccountNumbers = new HashSet<String>();
                      while(rs.next()){
                        customerAccountNumbers.add(rs.getString(1));
                      }
                      System.out.print("\nEnter source account number: ");
                      String source = sc.nextLine();
                      if(customerAccountNumbers.contains(source)){
                        System.out.print("\nEnter destination account number: ");
                        String destination = sc.nextLine();
                        if(accountNumbers.contains(destination)){
                          System.out.print("\nEnter transfer amount: ");
                          String amount = sc.nextLine();
                          BankingSystem.transfer(source, destination, amount);
                        }
                        else{
                          System.out.println("Invalid destination account number.");
                        }
                      }
                      else{
                        System.out.println("Invalid source account number.");
                      }
                    }catch(Exception e){
                      System.out.println("EXCEPTION CAUGHT - Invalid input. Returning to main menu...");
                      
                      return;
                    }
                  }
                  case 6 -> {
                    System.out.println("Account Summary");
                    BankingSystem.accountSummary(Integer.toString(customerID));
                  }
                  case 7 -> {
                    System.out.println("Returning to main menu...");
                  }
                  default -> {
                    System.out.println("Invalid choice.");
                  }
                }
                System.out.println("1. Open Account");
                System.out.println("2. Close Account");
                System.out.println("3. Deposit");
                System.out.println("4. Withdraw");
                System.out.println("5. Transfer");
                System.out.println("6. Account Summary");
                System.out.println("7. Exit");  
                System.out.print("Enter your choice: ");
                choice = Integer.parseInt(sc.nextLine());
              }
            }
            
          
          else{
            System.out.println("Invalid customer ID or pin.  Returning to main menu...");
          }
      }catch(SQLException e){
        System.out.println("SQL Exception. Returning to main menu...");
        return;
      }catch(Exception e){
        System.out.println("Exception. Returning to main menu...");
        return;
      }
    }
  }

  //Admin Screen:
  static void adminScreen(){
    System.out.println("Administrator Main Menu");
   
    System.out.println("1. Account Summary for a Customer");
    System.out.println("2. Report A :: Customer Information with Total Balance in Decreasing Order");
    System.out.println("3. Report B :: Find the Average Total Balance Between Age Groups");
    System.out.println("4. Exit");
    System.out.print("Enter your choice: ");
    int choice = Integer.parseInt(sc.nextLine());
    while(choice != 4){
      try{
        switch(choice){
          case 1 -> {
            System.out.print("\nEnter customer ID: ");
            String customerID = sc.nextLine();
            BankingSystem.accountSummary(customerID);
          }
          case 2 -> {
            BankingSystem.reportA();
          }
          case 3 -> {
            // stmt = conn.createStatement();
            // String query = "SELECT  FROM P1.customer";
            System.out.print("\nEnter minimum age: ");
            String minAge = sc.nextLine();
            System.out.print("\nEnter maximum age: ");
            String maxAge = sc.nextLine();
            BankingSystem.reportB(minAge, maxAge);
          }
          default -> {
            System.out.println("Invalid choice.");
          }
        }
        System.out.println("1. Account Summary for a Customer");
        System.out.println("2. Report A :: Customer Information with Total Balance in Decreasing Order");
        System.out.println("3. Report B :: Find the Average Total Balance Between Age Groups");
        System.out.println("4. Exit");
        System.out.print("Enter your choice: ");
        choice = Integer.parseInt(sc.nextLine());
      }catch(Exception e){
        System.out.println("EXCEPTION CAUGHT - Invalid input. Returning to main menu...");
        return;
      }
    }

  }

  static void init(String filename){
    try {
			Properties props = new Properties();						// Create a new Properties object
			FileInputStream input = new FileInputStream(filename);	// Create a new FileInputStream object using our filename parameter
			props.load(input);										// Load the file contents into the Properties object
			driver = props.getProperty("jdbc.driver");				// Load the driver
			url = props.getProperty("jdbc.url");						// Load the url
			username = props.getProperty("jdbc.username");			// Load the username
			password = props.getProperty("jdbc.password");			// Load the password
		} catch (Exception e) {
			e.printStackTrace();
		}
  }

}
