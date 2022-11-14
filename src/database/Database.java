package database;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class Database {
	protected static ComboPooledDataSource dataSource = null;
	
	// Initial setup for the database
	static {
		
		// Cria uma forma de manter a conecção persistente
		dataSource = new ComboPooledDataSource();
		try {
			dataSource.setDriverClass("org.postgresql.Driver");
			dataSource.setJdbcUrl("jdbc:postgresql://db.fe.up.pt:5432/pswa0502");
			dataSource.setUser("pswa0502");
			dataSource.setPassword("jKWlEeAs");
			dataSource.setCheckoutTimeout(5000);
			System.out.println(dataSource.getCheckoutTimeout());
		} catch (PropertyVetoException e){
			System.out.println(e);
		}
		
		// Inicializa a tabela se não existir - não utilizado, mas é uma boa prática		
		String query =    "CREATE SCHEMA IF NOT EXISTS projeto;"
						+ "CREATE TABLE IF NOT EXISTS projeto.account ();"
						+ "ALTER TABLE projeto.account ADD COLUMN IF NOT EXISTS username character varying(32) NOT NULL;"
						+ "ALTER TABLE projeto.account ADD COLUMN IF NOT EXISTS email character varying(64) NOT NULL;"
						+ "ALTER TABLE projeto.account ADD COLUMN IF NOT EXISTS password character varying(32) NOT NULL;"
						+ "ALTER TABLE projeto.account ADD COLUMN IF NOT EXISTS register_date date;"
						+ "ALTER TABLE projeto.account ADD COLUMN IF NOT EXISTS first_name character varying(32);"
						+ "ALTER TABLE projeto.account ADD COLUMN IF NOT EXISTS last_name character varying(32);"
						+ "ALTER TABLE ONLY projeto.account DROP CONSTRAINT IF EXISTS account_pkey;"
						+ "ALTER TABLE ONLY projeto.account ADD CONSTRAINT account_pkey PRIMARY KEY (username, email);"
						+ "CREATE TABLE IF NOT EXISTS projeto.personal_tasks ();"
						+ "ALTER TABLE projeto.personal_tasks ADD COLUMN IF NOT EXISTS id integer NOT NULL;"
						+ "ALTER TABLE projeto.personal_tasks ADD COLUMN IF NOT EXISTS list_id integer NOT NULL;"
						+ "ALTER TABLE projeto.personal_tasks ADD COLUMN IF NOT EXISTS email character varying(64) NOT NULL;"
						+ "ALTER TABLE projeto.personal_tasks ADD COLUMN IF NOT EXISTS name character varying(32) NOT NULL;"
						+ "ALTER TABLE projeto.personal_tasks ADD COLUMN IF NOT EXISTS description character varying(128);"
						+ "ALTER TABLE projeto.personal_tasks ADD COLUMN IF NOT EXISTS created_date date;"
						+ "ALTER TABLE projeto.personal_tasks ADD COLUMN IF NOT EXISTS deadline_date date;"
						+ "ALTER TABLE projeto.personal_tasks ADD COLUMN IF NOT EXISTS state boolean NOT NULL;"
						+ "CREATE SEQUENCE IF NOT EXISTS projeto.tasks_id_seq\r\n"
						+ "    AS integer\r\n"
						+ "    START WITH 1\r\n"
						+ "    INCREMENT BY 1\r\n"
						+ "    NO MINVALUE\r\n"
						+ "    NO MAXVALUE\r\n"
						+ "    CACHE 1;"
						+ "ALTER SEQUENCE projeto.tasks_id_seq OWNED BY projeto.personal_tasks.id;"
						+ "ALTER TABLE ONLY projeto.personal_tasks ALTER COLUMN id SET DEFAULT nextval('projeto.tasks_id_seq'::regclass);"
						+ "ALTER TABLE ONLY projeto.personal_tasks DROP CONSTRAINT IF EXISTS tasks_pkey;"
						+ "ALTER TABLE ONLY projeto.personal_tasks ADD CONSTRAINT tasks_pkey PRIMARY KEY (id);"
						+ "CREATE TABLE IF NOT EXISTS projeto.personal_lists ();"
						+ "ALTER TABLE projeto.personal_lists ADD COLUMN IF NOT EXISTS id integer NOT NULL;"
						+ "ALTER TABLE projeto.personal_lists ADD COLUMN IF NOT EXISTS email character varying (64) NOT NULL;"
						+ "ALTER TABLE projeto.personal_lists ADD COLUMN IF NOT EXISTS name character varying (32) NOT NULL;"
						+ "CREATE SEQUENCE IF NOT EXISTS projeto.lists_id_seq\r\n"
						+ "		AS integer\r\n"
						+ "		START WITH 1\r\n"
						+ "		INCREMENT BY 1\r\n"
						+ "		NO MINVALUE\r\n"
						+ "		NO MAXVALUE\r\n"
						+ "		CACHE 1;"
						+ "ALTER SEQUENCE projeto.lists_id_seq OWNED BY projeto.personal_lists.id;"
						+ "ALTER TABLE ONLY projeto.personal_lists ALTER COLUMN id SET DEFAULT nextval('projeto.lists_id_seq'::regclass);"
						+ "ALTER TABLE ONLY projeto.personal_lists DROP CONSTRAINT IF EXISTS lists_pkey;"
						+ "ALTER TABLE ONLY projeto.personal_lists ADD CONSTRAINT lists_pkey PRIMARY KEY (id);";
		
		try {
			executeUpdate(query);
		} catch (SQLException e) {
			System.out.println(e);
			System.out.println("There was an conection error in the database setup");
		}
	}
	
	protected static String executeQuery_SingleColumn(String query) throws SQLException{
		
		try (Connection connection = dataSource.getConnection()){
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
	
	protected static int executeUpdate(String query) throws SQLException{
		
		try (Connection connection = dataSource.getConnection()){
			if (connection != null) {
				Statement stmt = connection.createStatement();
				return stmt.executeUpdate(query);
			} else {
				throw new SQLException("Connection failed!");
			}
		}
		
	}
}
