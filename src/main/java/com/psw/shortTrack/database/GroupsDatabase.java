package com.psw.shortTrack.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.postgresql.util.PSQLException;

import com.psw.shortTrack.data.Account;
import com.psw.shortTrack.data.Group;
import com.psw.shortTrack.data.Task;

public class GroupsDatabase extends Database{

	/**
	 * Creates a new group in the database. Updates the groups' id.
	 * It doesn't verify if the members' accounts exists
	 * 
	 * @param group Group that you want to add in the database
	 * @return (True) Success; (False) If the manager account doesn't exist
	 * 
	 * @throws NotFoundException If the manager account doesn't exist
	 * @throws SQLException If a database access error occurs
	 */
	public static boolean createGroup(Group group) throws NotFoundException, SQLException {
		
		try {
			
			group.setID(executeQueryReturnInt(
					"INSERT INTO projeto.groups (name, manager, members)\r\n"
					+ "VALUES (" + toSQL((String)group.getName()) + "," + toSQL((String)group.getManagerEmail()) + "," 
					+ toSQL((ArrayList<String>)group.getMemberEmails()) + ")\r\n"
					+ "RETURNING id;"));
			return true;
			
		} catch(PSQLException psql) {
			if (psql.getSQLState().startsWith("23")) {
				throw new NotFoundException("The manager account was not found");
			}
			throw psql;
		}
		
	}
	
	/**
	 * Deletes a group from the database.
	 * It also deletes the tasks of this group, because the database is configured to delete on cascade
	 * 
	 * @param id Group's id
	 * @return (True) If it was deleted; (False) If the group still exists
	 * 
	 * @throws SQLException If a database access error occurs
	 */
	public static boolean deleteGroup(int id) throws SQLException {
		
		if (executeUpdate(
			"DELETE FROM projeto.groups WHERE id=" + toSQL(id) + ";"
		) > 0) {
			return true;
		}
		else {
			return !existGroup(id);
		}
		
	}
	
	/**
	 * Returns every groups with the user's email, as a manager or as a member.
	 * If the user is the manager, it returns all the tasks of that group.
	 * If the user is only a member, it only returns the tasks assigned to him.
	 * 
	 * @param email String with user's email
	 * @return ArrayList every group with the user's email
	 * 
	 * @throws NotFoundException If the user's account doesn't exist
	 * @throws SQLException If a database access error occurs
	 */
	public static ArrayList<Group> getAllGroups(Account user) throws NotFoundException, SQLException {
		
		try (Connection connection = Database.getConnection()){
			
			Statement stmt = connection.createStatement();
			
			String emailSQL = toSQL((String)user.getEmail());
			ResultSet rs = stmt.executeQuery(
				  "SELECT * FROM (\r\n"
				+ "  SELECT g_t.*, g.id AS group_id_pk, g.manager AS manager_email, g.name AS group_name,"
				+ "g.members, acc1.name AS manager_name, acc2.name AS assigned_to_name\r\n"
				+ "  FROM projeto.groups g\r\n"
				+ "  LEFT JOIN projeto.group_tasks g_t ON (g_t.group_id=g.id AND"
				+ "(assigned_to=" + emailSQL + " OR manager=" + emailSQL + "))\r\n"
				+ "  LEFT JOIN projeto.account acc1 ON manager=acc1.email\r\n"
				+ "  LEFT JOIN projeto.account acc2 ON assigned_to=acc2.email\r\n"
				+ "  WHERE manager=" + emailSQL + " OR " + emailSQL + "=ANY(members)\r\n"
				+ "	 ORDER BY group_id_pk, id) AS t\r\n"
				+ "NATURAL FULL JOIN (\r\n"
				+ "  SELECT ARRAY(\r\n"
				+ "    SELECT DISTINCT ARRAY[acc.email, acc.name]\r\n"
				+ "    FROM projeto.account acc\r\n"
				+ "    LEFT JOIN projeto.groups g ON email=ANY(g.members)"
				+ "    WHERE g.manager=" + emailSQL + " OR " + emailSQL + "=ANY(g.members)\r\n"
				+ ") AS member_accounts) AS q"
			);
			
			ArrayList<Group> groups = new ArrayList<Group>();
			ArrayList<Account> accounts = new ArrayList<Account>();
			Group atual = null;
			
			while (rs.next()) {
				
				if (rs.getRow() == 1) {
					/* Lê todos os membros de todos os grupos
					 * Isto apenas é chamado uma vez porque as contas vêm repetidas em todas as linhas
					 */
					try {
						
						for (String[] row : (String[][]) rs.getArray("member_accounts").getArray()) {
							
							Account newMember = new Account(row[0], row[1]);
							accounts.add(newMember);
							
						}
						
					}
					catch (ClassCastException cceIgnore) {
						/* Isto acontece quando o user ou não tem grupos ou tem grupos sem membros
						 * Então dá exceção quando tenta dar cast do array para String[][] pq o array é unidimensional
						 */
					}
				}
				
				
				int group_id = rs.getInt("group_id_pk");
				if (group_id != 0) {
					
					if (atual == null || group_id != atual.getID()) {
						
						atual = constructGroup(rs, user, accounts);
						groups.add(atual);
						
					}
					
					Task task = GroupTasksDatabase.getTask(rs);
					if (task != null) {
						atual.getTaskList().add(task);
					}
					
				}
				
			}
			
			if (groups.size() == 0) {
				// If there are no groups, it verifies if the user's account exists
				if (AccountsDatabase.checkEmail(user.getEmail())) {
					throw new NotFoundException("User's account was not found");
				}
			}
			
			return groups;
		}
		
	}

	/**
	 * Changes the name of the group in the database.
	 * 
	 * @param id Integer with group's id
	 * @param newGroupName String with group's new name
	 * 
	 * @throws NotFoundException Group doesn't exist
	 * @throws SQLException If a database access error occurs
	 */
	public static boolean changeName(int id, String newGroupName) throws SQLException {
		
		if (executeUpdate(
			"UPDATE projeto.groups SET name=" + toSQL((String)newGroupName) + " WHERE id=" + toSQL(id) + ";"
		) > 0) {
			return true;
		}
		else if (!existGroup(id)) {
			throw new NotFoundException("Group was not found");
		}
		else {
			return false;
		}
		
	}
	
	/**
	 * Removes a member from one group and assigns his tasks to NULL.
	 * 
	 * @param id Group's id
	 * @param member Account of the member to remove
	 * @return (True) Success; (False) Member still belongs to the group
	 * 
	 * @throws NotFoundException If group doesn't exist
	 * @throws SQLException If a database access error occurs
	 */
	public static boolean removeMember(int id, Account member) throws NotFoundException, SQLException {

		executeUpdate(
			"DELETE FROM projeto.notifications\r\n"
			+ "WHERE source=" + toSQL((String)member.getEmail()) + " AND group_id=" + toSQL(id) + ";"
		);
		
		if (executeUpdate(
			"UPDATE projeto.groups SET members=("
			+ "SELECT array_remove(members,'" + member.getEmail() + "') FROM projeto.groups WHERE id='" + id + "') "
			+ "WHERE id='"+id+"';\r\n"
			+ "UPDATE projeto.group_tasks SET assigned_to=NULL WHERE group_id='" + id + "' AND assigned_to='" + member.getEmail() + "';"
		) > 0) {
			return true;
		}
		else if (!existGroup(id)) {
			throw new NotFoundException("Group was not found");
		}
		else {
			return !belongsToGroup(id, member.getEmail());
		}
			
	}

	/**
	 * Adds a member to a group in the database.
	 * 
	 * @param id Group's id
	 * @param member Member's account
	 * @return (True) Success; (False) Member still doesn't belong to the group
	 * 
	 * @throws NotFoundException If the group doesn't exist
	 * @throws SQLException If a database access error occurs
	 */
	public static boolean addMember(int id, Account member) throws NotFoundException, SQLException {
		
		if (executeUpdate(
			"UPDATE projeto.groups SET members=("
			+ "SELECT array_append(members,'" + member.getEmail() + "') FROM projeto.groups WHERE id='" + id + "') "
			+ "WHERE id='" + id + "';\r\n"
		) > 0) {
			return true;
		}
		else if (!existGroup(id)) {
			throw new NotFoundException("Group was not found");
		}
		else {
			return (belongsToGroup(id, member.getEmail()));
		}
		
	}
	
	/**
	 * Checks if a group with the provided id exists
	 *
	 * @param id Group's id
	 * @return (True) It exists; (False) Otherwise
	 * 
	 * @throws SQLException If there is a connection error
	 */
	protected static boolean existGroup(int id) throws SQLException {
		
		return executeQueryReturnBoolean(
			"SELECT EXISTS(SELECT 1 FROM projeto.groups WHERE id=" + toSQL(id) + ";"
		);
		
	}
	
	/**
	 * Checks if a user belongs to the group with the provided id.
	 * 
	 * @param id Group's id
	 * @param email User's email
	 * @return (True) User is either the manager or a member of the group; (False) Otherwise
	 * 
	 * @throws SQLException If there is a connection error
	 */
	private static boolean belongsToGroup(int id, String email) throws SQLException {
		
		return executeQueryReturnBoolean(
			"SELECT EXISTS(SELECT 1 FROM projeto.groups WHERE id=" + toSQL(id) + " AND email=ANY(members);"	
		);
		
	}
	
	/**
	 *  Constructs a new group from the result set
	 *  
	 * @param rs Result Set
	 * @param user User's Account
	 * @param accounts Array with members accounts
	 * @return new Group
	 * 
	 * @throws SQLException If there is an error
	 */
	private static Group constructGroup(ResultSet rs, Account user, ArrayList<Account> accounts) throws SQLException {
		
		String manager_email = rs.getString("manager_email");
		Account managerAccount = null;
		if (user.getEmail().equals(manager_email)) {
			managerAccount = user;
		}
		else {
			managerAccount = new Account(manager_email, rs.getString("manager_name"));
		}
		
		ArrayList<Account> memberAccounts = new ArrayList<Account>();
		
		for (String member : (String[]) rs.getArray("members").getArray()) {
				
			for (Account m : accounts) {
				if (m.getEmail().equals(member)) {
					memberAccounts.add(m);
					break;
				}
			}
				
		}
		
		return new Group(	rs.getString("group_name"),
							managerAccount,
							rs.getInt("group_id_pk"),
							new ArrayList<Task>(),
							memberAccounts);
		
	}
	
}
