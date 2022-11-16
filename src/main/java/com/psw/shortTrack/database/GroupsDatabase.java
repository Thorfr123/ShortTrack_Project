package com.psw.shortTrack.database;

import java.sql.SQLException;
import java.util.ArrayList;

import com.psw.shortTrack.data.Group;

public class GroupsDatabase extends Database{

	public static int createGroup(String name, String manager, ArrayList<String> members) throws SQLException{
		String query = "INSERT INTO projeto.groups (name, manager, members)" +
				   "	VALUES ('" + name + "', '" + manager + "\', '{";

		for (int i = 0; i < members.size(); i++) {
			if (i == members.size() - 1) {
				query += "\"" + members.get(i) + "\"";
			} else {
			query += "\"" + members.get(i) + "\",";
			}
 		}
		
		query += "}\') RETURNING id;";
		
		
		return Integer.parseInt(executeQuery_SingleColumn(query));
	}	
	
}
