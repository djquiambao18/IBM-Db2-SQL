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
			con.close();
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
				n_age = Integer.parseInt(age); // check if age is an integer
				n_pin = Integer.parseInt(pin); // check if pin is an integer
				gender = gender.toUpperCase();
				ch_gender = gender.charAt(0);
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
		catch(IllegalArgumentException | NullPointerException e){
			System.out.println(":: CREATE NEW CUSTOMER - FAILED");
			System.out.println("Invalid input" + " " + e.getMessage());
			e.printStackTrace();
			return;
		}
		if(!name.isEmpty() && name.matches("[a-zA-Z ]+") && n_age >= 0 && n_pin >= 0 && (ch_gender == 'M' || ch_gender == 'F')) // check if name is alphabetic and age is between 0 and 120
		{
			if(name.length() <= 15){
				try {
					Class.forName(driver);
					con = DriverManager.getConnection(url, username, password);
					String query = "INSERT INTO P1.CUSTOMER (name, gender, age, pin) VALUES ('"
									+ name + "', '" + ch_gender + "', " + n_age + ", " + n_pin + ");";
					stmt = con.createStatement();
					stmt.executeUpdate(query);
					rs = stmt.getResultSet();
					if(rs != null){
						System.out.println(rs.getInt(1));
						rs.close();
					}
					stmt.close();
					query = "SELECT ID FROM P1.CUSTOMER";
					stmt = con.createStatement();
					rs = stmt.executeQuery(query);
					
					if(rs != null){
						while(rs.next())
							System.out.println(rs.getInt(1));
					}
					con.close();
					
					// System.out.println(rs.getInt(1));
					
					
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
					throw new NullPointerException("Invalid input - Empty");
				}
				try{
					n_id = Integer.parseInt(id);
					type = type.toUpperCase();
					ch_type = type.charAt(0);
					n_amount = Integer.parseInt(amount);
					if(ch_type != 'C' || ch_type != 'S' && n_amount < 0 && n_id < 100)
					{
						throw new IllegalArgumentException("Invalid input - Type");
					}
				}
				catch(IllegalArgumentException | NullPointerException e){
					System.out.println(":: OPEN ACCOUNT - FAILED");
					System.out.println("Invalid input");
					e.printStackTrace();
					return;
				}
				try {
					if(ch_type != 'C' || ch_type != 'S' && n_amount < 0 && n_id < 100){
						Class.forName(driver);
						con = DriverManager.getConnection(url, username, password);
						stmt = con.createStatement();
						String query = "INSERT INTO p1.account (id, type, balance, status) VALUES (" 
										+ n_id + ", '" + ch_type + "', " + n_amount + ", 'A');"; //A for active, I for Inactive
						if(stmt.execute(query)){
							rs = stmt.getResultSet();
							int rs_number = rs.getInt(1);
							System.out.println(rs_number);
							rs.close();
						}
						
						
						
						System.out.println(":: OPEN ACCOUNT - SUCCESS");
						
						stmt.close();
						con.close();
						
						return;
						
					}

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
						throw new NullPointerException("Invalid input - Empty");
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
						throw new IllegalArgumentException("Invalid input - Number");
					}
				} catch(IllegalArgumentException e)
					{
						e.printStackTrace();
						return;
					}
				try {
					Class.forName(driver);
					con = DriverManager.getConnection(url, username, password);
					stmt = con.createStatement();
					// Set Status to I for account number and set amount to 0
					String query = "UPDATE p1.account SET status = 'I', balance = 0 WHERE number = " + n_accNum + ";";
					stmt.execute(query);
					rs = stmt.getResultSet();
					rs.close();
					stmt.close();
					con.close();
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
						throw new NullPointerException("Invalid input - Empty");
					}
				} catch(NullPointerException e)
					{
						e.printStackTrace();
						return;
					}
				try{
					n_accNum = Integer.parseInt(accNum);
					n_amount = Integer.parseInt(amount);
					if(n_accNum < 1000 || n_amount < 0)
						throw new IllegalArgumentException("Invalid input - Number");
					
				} catch(IllegalArgumentException e)
					{
						e.printStackTrace();
						return;
					}
				try {
					Class.forName(driver);
					con = DriverManager.getConnection(url, username, password);
					stmt = con.createStatement();
					// Set balance to balance + amount
					String query = "UPDATE p1.account SET balance = balance + " + n_amount + " WHERE number = " + n_accNum + ";";
					stmt.execute(query);
					rs = stmt.getResultSet();
					rs.close();
					stmt.close();
					con.close();
					System.out.println(":: DEPOSIT - SUCCESS");
				} catch(SQLException e){
					System.out.print("SQLException");
					System.out.println(":: DEPOSIT - FAILED");
					e.printStackTrace();
					return;
				}
				catch(ClassNotFoundException cnfe){
					System.out.println("ClassNotFoundException");
					cnfe.printStackTrace();
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
						throw new NullPointerException("Invalid input - Empty");
					}
				} catch(NullPointerException e)
					{
						e.printStackTrace();
						return;
					}
				try{
					n_accNum = Integer.parseInt(accNum);
					n_amount = Integer.parseInt(amount);
					if(n_accNum < 1000 || n_amount < 0)
						throw new IllegalArgumentException("Invalid input - Number");
					
				} catch(IllegalArgumentException e)
					{
						e.printStackTrace();
						return;
					}
				try {
					Class.forName(driver);
					con = DriverManager.getConnection(url, username, password);
					stmt = con.createStatement();
					// Set balance to balance - amount
					String query = "UPDATE p1.account SET balance = balance - " + n_amount + " WHERE number = " + n_accNum + ";";
					stmt.execute(query);
					rs = stmt.getResultSet();
					rs.close();
					stmt.close();
					con.close();
					System.out.println(":: WITHDRAW - SUCCESS");
				} catch(SQLException e){
					System.out.print("SQLException");
					System.out.println(":: WITHDRAW - FAILED");
					e.printStackTrace();
					return;
				}	catch(ClassNotFoundException cnfe){
					System.out.println("ClassNotFoundException");
					cnfe.printStackTrace();
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
						throw new NullPointerException("Invalid input - Empty");
					}
				} catch(NullPointerException e)
					{
						e.printStackTrace();
						return;
					}
				try{
					n_srcAccNum = Integer.parseInt(srcAccNum);
					n_destAccNum = Integer.parseInt(destAccNum);
					n_amount = Integer.parseInt(amount);
					if(n_srcAccNum < 1000 || n_destAccNum < 1000 || n_amount < 0)
						throw new IllegalArgumentException("Invalid input - Number");
					
				} catch(IllegalArgumentException e)
					{
						e.printStackTrace();
						return;
					}
				try {
					Class.forName(driver);
					con = DriverManager.getConnection(url, username, password);
					stmt = con.createStatement();
					// Set balance to balance - amount
					String query = "UPDATE p1.account SET balance = balance - " + n_amount + " WHERE number = " + n_srcAccNum + ";";
					stmt.execute(query);
					rs = stmt.getResultSet();
					// Set balance to balance + amount
					query = "UPDATE p1.account SET balance = balance + " + n_amount + " WHERE number = " + n_destAccNum + ";";
					stmt.execute(query);
					rs = stmt.getResultSet();
					rs.close();
					stmt.close();
					con.close();
				System.out.println(":: TRANSFER - SUCCESS");
				} catch(SQLException e){
					System.out.print("SQLException");
					System.out.println(":: TRANSFER - FAILED");
					e.printStackTrace();
					return;
				}	catch(ClassNotFoundException cnfe){
					System.out.println("ClassNotFoundException");
					cnfe.printStackTrace();
					System.out.println(":: TRANSFER - FAILED");
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
				throw new NullPointerException("Invalid input - Empty");
			}
		} catch(NullPointerException e)
			{
				e.printStackTrace();
				return;
			}
		try{
			n_cusID = Integer.parseInt(cusID);
			if(n_cusID < 1000)
				throw new IllegalArgumentException("Invalid customer ID");
			
		} catch(IllegalArgumentException e)
			{
				e.printStackTrace();
				return;
			}
			try{
				Class.forName(driver);
				con = DriverManager.getConnection(url, username, password);
				stmt = con.createStatement();
				String query = "SELECT number, balance FROM p1.account WHERE customer_id = " + n_cusID + ";";
				stmt.execute(query);
				rs = stmt.getResultSet();
				int totalBalance = 0;
				System.out.println("Account Number\tBalance");
				while(rs.next())
				{
					System.out.println(rs.getInt(1) + "\t\t" + rs.getInt(2));
					totalBalance += rs.getInt(2);
				}
				rs.close();
				stmt.close();
				con.close();
				System.out.println("Total Balance\t" + totalBalance);
				System.out.println(":: ACCOUNT SUMMARY - SUCCESS");
			} catch(SQLException e){
				System.out.print("SQLException");
				System.out.println(":: ACCOUNT SUMMARY - FAILED");
				e.printStackTrace();
				return;
			}	catch(ClassNotFoundException cnfe){
				System.out.println("ClassNotFoundException");
				cnfe.printStackTrace();
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
		// display customer information and its total balance in decreasing order.
		try{
			Class.forName(driver);
			con = DriverManager.getConnection(url, username, password);
			stmt = con.createStatement();
			String query = "SELECT C.id, C.name, C.age, C.gender, SUM(A.balance) AS total FROM CUSTOMER C LEFT JOIN ACCOUNT A ON C.id=A.id ORDER BY total DESC";
			stmt.execute(query);
			rs = stmt.getResultSet();
			System.out.println("ID" + "\t" + "Name" +"\t" + "Age" + "\t" + "Gender" + "\t" + "Total Balance");
			while(rs.next()){
				System.out.println(rs.getInt(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3) + "\t" + rs.getString(4) + "\t" + rs.getInt(5));
			}
			rs.close();
			stmt.close();
			con.close();
			System.out.println(":: REPORT A - SUCCESS");
		}
		catch (SQLException e){
			System.out.println(":: REPORT A - FAILED");
			e.printStackTrace();
			return;
		}
		catch (ClassNotFoundException cnfe){
			System.out.println(":: REPORT A - FAILED");
			cnfe.printStackTrace();
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
				throw new NullPointerException("Invalid input - Empty");
			}
		} catch(NullPointerException e)
			{
				e.printStackTrace();
				return;
			}
		int n_min = 0;
		int n_max = 0;
		try{
			n_min = Integer.parseInt(min);
			n_max = Integer.parseInt(max);
			if(n_min < 0 || n_max < 0)
				throw new IllegalArgumentException("Invalid age");
			
		} catch(IllegalArgumentException e)
			{
				e.printStackTrace();
				return;
			}
			try{
				Class.forName(driver);
				con = DriverManager.getConnection(url, username, password);
				stmt = con.createStatement();
				String query = "SELECT C.age, AVG(A.balance) AS avg_balance FROM CUSTOMER C LEFT JOIN ACCOUNT A ON C.id=A.id WHERE C.age BETWEEN " + n_min + " AND " + n_max + " GROUP BY C.age ORDER BY avg_balance DESC;";
				stmt.execute(query);
				rs = stmt.getResultSet();
				System.out.println("Age" + "\t" + "Average Balance");
				while(rs.next()){
					System.out.println(rs.getInt(1) + "\t" + rs.getInt(2));
				}
				rs.close();
				stmt.close();
				con.close();
				System.out.println(":: REPORT B - SUCCESS");
			}
			catch (SQLException e){
				System.out.println(":: REPORT B - FAILED");
				e.printStackTrace();
				return;
			}
			catch (ClassNotFoundException cnfe){
				System.out.println(":: REPORT B - FAILED");
				cnfe.printStackTrace();
				return;
			}
	}
}
