import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class P1 {
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
      System.out.println();
      Scanner sc = new Scanner(System.in);
      int choice = 0;
      while(choice != 3){
        System.out.println("1. New Customer");
        System.out.println("2. Customer Login");
        System.out.println("3. Exit");
        System.out.println("Enter your choice: ");
        choice = sc.nextInt();
        sc.nextLine();
        switch(choice){
          case 1 ->{
            screenTwo();
          }
          case 2 -> {
            int customerID = 0, pin = 0;
            System.out.println("Enter your customer ID: ");
            customerID = sc.nextInt();
            sc.nextLine();
            System.out.println("Enter your pin: ");
            pin = sc.nextInt();
            sc.nextLine();
            screenThree(customerID, pin);
          }
        }

      }
      sc.close();
    
    }
  }

  // New Customer Screen:
  static void screenTwo(){
    Scanner input = new Scanner(System.in);
    String gender = "", name = "", age = "", pin = "";
    System.out.print("\nEnter Name (no longer than 15 characters): ");
      try{
        name = input.nextLine();
        while(name.length() > 15){
          System.out.println("Name is too long.  Please enter a name that is less than 15 characters.");
          name = input.nextLine();
        }
      }catch(Exception e){
        System.out.println("EXCEPTION CAUGHT - Invalid name input. Returning to main menu...");
        input.close();
        return;
      }
      System.out.print("\nEnter gender (M or F): ");
      try{
        gender = input.nextLine();
      
        if(gender.isEmpty()){
          input.close();
          throw new NullPointerException();
        }
      }catch(Exception e){
        System.out.println("EXCEPTION CAUGHT - invalid gender input. Returning to main menu...");
        return;
      }
      if(gender.length() > 1 && (gender.charAt(0) != 'M' || gender.charAt(0) != 'F'))
        while(gender.charAt(0) != 'M' && gender.length() > 1){
          System.out.println("Please provide a valid gender (M or F): ");
          gender = input.nextLine();
        }
      System.out.print("\nEnter age: ");
      try{
        age = input.nextLine();
      }catch(Exception e){
        System.out.println("EXCEPTION CAUGHT - Invalid age. Returning to main menu...");
        input.close();
        return;
      }
      System.out.print("\nEnter pin: ");
      try{
        pin = input.nextLine();
      }
      catch(Exception e){
        System.out.println("EXCEPTION CAUGHT - Invalid pin. Returning to main menu...");
        input.close();
        return;
      }
      BankingSystem.newCustomer(name, gender, age, pin);    
      input.close();
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
        Scanner sc = new Scanner(System.in);
        int choice = 0;
        while(choice != 7){
          System.out.println("Enter your choice: ");
          choice = sc.nextInt();
          sc.nextLine();
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
                sc.close();
                return;
              }
              
            }
            case 2 -> {
              BankingSystem.closeAccount(customerID);
            }
            case 3 -> {
              BankingSystem.deposit(customerID);
            }
            case 4 -> {
              BankingSystem.withdraw(customerID);
            }
            case 5 -> {
              BankingSystem.transfer(customerID);
            }
            case 6 -> {
              BankingSystem.accountSummary(customerID);
            }
          }
        }
        sc.close();
      }
      else{
        System.out.println("Invalid customer ID or pin.  Returning to main menu...");
      }

    }
  }

  //Admin Screen:
  static void adminScreen(){
    System.out.println("Administrator Main Menu");
  }

}
