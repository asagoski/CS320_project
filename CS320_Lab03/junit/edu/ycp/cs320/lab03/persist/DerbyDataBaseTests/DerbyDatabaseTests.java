package edu.ycp.cs320.lab03.persist.DerbyDataBaseTests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.ycp.cs320.lab03.model.Account;
import edu.ycp.cs320.lab03.model.Reservation;
import edu.ycp.cs320.lab03.persist.DatabaseProvider;
import edu.ycp.cs320.lab03.persist.DerbyDatabase;
import edu.ycp.cs320.lab03.FindUserWithUsername;
//import edu.ycp.cs320.lab03.model.Pair;
import edu.ycp.cs320.lab03.persist.IDatabase;

public class DerbyDatabaseTests {

	private IDatabase db = null;
	
	List<Account> Account = null;
	List<Reservation>   reservations  = null;
	List<edu.ycp.cs320.lab03.model.Account> accountList = null;
	List<Reservation> reservationList = null;
	String userName = "Ryan";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		// creating DB instance here
		DatabaseProvider.setInstance(new DerbyDatabase());
		db = DatabaseProvider.getInstance();		
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFindAllReservationsWithAccount() {
		System.out.println("\n*** Testing finding of reservations with users ***");

		// get the list of Reservations from DB
		String userName = "ryan";
		reservationList = db.findAllReservationsWithUser(userName);
		
		// NOTE: this is a simple test to check if no results were found in the DB
		if (reservationList.isEmpty()) {
			System.out.println("No books in database");
			fail("No books returned from Library DB");
		}
		// NOTE: assembling the results into Author and Book lists so that they could be
		//       inspected for correct content - well-formed objects with correct content
		else {
			reservations = new ArrayList<Reservation>();
			for (int i = 0; i < reservations.size(); i++) {
				Reservation reserv = reservations.get(i);
				reservations.add(reserv);
				System.out.println(reserv.getUserID() + ", " + reserv.getSite() + ", " + reserv.getRoom() + ", " + reserv.getCheckInDate() + 
						", " + reserv.getCheckOutDate() + ", " + reserv.getCost());
			}			
		}
	}

	@Test
	public void testFindUsersWithUsername() {

		System.out.println("\n*** Testing findUserWithUsername ***");

		// get the list of (Accounts) pairs from DB
		List<Account> acc = db.findUsersWithUsername(userName);

		// NOTE: this is a simple test to check if no results were found in the DB
		if (acc == null) {
			System.out.println("No accounts found for user" + userName);
			fail("No accounts returned from DB");
		}
		// NOTE: assembling the results into Author and Book lists so that they could be
		//       inspected for correct content - well-formed objects with correct content
		else {
			for(int i = 0; i < acc.size(); i++){
				Account temp = acc.get(i);
				System.out.println(temp.getUserId() + ", " + temp.getName() + ", " + temp.getUsername() + temp.getPassword() + ", "
						+ temp.getPayment() + ", " + temp.getSecCode() + ", " + temp.getEmail() + ", " + temp.getAddress());
			}
		}			
	}
	

	@Test
	public void testInsertReservationIntoReservationsTable() throws Exception {
		System.out.println("\n*** Testing insertReservationIntoReservationsTable ***");
		userName = "ryan";
		List<Account> temp = FindUserWithUsername.main(userName);
		if(temp.size() > 0){
			Account acct = temp.get(0);
			int usrID = acct.getUserId();
			String site = "Home";
			String room = "2";
			String dateStart = "10-14-16";
			String dateEnd = "10-16-16";
			String cost = "15";
				
		// insert new book (and possibly new author) into DB
		Integer reservID = db.insertReservationIntoReservationsTable(usrID, site, room, dateStart, dateEnd, cost);;

		// check the return value - should be a reservationID > 0
		if (reservID > 0)
		{
			// try to retrieve the reservation from the DB
			// get the list of (Author, Book) pairs from DB
			reservationList = db.findAllReservationsWithUser(userName);
			
			if (reservationList.isEmpty()) {
				System.out.println("No reservations found for user <" + userName + ">");
				fail("Failed to insert new reservation <" + site + "> into DB");
			}
			// otherwise, the test was successful.  Now remove the reservation just inserted to return the DB
			// to it's original state, except for using a reservationID
			else {
				System.out.println("New reservation (ID: " + reservID + ") successfully added to reservations table: <" + site + ">");
				
				// now delete reservation from DB
				// leaving the DB in its previous state - except that an reservationID has been used
//				List<Reservation> reservation = db.removeReservationBySiteandUser(site, userName);				
			}
		}
		else
		{
			System.out.println("Failed to insert new reservation (ID: " + reservID + ") into reservations table: <" + site + ">");
			fail("Failed to insert new resrvation <" + site + "> into DB");
		}}
		else{
			System.out.println("Failed to insert new reservation as Account" + userName + "does not exist");
		}
	}
	
	@Test
	public void testInsertAccountIntoAccountTable() {
		System.out.println("\n*** Testing insertAccountintoAccountTable ***");

		String name = "Jim Kurk";
		String username = "Enterprise";
		String pass = "NCC1701";
		String payment = "7890789078907890";
		String secCode = "951";
		String email = "captkurk@USSEnterprise.ufp";
		String address = "FollowThePrimeDirective101";
				
		// insert new book (and possibly new author) into DB
		Integer userID = db.insertUserIntoAccountTable(name, username, pass, payment, secCode, email, address);

		// check the return value - should be a reservationID > 0
		if (userID > 0)
		{
			// try to retrieve the reservation from the DB
			// get the list of (Author, Book) pairs from DB
			accountList = db.findUsersWithUsername(username);
			
			if (accountList.isEmpty()) {
				System.out.println("No account found for user <" + username + ">");
				fail("Failed to insert new account <" + name + "> into DB");
			}
			// otherwise, the test was successful.  Now remove the account just inserted to return the DB
			// to it's original state, except for using a userID
			else {
				System.out.println("New account (ID: " + userID + ") successfully added to account table: <" + username + ">");
			}
		}
		else
		{
			System.out.println("Failed to insert new account (ID: " + userID + ") into Account table: <" + username + ">");
			fail("Failed to insert new account <" + username + "> into DB");
		}
	}

	@Test
	public void testRemoveReservationByReservationID() {
		System.out.println("\n*** Testing removeReservationbyReservationID ***");
		
		int usr     = 2;
		String site = "away";
		String room  = "3";
		String dateStart = "04/15/16";
		String dateEnd = "04/25/16";
		String cost = "146";
				
		// insert new reservation into DB
		Integer reservID = db.insertReservationIntoReservationsTable(usr, site, room, dateStart, dateEnd, cost);
		int pre = db.findAllReservationsWithUser("ryan").size();
		
		// check to see that insertion was successful before proceeding
		if (reservID > 0) {
			// now delete Book (and its Author) from DB
			List<Reservation> reservation = db.removeReservationByReservID(reservID);
			
			if(reservation == null){
				System.out.println("No valid reservations");
			}
			else if (pre < reservation.size()) {
				System.out.println("Failed to remove reservation");
				fail("Reservation failed to remove.");
			}
			else {
				for(int i = 0; i < reservation.size(); i++){
					System.out.println(reservation.get(i).getReservID() + ", " + reservation.get(i).getSite());
				}
			}
		}
		else {
			System.out.println("Failed to insert new reservation (ID: " + reservID + ") into reservations table.");
			fail("Failed to insert new reservation into DB");			
		}
	}
	
}
