package edu.ycp.cs320.lab03;

//import java.util.List;
import java.util.Scanner;

import edu.ycp.cs320.lab03.model.Account;
import edu.ycp.cs320.lab03.persist.DatabaseProvider;
import edu.ycp.cs320.lab03.persist.IDatabase;

public class FindUserWithUsername {
	public static void main(String userName) throws Exception {
		Scanner keyboard = new Scanner(System.in);

		// Create the default IDatabase instance
		InitDatabase.init(keyboard);
		
		// get the DB instance and execute transaction
		IDatabase db = DatabaseProvider.getInstance();
		Account userList = db.findUsersWithUsername(userName);
		
		// check if anything was returned and output the list
		if (userList == null) {
			System.out.println("There are no users in the database");
		}
		else {
			System.out.println(userList.getName());
			
		}
	}
}
