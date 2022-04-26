import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Manage connection to database and perform SQL statements.
 */
public class BankingSystem {
	// Connection properties
	private static String driver;
	private static String url;
	private static String username;
	private static String password;
	
	// JDBC Objects
	private static Connection con;
	private static Statement stmt;
	private static ResultSet rs;

	/**
	 * Initialize database connection given properties file.
	 * @param filename name of properties file
	 */
	public static void init(String filename) {
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
	
	/**
	 * Test database connection.
	 */
	public static void testConnection() {
		System.out.println(":: TEST - CONNECTING TO DATABASE");
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, username, password);
			// con.close();
			System.out.println(":: TEST - SUCCESSFULLY CONNECTED TO DATABASE");
			} catch (Exception e) {
				System.out.println(":: TEST - FAILED CONNECTED TO DATABASE");
				e.printStackTrace();
			}
	  }

	/**
	 * Create a new customer.
	 * @param name customer name
	 * @param gender customer gender
	 * @param age customer age
	 * @param pin customer pin
	 */
	public static void newCustomer(String name, String gender, String age, String pin) 
	{
		System.out.println(":: CREATE NEW CUSTOMER - RUNNING");
		// Useful scoped vars:
		int n_age = 0, n_pin = 0;
		char ch_gender = 0;

		// Input Validation
		try{
			if(gender.length() == 1 && !age.isEmpty() && !pin.isEmpty()){
				name = name.trim(); // clean out both ends of string of whitespaces
				try{
					n_age = Integer.parseInt(age); // check if age is an integer
				}catch(NumberFormatException e){
					System.out.println(":: CREATE NEW CUSTOMER - ERROR - INVALID AGE");
					return;
				}
				try{
					n_pin = Integer.parseInt(pin); // check if pin is an integer
				}catch(NumberFormatException e){
					System.out.println(":: CREATE NEW CUSTOMER - ERROR - INVALID PIN");
					return;
				}

				try{
					gender = gender.toUpperCase();
					ch_gender = gender.charAt(0);
				}catch(Exception e){
					System.out.println(":: CREATE NEW CUSTOMER - ERROR - INVALID GENDER");
				}
			}
			else{
				if(pin.isEmpty()){
					System.out.println("Pin cannot be empty");
				}
				else if(age.isEmpty()){
					System.out.println("Age cannot be empty");
				}
				else if(gender.isEmpty()){
					System.out.println("Gender cannot be empty");
				}
				throw new IllegalArgumentException();
			}
		}
		catch(Exception e){
			System.out.println(":: CREATE NEW CUSTOMER - FAILED");
			System.out.println("Invalid input" + " " + e.getMessage());
			e.printStackTrace();
			return;
		}
		if(!name.isEmpty() && name.matches("[a-zA-Z ]+") && n_age >= 0 && n_pin >= 0 && (ch_gender == 'M' || ch_gender == 'F')) // check if name is alphabetic and age is between 0 and 120
		{
			if(name.length() <= 15){
				try {
					// Class.forName(driver);
					// con = DriverManager.getConnection(url, username, password);
					String query = "INSERT INTO P1.CUSTOMER (name, gender, age, pin) VALUES ('"
									+ name + "', '" + ch_gender + "', " + n_age + ", " + n_pin + ");";
					stmt = con.createStatement();
					stmt.executeUpdate(query);
					rs = stmt.getResultSet();
					// if(rs != null){
					// 	System.out.println(rs.getInt(1));
					// 	rs.close();
					// }
					
					query = "SELECT ID FROM P1.CUSTOMER WHERE NAME = '" + name + "' AND PIN = " + n_pin + ";";
					stmt = con.createStatement();
					rs = stmt.executeQuery(query);
					int result = 0;
					if(rs.next())
						result = rs.getInt(1);
					System.out.println("ID: " + result + " added");
					// rs.close();
					// stmt.close();
					// con.close();
					
					
					System.out.println(":: CREATE NEW CUSTOMER - SUCCESS");
					
				} catch(SQLException e){
					System.out.print("SQLException");
					System.out.println(":: CREATE NEW CUSTOMER - FAILED");
					e.printStackTrace();

				} catch(Exception e) {
					System.out.println(":: CREATE NEW CUSTOMER - FAILED");
					e.printStackTrace();
				}
			}
			else{
				System.out.println("System does not support names longer than 15 characters");
				System.out.println(":: CREATE NEW CUSTOMER - FAILED");
			}
		
		}
		else{
			if(ch_gender != 'M' || ch_gender != 'F')
			{
				System.out.println("Invalid gender");
			}
			else if(name.isEmpty()){
				System.out.println("Name cannot be empty!");
			}
			else if(!name.matches("[a-zA-Z ]+")){
				System.out.println("Name is not a valid name (not recognized as a character in the A-Z range)");
			}
			else if(n_age < 0){
				System.out.println("Age cannot be negative");
			}
			else if(n_pin < 0){
				System.out.println("Pin cannot be negative");
			}
			System.out.println(":: CREATE NEW CUSTOMER - FAILED");
		}
	}

	/**
	 * Open a new account.
	 * @param id customer id
	 * @param type type of account
	 * @param amount initial deposit amount
	 */
	public static void openAccount(String id, String type, String amount) 
	{
		System.out.println(":: OPEN ACCOUNT - RUNNING");
				// Open Account SQL Query
				char ch_type = 0;
				int n_id = 0, n_amount = 0;
				if(id.isEmpty() || type.isEmpty() || amount.isEmpty())
				{
					throw new NullPointerException(":: OPEN ACCOUNT - ERROR - EMPTY INPUT");
				}
				try{
					try{
						n_id = Integer.parseInt(id);
					}catch(NumberFormatException e){
						System.out.println(":: OPEN ACCOUNT - ERROR - INVALID ID");
						return;
					}
					try{
						type = type.toUpperCase();
						ch_type = type.charAt(0);
					}catch(Exception e){
						System.out.println(":: OPEN ACCOUNT - ERROR - INVALID TYPE");
						return;
					}
					try{
						n_amount = Integer.parseInt(amount);
					}catch(NumberFormatException e){
						System.out.println(":: OPEN ACCOUNT - ERROR - INVALID AMOUNT");
						return;
					}
					if((ch_type != 'C' || ch_type != 'S') && n_amount < 0 && n_id < 100)
					{
						throw new IllegalArgumentException(":: OPEN ACCOUNT - ERROR - INVALID INPUT");
					}
				}
				catch(IllegalArgumentException | NullPointerException e){
					e.printStackTrace();
					return;
				}
				try {
					
						// Class.forName(driver);
						// con = DriverManager.getConnection(url, username, password);
						stmt = con.createStatement();
						String query = "INSERT INTO p1.account (id, type, balance, status) VALUES (" 
										+ n_id + ", '" + ch_type + "', " + n_amount + ", 'A');"; //A for active, I for Inactive
						stmt.executeUpdate(query);
						query = "SELECT number FROM P1.ACCOUNT WHERE ID = " + n_id + " AND TYPE = '" + ch_type + "';";
						rs = stmt.executeQuery(query);
						if(rs.next())
							System.out.println("Account number: " + rs.getInt(1) + " added");

						// rs.close();
						// stmt.close();
						// con.close();
						System.out.println(":: OPEN ACCOUNT - SUCCESS");
						return;

				} catch(SQLException e){
					System.out.print("SQLException");
					
					System.out.println(":: OPEN ACCOUNT - FAILED");
					e.printStackTrace();
					return;

				} catch(Exception e) {
					System.out.println(":: OPEN ACCOUNT - FAILED");
					e.printStackTrace();
					return;
				}
	}

	/**
	 * Close an account.
	 * @param accNum account number
	 */
	public static void closeAccount(String accNum) 
	{
		System.out.println(":: CLOSE ACCOUNT - RUNNING");
				int n_accNum = 0;
				try{
					if(accNum.isEmpty())
					{
						throw new NullPointerException(":: CLOSE ACCOUNT - ERROR - EMPTY INPUT");
					}
				} catch(NullPointerException e)
					{
						e.printStackTrace();
						return;
					}
				try{
					n_accNum = Integer.parseInt(accNum);
					if(n_accNum < 1000)
					{
						throw new IllegalArgumentException(":: CLOSE ACCOUNT - ERROR - INVALID ACCOUNT NUMBER");
					}
				} catch(IllegalArgumentException e)
					{
						e.printStackTrace();
						return;
					}
				try {
					// Class.forName(driver);
					// con = DriverManager.getConnection(url, username, password);
					stmt = con.createStatement();
					// Set Status to I for account number and set amount to 0
					String query = "UPDATE p1.account SET status = 'I', balance = 0 WHERE number = " + n_accNum + ";";
					stmt.executeUpdate(query);
					// stmt.close();
					// con.close();
					System.out.println(":: CLOSE ACCOUNT - SUCCESS");
				} catch(SQLException e){
					System.out.print("SQLException");
					System.out.println(":: CLOSE ACCOUNT - FAILED");
					e.printStackTrace();

				} catch(Exception e) {
					System.out.println(":: CLOSE ACCOUNT - FAILED");
					e.printStackTrace();
				}
	}

	/**
	 * Deposit into an account.
	 * @param accNum account number
	 * @param amount deposit amount
	 */
	public static void deposit(String accNum, String amount) 
	{
		System.out.println(":: DEPOSIT - RUNNING");
				int n_accNum = 0;
				int n_amount = 0;
				try{
					if(accNum.isEmpty() || amount.isEmpty())
					{
						throw new NullPointerException(":: DEPOSIT - ERROR - EMPTY INPUT");
					}
				} catch(NullPointerException e)
					{
						e.printStackTrace();
						return;
					}
				try{
					try{
						n_accNum = Integer.parseInt(accNum);
					}catch(NumberFormatException e){
						System.out.println(":: DEPOSIT - ERROR - INVALID ACCOUNT NUMBER");
						return;
					}
					try{
						n_amount = Integer.parseInt(amount);
					}catch(NumberFormatException e){
						System.out.println(":: DEPOSIT - ERROR - INVALID AMOUNT");
						return;
					}
					if(n_accNum < 1000 || n_amount < 0)
						throw new IllegalArgumentException(":: DEPOSIT - ERROR - INVALID AMOUNT");
					
				} catch(IllegalArgumentException e)
					{
						e.printStackTrace();
						return;
					}
				try {
					// Class.forName(driver);
					// con = DriverManager.getConnection(url, username, password);
					stmt = con.createStatement();
					// Set balance to balance + amount
					String query = "UPDATE p1.account SET balance = balance + " + n_amount + " WHERE number = " + n_accNum + ";";
					stmt.executeUpdate(query);
					// rs = stmt.getResultSet();
					// rs.close();
					// stmt.close();
					// con.close();
					System.out.println(":: DEPOSIT - SUCCESS");
				} catch(SQLException e){
					System.out.println(":: DEPOSIT - FAILED");
					e.printStackTrace();
					return;
				}
				catch(Exception e){
					e.printStackTrace();
					System.out.println(":: DEPOSIT - FAILED");
					return;
				}
	}

	/**
	 * Withdraw from an account.
	 * @param accNum account number
	 * @param amount withdraw amount
	 */
	public static void withdraw(String accNum, String amount) 
	{
		System.out.println(":: WITHDRAW - RUNNING");
				int n_accNum = 0;
				int n_amount = 0;
				try{
					if(accNum.isEmpty() || amount.isEmpty())
					{
						throw new NullPointerException(":: WITHDRAW - ERROR - EMPTY INPUT");
					}
				} catch(NullPointerException e)
					{
						e.printStackTrace();
						return;
					}
				try{
					try{
						n_accNum = Integer.parseInt(accNum);
					}catch (IllegalArgumentException e){
						System.out.println(":: WITHDRAW - ERROR - INVALID ACCOUNT NUMBER");
						return;
					}
					try{
						n_amount = Integer.parseInt(amount);
					}catch(IllegalArgumentException e){
						System.out.println(":: WITHDRAW - ERROR - INVALID AMOUNT");
						return;
					}

					if(n_accNum < 1000 || n_amount < 0)
						throw new NumberFormatException(":: WITHDRAW - ERROR - INVALID AMOUNT");
					
				} catch(NumberFormatException e)
					{
						e.printStackTrace();
						return;
					}
				try {
					// Class.forName(driver);
					// con = DriverManager.getConnection(url, username, password);
					stmt = con.createStatement();
					// Set balance to balance - amount
					String query = "SELECT balance FROM P1.account WHERE number = " + n_accNum + ";";
					rs = stmt.executeQuery(query);
					if(rs.next()){
						if((rs.getInt(1) - n_amount) < 0){
							System.out.println(":: WITHDRAW - ERROR - NOT ENOUGH FUNDS");
							return;
						}
					}
					
					query = "UPDATE p1.account SET balance = balance - " + n_amount + " WHERE number = " + n_accNum + ";";
					stmt.executeUpdate(query);
					// rs = stmt.getResultSet();
					// rs.close();
					// stmt.close();
					// con.close();
					System.out.println(":: WITHDRAW - SUCCESS");
				} catch(SQLException e){
					System.out.print("SQLException");
					System.out.println(":: WITHDRAW - FAILED");
					e.printStackTrace();
					return;
				}	catch(Exception e){
					System.out.println("Exception");
					e.printStackTrace();
					System.out.println(":: WITHDRAW - FAILED");
					return;
				}
			
	}

	/**
	 * Transfer amount from source account to destination account. 
	 * @param srcAccNum source account number
	 * @param destAccNum destination account number
	 * @param amount transfer amount
	 */
	public static void transfer(String srcAccNum, String destAccNum, String amount) 
	{
		System.out.println(":: TRANSFER - RUNNING");
				int n_srcAccNum = 0;
				int n_destAccNum = 0;
				int n_amount = 0;
				try{
					if(srcAccNum.isEmpty() || destAccNum.isEmpty() || amount.isEmpty())
					{
						throw new NullPointerException(":: TRANSFER - ERROR - EMPTY INPUT");
					}
				} catch(NullPointerException e)
					{
						e.printStackTrace();
						return;
					}
				try{
					try{
						n_srcAccNum = Integer.parseInt(srcAccNum);
					} catch(IllegalArgumentException e){
						System.out.println(":: TRANSFER - ERROR - INVALID SOURCE ACCOUNT NUMBER");
						return;
					}
					try{
						n_destAccNum = Integer.parseInt(destAccNum);
					} catch(IllegalArgumentException e){
						System.out.println(":: TRANSFER - ERROR - INVALID DESTINATION ACCOUNT NUMBER");
						return;
					}
					try{
						n_amount = Integer.parseInt(amount);
					}
					catch(IllegalArgumentException e){
						System.out.println(":: TRANSFER - ERROR - INVALID AMOUNT");
						return;
					}
					if(n_srcAccNum < 1000 || n_destAccNum < 1000 || n_amount < 0)
						throw new IllegalArgumentException(":: TRANSFER - ERROR - INVALID INPUT");
					
				} catch(IllegalArgumentException e)
					{
						e.printStackTrace();
						return;
					}
				try {
					// Class.forName(driver);
					// con = DriverManager.getConnection(url, username, password);
					stmt = con.createStatement();

					// Set balance to balance - amount
					String query = "SELECT balance FROM p1.account WHERE number = " + n_srcAccNum + ";";
					rs = stmt.executeQuery(query);
					int tempBal = 0;
					if(rs.next()){
						tempBal = rs.getInt(1);
						if((tempBal - n_amount) < 0){
							System.out.println(":: TRANSFER - ERROR - NOT ENOUGH FUNDS");
							return;
						}
						else
						{
							query = "UPDATE p1.account SET balance = balance - " + n_amount + " WHERE number = " + n_srcAccNum + ";";
							stmt.executeUpdate(query);
						}

					}
					
					query = "SELECT balance FROM p1.account WHERE number = " + n_destAccNum + ";";
					rs = stmt.executeQuery(query);
					if(rs.next()){
						tempBal = rs.getInt(1);
						query = "UPDATE p1.account SET balance = balance + " + n_amount + " WHERE number = " + n_destAccNum + ";";
						stmt.executeUpdate(query);
					}
					// rs = stmt.getResultSet();

					// rs.close();
					
					// Set balance to balance + amount
					// query = "UPDATE p1.account SET balance = balance + " + n_amount + " WHERE number = " + n_destAccNum + ";";
					// stmt.executeUpdate(query);
					// rs = stmt.getResultSet();

					// rs.close();
					// stmt.close();
					// con.close();

				System.out.println(":: TRANSFER - SUCCESS");
				} catch(SQLException e){
					System.out.println(":: TRANSFER - FAILED");
					e.printStackTrace();
					return;
				}	catch(Exception e){
					System.out.println(":: TRANSFER - FAILED");
					e.printStackTrace();
					
					return;
				}
	}

	/**
	 * Display account summary.
	 * @param cusID customer ID
	 */
	public static void accountSummary(String cusID) 
	{
		System.out.println(":: ACCOUNT SUMMARY - RUNNING");
		// display each account # and its balance for same customer and the total balance of all accounts.
		int n_cusID = 0;
		try{
			if(cusID.isEmpty())
			{
				throw new NullPointerException(":: ACCOUNT SUMMARY - ERROR - EMPTY INPUT");
			}
		} catch(NullPointerException e)
			{
				e.printStackTrace();
				return;
			}
		try{
			n_cusID = Integer.parseInt(cusID);
		// 	if(n_cusID < 100 || n_cusID != 0)
		// 		throw new IllegalArgumentException("Invalid customer ID");
			
		} catch(IllegalArgumentException e)
			{
				System.out.println(":: ACCOUNT SUMMARY - ERROR - INVALID CUSTOMER ID");
				e.printStackTrace();
				return;
			}
			try{
				// Class.forName(driver);
				// con = DriverManager.getConnection(url, username, password);
				stmt = con.createStatement();
				// find the sum of all ACTIVE accounts from customer:
				String query = "SELECT number, balance FROM p1.account WHERE id = " + n_cusID + " AND status = 'A';";
				rs = stmt.executeQuery(query);
				System.out.println("NUMBER\t\tBALANCE");
				System.out.println("--------------------");
				while(rs.next()){
					System.out.println(rs.getInt(1) + "\t\t\t" + rs.getInt(2));
				}
				System.out.println("--------------------");
				query = "SELECT SUM(balance) TOTAL FROM p1.account WHERE id = " + n_cusID + ";";
				rs = stmt.executeQuery(query);
				if(rs.next()){
					System.out.println("TOTAL: \t\t" + rs.getInt(1));
				}
				
				// rs.close();
				// stmt.close();
				// con.close();
				
				System.out.println(":: ACCOUNT SUMMARY - SUCCESS");
			} catch(SQLException e){
				System.out.print("SQLException");
				System.out.println(":: ACCOUNT SUMMARY - FAILED");
				e.printStackTrace();
				return;
			}	catch(Exception e){
				System.out.println("Exception");
				e.printStackTrace();
				System.out.println(":: ACCOUNT SUMMARY - FAILED");
				return;
			}
	}

	/**
	 * Display Report A - Customer Information with Total Balance in Decreasing Order.
	 */
	public static void reportA() 
	{
		System.out.println(":: REPORT A - RUNNING");
		// Account Schema:
		// id, number, balance, status, type
		// Customer Schema:
		// id, name, gender, age, pin

		try{
			// Class.forName(driver);
			// con = DriverManager.getConnection(url, username, password);
			stmt = con.createStatement();

			// display each customer information and its total balance in decreasing order.
			String query = "SELECT p1.customer.id, p1.customer.name, p1.customer.age, p1.customer.gender, SUM(p1.account.balance) total FROM p1.account, p1.customer WHERE p1.account.id = p1.customer.id GROUP BY p1.customer.id, p1.customer.name, p1.customer.age, p1.customer.gender ORDER BY total DESC;";
			// String query = "SELECT p1.customer.id, p1.customer.name, p1.customer.age, p1.customer.age, (SELECT SUM(p1.account.balance) total FROM p1.account, p1.customer WHERE p1.account.id = p1.customer.id GROUP BY p1.customer.id ORDER BY total DESC) FROM p1.customer;";

			rs = stmt.executeQuery(query);
			// System.out.println("ID" + "\t" + "Name" +"\t" + "Age" + "\t" + "Gender" + "\t" + "Total Balance");
			System.out.printf("%s \t\t%-20s \t%s \t\t%s \t\t%s\n", "ID", "Name", "Age", "Gender", "Total Balance");
			System.out.println("-----------------------------------------------------------------");
			while(rs.next()){
				System.out.printf("%d \t%-20s \t%-10d %s \t\t\t%d\n", rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4), rs.getInt(5));
			}
			System.out.println("-----------------------------------------------------------------");
			// rs.close();
			// stmt.close();
			// con.close();
			System.out.println(":: REPORT A - SUCCESS");
		}
		catch (SQLException e){
			System.out.println(":: REPORT A - FAILED");
			e.printStackTrace();
			return;
		}
		catch (Exception e){
			System.out.println(":: REPORT A - FAILED");
			e.printStackTrace();
			return;
		}
	}

	/**
	 * Display Report B - Customer Information with Total Balance in Decreasing Order.
	 * @param min minimum age
	 * @param max maximum age
	 */
	public static void reportB(String min, String max) 
	{
		System.out.println(":: REPORT B - RUNNING");
		// Find average total balance across all accounts for customers between age groups max and min.
		try{
			if(min.isEmpty() || max.isEmpty())
			{
				throw new NullPointerException(":: REPORT B - ERROR - EMPTY INPUT");
			}
		} catch(NullPointerException e)
			{
				e.printStackTrace();
				return;
			}
		int n_min = 0;
		int n_max = 0;
		try{
			try{
				n_min = Integer.parseInt(min);
			} catch(NumberFormatException e){
				throw new NumberFormatException(":: REPORT B - ERROR - INVALID MINIMUM AGE");
			}
			try{
				n_max = Integer.parseInt(max);
			} catch(NumberFormatException e){
				throw new NumberFormatException(":: REPORT B - ERROR - INVALID MAXIMUM AGE");
			}
			if(n_min < 0 || n_max < 0)
				throw new IllegalArgumentException(":: REPORT B - ERROR - INVALID AGE");
			
		} catch(IllegalArgumentException e)
			{
				e.printStackTrace();
				return;
			}
			try{
				// Class.forName(driver);
				// con = DriverManager.getConnection(url, username, password);
				stmt = con.createStatement();
				// Find the Average Total Balance Between Age Groups Max and Min.
				String query = "SELECT AVG(total) FROM (SELECT SUM(p1.account.balance) total FROM p1.account, p1.customer WHERE p1.account.id = p1.customer.id AND p1.customer.age BETWEEN " + n_min + " AND " + n_max + " GROUP BY p1.customer.id ORDER BY total DESC);";

				rs = stmt.executeQuery(query);
				System.out.println("AVERAGE BALANCE");
				System.out.println("------------------------");
				while(rs.next()){
					System.out.println(rs.getInt(1));
				}
				
				System.out.println("------------------------");
				// rs.close();
				// stmt.close();
				// con.close();
				System.out.println(":: REPORT B - SUCCESS");
			}
			catch (SQLException e){
				System.out.println(":: REPORT B - FAILED");
				e.printStackTrace();
				return;
			}
			catch (Exception e){
				System.out.println(":: REPORT B - FAILED");
				e.printStackTrace();
				return;
			}
	}
}
