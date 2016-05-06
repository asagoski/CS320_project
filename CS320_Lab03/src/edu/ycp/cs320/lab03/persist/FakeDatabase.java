package edu.ycp.cs320.lab03.persist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.ycp.cs320.lab03.model.Account;
import edu.ycp.cs320.lab03.model.Pair;
import edu.ycp.cs320.lab03.model.Reservation;

public class FakeDatabase implements IDatabase {
	
	private List<Account> accountList;
	private List<Reservation> reservList;
	private List<Account> act;
	
	// Fake database constructor - initializes the DB
	// the DB only consists for a List of  and a List of Books
	public FakeDatabase() {
		accountList = new ArrayList<Account>();
		reservList = new ArrayList<Reservation>();
		
		// Add initial data
		readInitialData();
		
//		System.out.println(List.size() + " ");
//		System.out.println(bookList.size() + " books");
	}

	// loads the initial data retrieved from the CSV files into the DB
	public void readInitialData() {
		try {
			accountList.addAll(InitialData.getAccount());
			reservList.addAll(InitialData.getReservation());
		} catch (IOException e) {
			throw new IllegalStateException("Couldn't read initial data", e);
		}
	}
	
	// query that retrieves all reservations belonging to a user from DB
	@Override
	public List<Reservation> findAllReservationsWithUser(String userName) {
		return findAllReservationsWithUser(userName);
	}
	
	
	// query that inserts a new Reservation, and possibly new Account
	// insertion requires that we maintain Reservation and user ID's
	// this can be a real PITA, if we intend to use the IDs to directly access the ArrayLists, since
	// deleting a Reservation/Account in the list would mean updating the ID's, since other list entries are likely to move to fill the space.
	// or we could mark Reservation/Account entries as deleted, and leave them open for reuse, but we could not delete an Account
	//    unless they have no Reservations in the table
	
	// query that retrieves an Account based on userID
	@Override
	public List<Account> findUsersWithUsername(String userName) {
		for (Account acc : accountList) {
			String temp = acc.getUsername();
			if (temp.equals(userName)) {
				act.add(acc);
			}
		}
		return act;
	}

	@Override
	public Integer insertUserIntoAccountTable(String name, String username, String pass, String payment, 
			String secCode, String email, String address) {
	
		int accountID = accountList.size()+1;
		
		Account newAcc = new Account();
		newAcc.setUserId(accountID);
		newAcc.setName(name);
		newAcc.setUsername(username);
		newAcc.setPassword(pass);
		newAcc.setPayment(payment);
		newAcc.setSecCode(secCode);
		newAcc.setEmail(email);
		newAcc.setAddress(address);
		accountList.add(newAcc);
		
		// return new Account Id
		return accountID;
	}
	
	@Override
	public List<Reservation> removeReservationByReservID(int reservID){
		List<Reservation> current = null;
		return current;
	}

	@Override
	public Integer insertReservationIntoReservationsTable(int usr, String site, String room, String dateStart,
			String dateEnd, String cost) {
		int reservID  = -1;
		int accountID = -1;
		
		// search Reservation list for the Reservation, by userID, get reservID
		for (Account acc : accountList) {
			if (Integer.toString(acc.getUserId()).equals(usr)) {
				accountID = acc.getUserId();
			}
		}
		
		// if the Account wasn't found in Reservation list, we have to add new Account to Accounts list
		if (accountID < 0) {
			// set userID to size of Account list + 1 (before adding Account)
			accountID = accountList.size() + 1;
			
			// add new Account to Account list
			Account newAccount = new Account();			
			newAccount.setUserId(accountID);
			
			System.out.println("New user (ID: " + accountID + ")");
		}

		// set reservation ID to size of Reservation list + 1
		reservID = reservList.size() + 1;

		// add new reservation to reservations list
		Reservation newReserv = new Reservation();
		newReserv.setReservID(reservID);
		newReserv.setUserID(accountID);
		newReserv.setSite(site);
		newReserv.setRoom(room);
		newReserv.setCheckInDate(dateStart);
		newReserv.setCheckOutDate(dateEnd);
		newReserv.setCost(Integer.parseInt(cost));
		reservList.add(newReserv);
		
		// return new Reservation Id
		return reservID;
	}
}

