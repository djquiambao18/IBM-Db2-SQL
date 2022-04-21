import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLDataException;
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
			if(gender.length() == 1 && !age.isEmpty() && !pin.isEmpty())
			name = name.trim(); // clean out both ends of string of whitespaces
			n_age = Integer.parseInt(age); // check if age is an integer
			n_pin = Integer.parseInt(pin); // check if pin is an integer
			ch_gender = gender.charAt(0);
		}
		catch(Exception e){
			System.out.println(":: CREATE NEW CUSTOMER - FAILED");
			System.out.println("Invalid input");
			e.printStackTrace();
			return;
		}
		if(!name.isEmpty() && name.matches("[a-zA-Z\s]+") && n_age > 0 && n_age < 150) // check if name is alphabetic and age is between 0 and 120
		{
			try {
				Class.forName(driver);
				con = DriverManager.getConnection(url, username, password);
				stmt = con.createStatement();
				String query = "INSERT INTO customer (name, gender, age, pin) VALUES (" 
								+ name + ", " + ch_gender + ", " + n_age + ", " + n_pin + ");";
				rs = stmt.executeQuery(query);
				String rs_name = rs.getString(1);
				String rs_gender = rs.getString(2);
				String rs_age = rs.getString(3);
				String rs_pin = rs.getString(4);
				System.out.println(":: CREATE NEW CUSTOMER - SUCCESS");
				rs.close();
				stmt.close();
				con.close();
			} catch(SQLDataException e){
				System.out.println(":: CREATE NEW CUSTOMER - FAILED");
				e.printStackTrace();
			}
			catch (Exception e) {
				System.out.println(":: CREATE NEW CUSTOMER - FAILED");
				e.printStackTrace();
			}
			}
			
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
				/* insert your code here */
		System.out.println(":: OPEN ACCOUNT - SUCCESS");
	}

	/**
	 * Close an account.
	 * @param accNum account number
	 */
	public static void closeAccount(String accNum) 
	{
		System.out.println(":: CLOSE ACCOUNT - RUNNING");
				/* insert your code here */
		System.out.println(":: CLOSE ACCOUNT - SUCCESS");
	}

	/**
	 * Deposit into an account.
	 * @param accNum account number
	 * @param amount deposit amount
	 */
	public static void deposit(String accNum, String amount) 
	{
		System.out.println(":: DEPOSIT - RUNNING");
				/* insert your code here */
		System.out.println(":: OPEN ACCOUNT - SUCCESS");
	}

	/**
	 * Withdraw from an account.
	 * @param accNum account number
	 * @param amount withdraw amount
	 */
	public static void withdraw(String accNum, String amount) 
	{
		System.out.println(":: WITHDRAW - RUNNING");
				/* insert your code here */
		System.out.println(":: WITHDRAW - SUCCESS");
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
				/* insert your code here */	
		System.out.println(":: TRANSFER - SUCCESS");
	}

	/**
	 * Display account summary.
	 * @param cusID customer ID
	 */
	public static void accountSummary(String cusID) 
	{
		System.out.println(":: ACCOUNT SUMMARY - RUNNING");
				/* insert your code here */		
		System.out.println(":: ACCOUNT SUMMARY - SUCCESS");
	}

	/**
	 * Display Report A - Customer Information with Total Balance in Decreasing Order.
	 */
	public static void reportA() 
	{
		System.out.println(":: REPORT A - RUNNING");
				/* insert your code here */	
		System.out.println(":: REPORT A - SUCCESS");
	}

	/**
	 * Display Report B - Customer Information with Total Balance in Decreasing Order.
	 * @param min minimum age
	 * @param max maximum age
	 */
	public static void reportB(String min, String max) 
	{
		System.out.println(":: REPORT B - RUNNING");
				/* insert your code here */		
		System.out.println(":: REPORT B - SUCCESS");
	}
}
