package db;

import java.sql.*;
import com.mchange.v2.c3p0.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DB_account {
	private static ComboPooledDataSource dataSource = null;
	
	// Initial setup for the database
	static {
		
		// Cria uma forma de manter a conecção persistente
		dataSource = new ComboPooledDataSource();
		try {
			dataSource.setDriverClass("org.postgresql.Driver");
		} catch (Exception e){
			System.out.println(e);
		}
		dataSource.setJdbcUrl("jdbc:postgresql://db.fe.up.pt:5432/pswa0502");
		dataSource.setUser("pswa0502");
		dataSource.setPassword("jKWlEeAs");
		
		// Inicializa a tabela se não existir - não utilizado na prática, mas é uma boa prática
		String query =    "CREATE SCHEMA IF NOT EXISTS projeto;" 
						+ "ALTER SCHEMA projeto OWNER TO pswa0502;" 
						+ "CREATE TABLE IF NOT EXISTS projeto.account (\r\n"
						+ "    username character varying(32) NOT NULL,\r\n"
						+ "    email character varying(64) NOT NULL,\r\n"
						+ "    password character varying(32) NOT NULL,\r\n"
						+ "    register_date date,\r\n"
						+ "	   first_name character varying (32),\r\n"
						+ "	   last_name character varying (32)\r\n"
						+ ");" 
						+ "ALTER TABLE projeto.account OWNER TO pswa0502;";
		
		executeUpdate(query);	
	}
	
	public DB_account() {
		// Not needed
	}
	
	
	public static boolean login (String user, String password) {
		String query;
		
		/* Como não sabemos à partida se o utilizador está a logar com o username ou com o email,
		   verificamos qual é através da existencia de @ */
		if (user.contains("@")) {
			query = "SELECT EXISTS (SELECT 1 FROM projeto.account WHERE email='"+user+"' AND password='"+password+"');";
		} else {
			query = "SELECT EXISTS (SELECT 1 FROM projeto.account WHERE username='"+user+"' AND password='"+password+"');";
		}
		
		String rs = executeQuery_SingleColumn(query);
		return (rs.equals("t"));
	}
	
	/*
	 * return  1 = Conta criada com sucesso
	 * return -1 = Conta não criada devido a erros de conecção
	 * return -2 = Conta não criada porque já existe uma conta com o mesmo email
	 * return -3 = Conta não criada porque já existe uma conta com o mesmo username
	 */
	public static int createAccount(String username, String password, String email, String first_name, String last_name) {
		Date registerDate = new Date();
		SimpleDateFormat DateFor = new SimpleDateFormat("yyyy-MM-dd");
		String stringDate= DateFor.format(registerDate);
		
		String query = "INSERT INTO projeto.account (username, password, email, register_date, first_name, last_name)" +
						"VALUES ('" + username + "', '" + password + "', '" + email +
						"', '" + stringDate + "', '" + first_name + "', '" + last_name + "');";
		// Não permite que sejam criadas novas contas com o mesmo username ou mail, ou seja, username unico e mail unico
		String checkUsername =    "SELECT EXISTS(SELECT 1 FROM projeto.account "
				 				+ "WHERE username = '" + username + "');";
		String checkEmail =   "SELECT EXISTS(SELECT 1 FROM projeto.account "
 							+ "WHERE email = '" + email + "');";
		
		String rs = executeQuery_SingleColumn(checkUsername);
		if (rs != null) {
			if (rs.equals("f")) {
				rs = executeQuery_SingleColumn(checkEmail);
				if (rs != null) {
					if (rs.equals("f")) {
						return executeUpdate(query);
					} else {
						System.out.println("Já existe uma conta com este email");
						return -2;
					}
				}
			} else {
				System.out.println("Já existe uma conta com este username");
				return -3;
			}
		}
		
		return -1;
	}
	
	public static boolean deleteAccount(String username) {
		String query = "DELETE FROM projeto.account WHERE username='" + username + "'";
		
		if (executeUpdate(query) > 0)
			return true;
		else
			return false;
	}
	
	public static String getFirstName(String username) {
		String query = 	  "SELECT first_name FROM projeto.account "
						+ "WHERE username='" + username + "';";
		
		return executeQuery_SingleColumn(query);
	}
	
	public static String getLastName(String username, String password) {
		String query = 	  "SELECT last_name FROM projeto.account "
						+ "WHERE username='" + username + "' AND password='" + password + "';";
		
		return executeQuery_SingleColumn(query);
	}
	
	public static String getUsername(String email, String password) {
		String query = 	  "SELECT username FROM projeto.account "
						+ "WHERE email='" + email + "' AND password='" + password + "';";
			
		return executeQuery_SingleColumn(query);
	}
	
	public static Date getRegisterDate(String username, String password) {
		String query = 	  "SELECT username FROM projeto.account "
						+ "WHERE username='" + username + "' AND password='" + password + "';";
		
		String dateString = executeQuery_SingleColumn(query);
		Date register_date = null;
		
		if (dateString != null) {
			try {
				register_date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return register_date;
	}
	
	public static boolean changeUserName(String old_username, String new_username) {
		String query = "UPDATE projeto.account " +
						"SET username='" + new_username +
						"'WHERE username='"+ old_username + "'";
		
		if (executeUpdate(query) > 0)
			return true;
		else
			return false;
	}
	
	public static boolean changePassword(String username, String new_password) {
		String query = "UPDATE projeto.account " +
						"SET password='" + new_password +
						"'WHERE username='"+ username + "'";
		
		if (executeUpdate(query) > 0)
			return true;
		else
			return false;
	}
	
	private static String executeQuery_SingleColumn(String query) {
		
		try (Connection connection = dataSource.getConnection()){
			if (connection != null) {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				if (rs.next()) {
					return rs.getString(1);
				}
			} else {
				System.out.println("Connection failed");
			}
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
		return null;
	}
	
	private static int executeUpdate(String query) {
		Connection connection = null;
		
		try {
			//Class.forName("org.postgresql.Driver");
			//connection = DriverManager.getConnection("jdbc:postgresql://db.fe.up.pt:5432/pswa0502","pswa0502","jKWlEeAs");
			connection = dataSource.getConnection();
			if (connection != null) {
				Statement stmt = connection.createStatement();
				int i = stmt.executeUpdate(query);
				return i;
			} else {
				System.out.println("Connection failed");
			}
			
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			if (connection != null) try { connection.close(); } catch (SQLException ignore) {}
		} 
		
		return -1;
	}
}
