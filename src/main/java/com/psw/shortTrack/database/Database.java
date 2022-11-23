package com.psw.shortTrack.database;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class Database {
	private static ComboPooledDataSource dataSource;
	private static final String driver = "org.postgresql.Driver";
	private static final String url = "jdbc:postgresql://db.fe.up.pt:5432/pswa0502";
	private static final String user = "pswa0502";
	private static final String password = "jKWlEeAs";
	
	// Initial setup for the database
	static {
		// Cria uma forma de manter a conecção persistente
		try {
			config();
			setup();
		} catch (PropertyVetoException pve) {
			pve.printStackTrace();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}
	
	/**
	 * Attempts to establish a connection with the database.
	 *
	 * @return a connection to the data source
	 * @throws SQLException If a database access error occurs
	 */
	protected static Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
	
	/**
	 * Configures the database access pool
	 * 
	 * @throws PropertyVetoException If the specified driver is not valid (unreachable)
	 */
	private static void config() throws PropertyVetoException {
		dataSource = new ComboPooledDataSource();
		dataSource.setDriverClass(driver);
		dataSource.setJdbcUrl(url);
		dataSource.setUser(user);
		dataSource.setPassword(password);
		
		// Define o numero máximo de conexões
		dataSource.setMaxPoolSize(30);
		// Timeout de checkout (getConnection)
		dataSource.setCheckoutTimeout(3000);
		// Define o numero máximo de tentativas de checkout
		dataSource.setAcquireRetryAttempts(5);
		// Define o delay entre cada tentativa de checkout
		dataSource.setAcquireRetryDelay(1000);
		// Define um teste obrigatório da conexão ao chamar getConnection
		dataSource.setTestConnectionOnCheckout(true);
		// Segundos que as conexões não usadas para além do minimo (3 conexões) duram
		dataSource.setMaxIdleTimeExcessConnections(180);
		// Define a query a executar nos testes (query mais rapida)
		dataSource.setPreferredTestQuery("SELECT 1");
	}
	
	/**
	 * Executes a query to the database and returns the first column of the response as a string
	 * 
	 * @param query SQL code to execute
	 * @return String - First column of the response
	 * 
	 * @throws SQLException If a database access error occurs 
	 */
	protected static String executeQueryReturnSingleColumn(String query) throws SQLException{
		
		try (Connection connection = getConnection()){
			if (connection != null) {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				if (rs.next()) {
					return rs.getString(1);
				}
			} else {
				throw new SQLException("Connection failed!");
			}
		}
		
		return null;
	}
	
	protected static boolean executeQueryReturnBoolean(String query) throws SQLException {
		
		try (Connection connection = getConnection()){
			if (connection != null) {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				if (rs.next()) {
					return rs.getBoolean(1);
				}
			} else {
				throw new SQLException("Connection failed!");
			}
		}
		
		return false;
	}
	
	/**
	 * Executes a update query to the database
	 * 
	 * @param query String with the sql code
	 * @return either (1) the row count for SQL Data Manipulation Language (DML) statementsor 
	 * (2) 0 for SQL statements that return nothing
	 * 
	 * @throws SQLException If a database access error occurs
	 */
	protected static int executeUpdate(String query) throws SQLException{
		
		try (Connection connection = getConnection()){
			if (connection != null) {
				Statement stmt = connection.createStatement();
				return stmt.executeUpdate(query);
			} else {
				throw new SQLException("Connection failed!");
			}
		}
	}
	
	/**
	 * Executes the sql code to do the initial setup of the database.
	 * Creates the needed tables, columns, sequences, primary keys, etc.
	 * 
	 * @throws SQLException If a database access error occurs
	 */
	private static void setup() throws SQLException {
		String query =    "CREATE SCHEMA IF NOT EXISTS projeto;\n"
				
						+ "CREATE TABLE IF NOT EXISTS projeto.account ();\r\n"						
						+ "ALTER TABLE projeto.account ADD COLUMN IF NOT EXISTS email character varying(64) NOT NULL;\r\n"
						+ "ALTER TABLE projeto.account ADD COLUMN IF NOT EXISTS password character varying(32) NOT NULL;\n"
						+ "ALTER TABLE projeto.account ADD COLUMN IF NOT EXISTS name character varying(64);\r\n"
						+ "DO $$\r\n"
						+ "BEGIN\r\n"
						+ "	IF (SELECT NOT EXISTS(SELECT 1 FROM pg_catalog.pg_constraint WHERE conname='account_pkey')) THEN\r\n"
						+ "		ALTER TABLE ONLY projeto.account ADD CONSTRAINT account_pkey PRIMARY KEY (email);\r\n"
						+ "	END IF;\r\n"
						+ "END $$;\r\n"
						
						+ "CREATE TABLE IF NOT EXISTS projeto.personal_tasks ();\r\n"
						+ "ALTER TABLE projeto.personal_tasks ADD COLUMN IF NOT EXISTS id integer NOT NULL;\r\n"
						+ "ALTER TABLE projeto.personal_tasks ADD COLUMN IF NOT EXISTS list_id integer NOT NULL;"
						+ "DO $$\r\n"
						+ "BEGIN\r\n"
						+ "	IF (SELECT NOT EXISTS(SELECT 1 FROM pg_catalog.pg_constraint WHERE conname='personal_tasks_list_id_fkey')) THEN\r\n"
						+ "		ALTER TABLE ONLY projeto.personal_tasks ADD CONSTRAINT personal_tasks_list_id_fkey FOREIGN KEY (list_id) REFERENCES projeto.personal_lists (id) ON DELETE CASCADE;\r\n"
						+ "	END IF;\r\n"
						+ "END $$;\r\n"
						+ "ALTER TABLE projeto.personal_tasks ADD COLUMN IF NOT EXISTS name character varying(32) NOT NULL;\r\n"
						+ "ALTER TABLE projeto.personal_tasks ADD COLUMN IF NOT EXISTS description character varying(128);\r\n"
						+ "ALTER TABLE projeto.personal_tasks ADD COLUMN IF NOT EXISTS created_date date;\r\n"
						+ "ALTER TABLE projeto.personal_tasks ADD COLUMN IF NOT EXISTS deadline_date date;\r\n"
						+ "ALTER TABLE projeto.personal_tasks ADD COLUMN IF NOT EXISTS state boolean NOT NULL;\r\n"
						+ "CREATE SEQUENCE IF NOT EXISTS projeto.tasks_id_seq\r\n"
						+ "	AS integer\r\n"
						+ "	START WITH 1\r\n"
						+ "	INCREMENT BY 1\r\n"
						+ "	NO MINVALUE\r\n"
						+ "	NO MAXVALUE\r\n"
						+ "	CACHE 1;\r\n"
						+ "ALTER SEQUENCE projeto.tasks_id_seq OWNED BY projeto.personal_tasks.id;\r\n"
						+ "ALTER TABLE ONLY projeto.personal_tasks ALTER COLUMN id SET DEFAULT nextval('projeto.tasks_id_seq'::regclass);\r\n"
						+ "DO $$\r\n"
						+ "BEGIN\r\n"
						+ "	IF (SELECT NOT EXISTS(SELECT 1 FROM pg_catalog.pg_constraint WHERE conname='tasks_pkey')) THEN\r\n"
						+ "		ALTER TABLE ONLY projeto.personal_tasks ADD CONSTRAINT tasks_pkey PRIMARY KEY (id);\r\n"
						+ "	END IF;\r\n"
						+ "END $$;\r\n"
						
						+ "CREATE TABLE IF NOT EXISTS projeto.personal_lists ();\r\n"
						+ "ALTER TABLE projeto.personal_lists ADD COLUMN IF NOT EXISTS id integer NOT NULL;\r\n"
						+ "ALTER TABLE projeto.personal_lists ADD COLUMN IF NOT EXISTS email character varying (64) NOT NULL;\r\n"
						+ "DO $$\r\n"
						+ "BEGIN\r\n"
						+ "	IF (SELECT NOT EXISTS(SELECT 1 FROM pg_catalog.pg_constraint WHERE conname='personal_lists_email_fkey')) THEN\r\n"
						+ "		ALTER TABLE ONLY projeto.personal_lists ADD CONSTRAINT personal_lists_email_fkey FOREIGN KEY (email) REFERENCES projeto.accpint (email) ON DELETE CASCADE;\r\n"
						+ "	END IF;\r\n"
						+ "END $$;\r\n"
						+ "ALTER TABLE projeto.personal_lists ADD COLUMN IF NOT EXISTS name character varying (32) NOT NULL;\r\n"
						+ "CREATE SEQUENCE IF NOT EXISTS projeto.lists_id_seq\r\n"
						+ "	AS integer\r\n"
						+ "	START WITH 1\r\n"
						+ "	INCREMENT BY 1\r\n"
						+ "	NO MINVALUE\r\n"
						+ "	NO MAXVALUE\r\n"
						+ "	CACHE 1;\r\n"
						+ "ALTER SEQUENCE projeto.lists_id_seq OWNED BY projeto.personal_lists.id;\r\n"
						+ "ALTER TABLE ONLY projeto.personal_lists ALTER COLUMN id SET DEFAULT nextval('projeto.lists_id_seq'::regclass);\r\n"
						+ "DO $$\r\n"
						+ "BEGIN\r\n"
						+ "	IF (SELECT NOT EXISTS(SELECT 1 FROM pg_catalog.pg_constraint WHERE conname='lists_pkey')) THEN\r\n"
						+ "		ALTER TABLE ONLY projeto.personal_lists ADD CONSTRAINT lists_pkey PRIMARY KEY (id);\r\n"
						+ "	END IF;\r\n"
						+ "END $$;\r\n"
						
						+ "CREATE TABLE IF NOT EXISTS projeto.groups ();\r\n"
						+ "ALTER TABLE projeto.groups ADD COLUMN IF NOT EXISTS id integer NOT NULL;\r\n"
						+ "ALTER TABLE projeto.groups ADD COLUMN IF NOT EXISTS manager character varying (64) NOT NULL;\r\n"
						+ "DO $$\r\n"
						+ "BEGIN\r\n"
						+ "	IF (SELECT NOT EXISTS(SELECT 1 FROM pg_catalog.pg_constraint WHERE conname='groups_manager_fkey')) THEN\r\n"
						+ "		ALTER TABLE ONLY projeto.groups ADD CONSTRAINT groups_manager_fkey FOREIGN KEY (manager) REFERENCES projeto.account (email) ON DELETE CASCADE;\r\n"
						+ "	END IF;\r\n"
						+ "END $$;\r\n"
						+ "ALTER TABLE projeto.groups ADD COLUMN IF NOT EXISTS name character varying (32) NOT NULL;\r\n"
						+ "CREATE SEQUENCE IF NOT EXISTS projeto.groups_id_seq\r\n"
						+ "	AS integer\r\n"
						+ "	START WITH 1\r\n"
						+ "	INCREMENT BY 1\r\n"
						+ "	NO MINVALUE\r\n"
						+ "	NO MAXVALUE\r\n"
						+ "	CACHE 1;\r\n"
						+ "ALTER SEQUENCE projeto.groups_id_seq OWNED BY projeto.groups.id;\r\n"
						+ "ALTER TABLE ONLY projeto.groups ALTER COLUMN id SET DEFAULT nextval('projeto.groups_id_seq'::regclass);\r\n"
						+ "DO $$\r\n"
						+ "BEGIN\r\n"
						+ "	IF (SELECT NOT EXISTS(SELECT 1 FROM pg_catalog.pg_constraint WHERE conname='groups_pkey')) THEN\r\n"
						+ "		ALTER TABLE ONLY projeto.groups ADD CONSTRAINT groups_pkey PRIMARY KEY (id);\r\n"
						+ "	END IF;\r\n"
						+ "END $$;\r\n"
						
						+ "CREATE TABLE IF NOT EXISTS projeto.group_tasks ();\r\n"
						+ "ALTER TABLE projeto.group_tasks ADD COLUMN IF NOT EXISTS id integer NOT NULL;\r\n"
						+ "ALTER TABLE projeto.group_tasks ADD COLUMN IF NOT EXISTS group_id integer NOT NULL;\r\n"
						+ "DO $$\r\n"
						+ "BEGIN\r\n"
						+ "	IF (SELECT NOT EXISTS(SELECT 1 FROM pg_catalog.pg_constraint WHERE conname='group_tasks_group_id_fkey')) THEN\r\n"
						+ "		ALTER TABLE ONLY projeto.group_tasks ADD CONSTRAINT group_tasks_group_id_fkey FOREIGN KEY (group_id) REFERENCES projeto.groups (id) ON DELETE CASCADE;\r\n"
						+ "	END IF;\r\n"
						+ "END $$;\r\n"
						+ "ALTER TABLE projeto.group_tasks ADD COLUMN IF NOT EXISTS assigned_to character varying (64);\r\n"
						+ "DO $$\r\n"
						+ "BEGIN\r\n"
						+ "	IF (SELECT NOT EXISTS(SELECT 1 FROM pg_catalog.pg_constraint WHERE conname='group_tasks_assigned_to_fkey')) THEN\r\n"
						+ "		ALTER TABLE ONLY projeto.group_tasks ADD CONSTRAINT group_tasks_assigned_to_fkey FOREIGN KEY (assigned_to) REFERENCES projeto.account (email) ON DELETE SET NULL;\r\n"
						+ "	END IF;\r\n"
						+ "END $$;\r\n"
						+ "ALTER TABLE projeto.group_tasks ADD COLUMN IF NOT EXISTS name character varying(32) NOT NULL;\r\n"
						+ "ALTER TABLE projeto.group_tasks ADD COLUMN IF NOT EXISTS description character varying(128);\r\n"
						+ "ALTER TABLE projeto.group_tasks ADD COLUMN IF NOT EXISTS created_date date;\r\n"
						+ "ALTER TABLE projeto.group_tasks ADD COLUMN IF NOT EXISTS deadline_date date;\r\n"
						+ "ALTER TABLE projeto.group_tasks ADD COLUMN IF NOT EXISTS state boolean NOT NULL;\r\n"
						+ "ALTER SEQUENCE projeto.tasks_id_seq OWNED BY projeto.group_tasks.id;\r\n"
						+ "ALTER TABLE ONLY projeto.group_tasks ALTER COLUMN id SET DEFAULT nextval('projeto.tasks_id_seq'::regclass);\r\n"
						+ "DO $$\r\n"
						+ "BEGIN\r\n"
						+ "	IF (SELECT NOT EXISTS(SELECT 1 FROM pg_catalog.pg_constraint WHERE conname='group_tasks_pkey')) THEN\r\n"
						+ "		ALTER TABLE ONLY projeto.group_tasks ADD CONSTRAINT group_tasks_pkey PRIMARY KEY (id);\r\n"
						+ "	END IF;\r\n"
						+ "END $$;";
		
		executeUpdate(query);
	}
	
	/**
	 * Parses a string to SQL String
	 * 
	 * @param str - String to parse
	 * @return SQL String
	 */
	protected static String toSQL(String str) {
		if (str != null) {
			str.replace("'", "\'");
			str = "'" + str + "'";
		}
			
		return str;
	}
	
	/**
	 * Parses an integer to SQL String
	 * 
	 * @param i - Integer to parse
	 * @return SQL String
	 */
	protected static String toSQL(int i) {
		return "'" + i + "'";
	}
	
	/**
	 * Parses a localDate to SQL String
	 * 
	 * @param date - LocalDate to parse
	 * @return SQL String
	 */
	protected static String toSQL(LocalDate date) {
		String str = null;
		if (date != null) {
			str = "'" + date.toString() + "'";
		}
		return str;
	}
	
	/**
	 * Parses a boolean to SQL String
	 * 
	 * @param bool - boolean to parse
	 * @return SQL String
	 */
	protected static String toSQL(boolean bool) {
		return "'" + bool + "'";
	}
	
	/**
	 * Parses an ArrayList(String) to SQL Array
	 * 
	 * @param array - ArrayList to parse
	 * @return SQL Array
	 */
	protected static String toSQL(ArrayList<String> array) {
		String str = "'{";
		
		for (int i = 0; i < array.size(); i++) {
			if (i == array.size() - 1) {
				str += "\"" + array.get(i) + "\"";
			} else {
			str += "\"" + array.get(i) + "\",";
			}
		}
		
		return str + "}'";
	}
}

