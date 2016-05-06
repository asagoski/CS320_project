package edu.ycp.cs320.lab03.persist;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//import edu.ycp.cs320.lab03.model.Pair;
import edu.ycp.cs320.lab03.persist.DBUtil;
import edu.ycp.cs320.lab03.FindAllReservationsWithUser;
//import edu.ycp.cs320.lab03.persist.DerbyDatabase.Transaction;
import edu.ycp.cs320.lab03.model.Account;
import edu.ycp.cs320.lab03.model.Reservation;

public class DerbyDatabase implements IDatabase {
	static {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		} catch (Exception e) {
			throw new IllegalStateException("Could not load Derby driver");
		}
	}
	
	public interface Transaction<ResultType> {
		public ResultType execute(Connection conn) throws SQLException;
	}

	private static final int MAX_ATTEMPTS = 10;


	// transaction that retrieves a list of Reservations with their Account, given userID
	@Override
	public List<Reservation> findAllReservationsWithUser(final String userName) {
		return executeTransaction(new Transaction<List<Reservation>>() {
			@Override
			public List<Reservation> execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;

				// try to retrieve Accounts and Reservations based on userName, passed into query
				try {
					stmt = conn.prepareStatement(
							"select reservations.*" +
							" from reservations, account" +
							" where account.userID = reservations.userID " +
							" and account.userName = ? " +
							" order by reservations.site asc"
					);
					stmt.setString(1, userName);
					
					// list of reservations
					List<Reservation> result = new ArrayList<Reservation>();
					
					// execute the query, get the results, and assemble them in an ArrayLsit
					resultSet = stmt.executeQuery();
					while (resultSet.next()) {
						Reservation reserv = new Reservation();
						loadReservation(reserv, resultSet, 1);
						
						result.add(reserv);
					}
					
					return result;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}
	
	
	// transaction that retrieves all Accounts in Library
	@Override
	public List<Account> findUsersWithUsername(final String userName) {
		return executeTransaction(new Transaction<List<Account>>() {
			@Override
			public List<Account> execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;

				// try to retrieve Accounts and Reservations based on userName, passed into query
				try {
					stmt = conn.prepareStatement(
							"select account.*" +
							"  from  account " +
							"  where account.userName = ? " +
							"  order by account.userName asc"
					);
					stmt.setString(1, userName);
					
					// establish the list of Accounts to receive the result
					List<Account> result = new ArrayList<Account>();
					
					// execute the query, get the results, and assemble them in an ArrayLsit
					resultSet = stmt.executeQuery();
					while (resultSet.next()) {
						Account user = new Account();
						loadAccount(user, resultSet, 1);
						
						result.add(user);
					}
					
					return result;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
			}		
		});
	}
	
	
	// transaction that inserts new Reservation into reservations table
	// also first inserts new Account into Accounts table, if necessary
	@Override
	public Integer insertReservationIntoReservationsTable(final int usr, final String site, final String room, final String dateStart, final String dateEnd, final String cost) {
		return executeTransaction(new Transaction<Integer>() {
			@Override
			public Integer execute(Connection conn) throws SQLException {
				PreparedStatement stmt1 = null;
				PreparedStatement stmt2 = null;
				PreparedStatement stmt3 = null;
				PreparedStatement stmt4 = null;
				PreparedStatement stmt5 = null;
//				PreparedStatement stmt6 = null;
//				PreparedStatement stmt7 = null;
				
				ResultSet resultSet1 = null;
//	(unused)	ResultSet resultSet2 = null;
				ResultSet resultSet3 = null;
//	(unused)	ResultSet resultSet4 = null;
				ResultSet resultSet5 = null;
//				ResultSet resultSet6 = null;
//				ResultSet resultSet7 = null;
				
				// for saving user ID and reservation ID
				Integer userID = -1;
				Integer reservID   = -1;

				// try to retrieve userID (if it exists) from DB, for user name, passed into query
				try {
					stmt1 = conn.prepareStatement(
							"select account.userID from account " +
							"  where userID = ?"
					);
					stmt1.setInt(1, usr);
					
					// execute the query, get the result
					resultSet1 = stmt1.executeQuery();

					
					// if Account was found then save userID				
					if (resultSet1.next())
					{
						userID = resultSet1.getInt(1);
						System.out.println("User found with ID " + userID);						
					}
					else
					{
						System.out.println("User not found");
						
					}
					
					// now that we have all the information, insert new Reservation into Reservation table
					// prepare SQL insert statement to add new Reservation to Reservation table
					stmt4 = conn.prepareStatement(
							"insert into reservations (userID, site, room, checkindate, checkOutDate, cost) " +
							"  values(?, ?, ?, ?, ?, ?) "
					);
					stmt4.setInt(1, userID);
					stmt4.setString(2, site);
					stmt4.setString(3, room);
					stmt4.setString(4, dateStart);
					stmt4.setString(5, dateEnd);
					stmt4.setString(6, cost);
					
					// execute the update
					stmt4.executeUpdate();
					
					System.out.println("New reservation <" + site + "> inserted into reservation table");					

					// now retrieve book_id for new Book, so that we can return it
					// DB auto-generates book_id
					// prepare SQL statement to retrieve book_id for new Book
					stmt5 = conn.prepareStatement(
							"select reservations.reservationID from reservations " +
							"  where userID = ? and site = ? and room = ? and checkInDate = ? and checkOutdate = ? and cost = ?"
					);
					stmt5.setInt(1, userID);
					stmt5.setString(2, site);
					stmt5.setString(3, room);
					stmt5.setString(4, dateStart);
					stmt5.setString(5, dateEnd);
					stmt5.setString(6, cost);
					
					// execute the query
					resultSet5 = stmt5.executeQuery();
					
					// get the result - there had better be one
					if (resultSet5.next())
					{
						reservID = resultSet5.getInt(1);
						System.out.println("New reservation <" + site + "> ID: " + reservID + ")");						
					}
					else	// really should throw an exception here - the new book should have been inserted, but we didn't find it
					{
						System.out.println("New reservation <" + site + "> not found in reservations table (ID: " + reservID + ")");
					}
					
					return reservID;
				} finally {
					DBUtil.closeQuietly(resultSet1);
					DBUtil.closeQuietly(stmt1);
//	(unused)		DBUtil.closeQuietly(resultSet2);
					DBUtil.closeQuietly(stmt2);					
					DBUtil.closeQuietly(resultSet3);
					DBUtil.closeQuietly(stmt3);					
// (unused)			DBUtil.closeQuietly(resultSet4);
					DBUtil.closeQuietly(stmt4);
					DBUtil.closeQuietly(resultSet5);
					DBUtil.closeQuietly(stmt5);					}
			}
		});
	}
	// transaction that inserts new accounts into the account table
		@Override
		public Integer insertUserIntoAccountTable(final String name, final String userName, final String password, 
				final String payment, final String secCode, final String email, final String address) {
			return executeTransaction(new Transaction<Integer>() {
				@Override
				public Integer execute(Connection conn) throws SQLException {
					PreparedStatement stmt1 = null;
					PreparedStatement stmt2 = null;
					PreparedStatement stmt3 = null;
					PreparedStatement stmt4 = null;
					PreparedStatement stmt5 = null;
//					PreparedStatement stmt6 = null;
//					PreparedStatement stmt7 = null;
//					PreparedStatement stmt8 = null;
					
					ResultSet resultSet1 = null;
//					ResultSet resultSet2 = null;
					ResultSet resultSet3 = null;
//					ResultSet resultSet4 = null;
					ResultSet resultSet5 = null;
//					ResultSet resultSet6 = null;
//					ResultSet resultSet7 = null;
//					ResultSet resultSet8 = null;
					
					// for saving user ID
					Integer userID = -1;

					// try to retrieve userID (if it exists) from DB, for user name, passed into query
					try {
						stmt1 = conn.prepareStatement(
								"select userID from account " +
								"  where username = ?"
						);
						stmt1.setString(1, userName);
						
						// execute the query, get the result
						resultSet1 = stmt1.executeQuery();

						
						// if Account was found then save userID					
						if (resultSet1.next())
						{
							userID = resultSet1.getInt(1);
							System.out.println("User found with ID " + userID);						
						}
						else
						{
							System.out.println("User not found");
					
							// if the User is new, to insert new Account into Account table
							if (userID < 0) {
								// prepare SQL insert statement to add account to table
								stmt2 = conn.prepareStatement(
										"insert into account (username) " +
										"  values(?) "
								);
								stmt2.setString(1, userName);
								
								// execute the update
								stmt2.executeUpdate();
								
								System.out.println("New account <" + name + "> inserted in account table");						
							
								// try to retrieve userID for new Account - DB auto-generates userID
								stmt3 = conn.prepareStatement(
										"select userID from account " +
										"  where userName = ? "
								);
								stmt3.setString(1, userName);
								
								// execute the query							
								resultSet3 = stmt3.executeQuery();
								
								// get the result - there had better be one							
								if (resultSet3.next())
								{
									userID = resultSet3.getInt(1);
									System.out.println("New account <" + name + "> ID: " + userID);						
								}
								else	// really should throw an exception here - the new Account should have been inserted, but we didn't find them
								{
									System.out.println("New account <" + name + "> not found in Account table (ID: " + userID);
								}
							}
						}
						
						// now that we have all the information, insert new Account into Account table
						// prepare SQL insert statement to add new Reservation to Reservation table
						stmt4 = conn.prepareStatement(
								"insert into Account (name, userName, password, payment, secCode, email, address) " +
								"  values(?, ?, ?, ?, ?, ?, ?) "
						);
						stmt4.setString(1, name);
						stmt4.setString(2, userName);
						stmt4.setString(3, password);
						stmt4.setString(4, payment);
						stmt4.setString(5, secCode);
						stmt4.setString(6, email);
						stmt4.setString(7, address);
						
						// execute the update
						stmt4.executeUpdate();
						
						System.out.println("New Account <" + name + "> inserted into Account table");					

						// now retrieve AccountID for new account, so that we can return it
						// DB auto-generates AccountID
						// prepare SQL statement to retrieve AccountID for new Book
						stmt5 = conn.prepareStatement(
								"select userID from Account " +
								"  where name = ? and userName = ? and password = ? and payment = ? and secCode = ? and email = ? and address = ?"
						);
						stmt5.setString(1, name);
						stmt5.setString(2, userName);
						stmt5.setString(3, password);
						stmt5.setString(4, payment);
						stmt5.setString(5, secCode);
						stmt5.setString(6, email);
						stmt5.setString(7, address);
						
						// execute the query
						resultSet5 = stmt5.executeQuery();
						
						// get the result - there had better be one
						if (resultSet5.next())
						{
							userID = resultSet5.getInt(1);
							System.out.println("New account <" + userName + "> ID: " + userID);						
						}
						else	// really should throw an exception here - the new book should have been inserted, but we didn't find it
						{
							System.out.println("New account <" + userName + "> not found in Account table (ID: " + userID);
						}
						
						return userID;
					} finally {
						DBUtil.closeQuietly(resultSet1);
						DBUtil.closeQuietly(stmt1);
//		(unused)		DBUtil.closeQuietly(resultSet2);
						DBUtil.closeQuietly(stmt2);					
						DBUtil.closeQuietly(resultSet3);
						DBUtil.closeQuietly(stmt3);					
	// (unused)			DBUtil.closeQuietly(resultSet4);
						DBUtil.closeQuietly(stmt4);
						DBUtil.closeQuietly(resultSet5);
						DBUtil.closeQuietly(stmt5);					}
				}
			});
		}
		
		// transaction that deletes reservation and returns a list of current reservations (or null)
		@Override
		public List<Reservation> removeReservationByReservID(final int reservID) {
			return executeTransaction(new Transaction<List<Reservation>>() {
				@Override
				public List<Reservation> execute(Connection conn) throws SQLException {
					PreparedStatement stmt1 = null;
					PreparedStatement stmt2 = null;
					PreparedStatement stmt3 = null;
					PreparedStatement stmt4 = null;
					PreparedStatement stmt5 = null;
					PreparedStatement stmt6 = null;							
					
					ResultSet resultSet1    = null;			
					ResultSet resultSet2    = null;
//					ResultSet resultSet3    = null;
//					ResultSet resultSet4    = null;
					ResultSet resultSet5    = null;
//					ResultSet resultSet6    = null;				
					
					try {
						// first get the Author(s) of the Book to be deleted
						// just in case it's the only Book by this Author
						// in which case, we should also remove the Author(s)
						stmt1 = conn.prepareStatement(
								"select account.* " +
								"  from  account, reservations " +
								"  where reservations.reservationID = ? " +
								"    and account.userID = reservations.userID " 
						);
						
						// get the Book's Author(s)
						stmt1.setInt(1, reservID);
						resultSet1 = stmt1.executeQuery();
						
						// assemble list of Authors from query
						List<Account> users = new ArrayList<Account>();					
					
						while (resultSet1.next()) {
							Account user = new Account();
							loadAccount(user, resultSet1, 1);
							users.add(user);
						}
						
						// check if any Authors were found
						// this shouldn't be necessary, there should not be a Book in the DB without an Author
						if (users.size() == 0) {
							System.out.println("No accounts were found for that reservation");
						}
											
						// now get the Reservation to be deleted
						// we will need the id to remove associated entires in BookAuthors (junction table)
					
						stmt2 = conn.prepareStatement(
								"select reservations.* " +
								"  from  reservations " +
								"  where reservations.reservationID = ? "
						);
						
						// get the Reservations
						stmt2.setInt(1, reservID);
						resultSet2 = stmt2.executeQuery();
						
						// assemble list of Books from query
						List<Reservation> reservations = new ArrayList<Reservation>();					
					
						while (resultSet2.next()) {
							Reservation reservation = new Reservation();
							loadReservation(reservation, resultSet2, 1);
							reservations.add(reservation);
						}
						
						// first delete entries in BookAuthors junction table
						// can't delete entries in Books or Authors tables while they have foreign keys in junction table
						// prepare to delete the junction table entries (bookAuthors)
						stmt3 = conn.prepareStatement(
								"delete from reservations " +
								"  where reservationID = ? "
						);
						
						// delete the reservation from the reservations table.
						stmt3.setInt(1, reservations.get(0).getReservID());
						stmt3.executeUpdate();
						
						System.out.println("Removed reservation" + reservID + "From reservation database.");									
						
						// now check if the Author(s) have any Books remaining in the DB
						// only need to check if there are any entries in junction table that have this author ID
						List<Reservation> current = null;
						try {
							current = FindAllReservationsWithUser.main(users.get(0).getName());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 							
								DBUtil.closeQuietly(stmt6);					
							
							// we're done with this
							DBUtil.closeQuietly(resultSet5);
							DBUtil.closeQuietly(stmt5);
						
						return current;
					} finally {
						DBUtil.closeQuietly(resultSet1);
						DBUtil.closeQuietly(resultSet2);
//						DBUtil.closeQuietly(resultSet3);
//						DBUtil.closeQuietly(resultSet4);
						
						DBUtil.closeQuietly(stmt1);
						DBUtil.closeQuietly(stmt2);
						DBUtil.closeQuietly(stmt3);					
						DBUtil.closeQuietly(stmt4);					
					}
				}
			});
		}
	
	
	// wrapper SQL transaction function that calls actual transaction function (which has retries)
	public<ResultType> ResultType executeTransaction(Transaction<ResultType> txn) {
		try {
			return doExecuteTransaction(txn);
		} catch (SQLException e) {
			throw new PersistenceException("Transaction failed", e);
		}
	}
	
	// SQL transaction function which retries the transaction MAX_ATTEMPTS times before failing
	public<ResultType> ResultType doExecuteTransaction(Transaction<ResultType> txn) throws SQLException {
		Connection conn = connect();
		
		try {
			int numAttempts = 0;
			boolean success = false;
			ResultType result = null;
			
			while (!success && numAttempts < MAX_ATTEMPTS) {
				try {
					result = txn.execute(conn);
					conn.commit();
					success = true;
				} catch (SQLException e) {
					if (e.getSQLState() != null && e.getSQLState().equals("41000")) {
						// Deadlock: retry (unless max retry count has been reached)
						numAttempts++;
					} else {
						// Some other kind of SQLException
						throw e;
					}
				}
			}
			
			if (!success) {
				throw new SQLException("Transaction failed (too many retries)");
			}
			
			// Success!
			return result;
		} finally {
			DBUtil.closeQuietly(conn);
		}
	}

	// TODO: Here is where you specify the location of your Derby SQL database
	// TODO: You will need to change this location to the same path as your workspace for this example
	// TODO: Change it here and in SQLDemo under CS320_Lab06->edu.ycp.cs320.sqldemo	
	private Connection connect() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:derby:C:/CS320/Reservation_System/CS320_Lab03/reservation.db;create=true");		
		
		// Set autocommit to false to allow multiple the execution of
		// multiple queries/statements as part of the same transaction.
		conn.setAutoCommit(false);
		
		return conn;
	}
	
	// retrieves Account information from query result set
	private void loadAccount(Account user, ResultSet resultSet, int index) throws SQLException {
		user.setUserId(resultSet.getInt(index++));
		user.setName(resultSet.getString(index++));
		user.setUsername(resultSet.getString(index++));
		user.setPassword(resultSet.getString(index++));
		user.setPayment(resultSet.getString(index++));
		user.setSecCode(resultSet.getString(index++));
		user.setEmail(resultSet.getString(index++));
		user.setAddress(resultSet.getString(index++));
	}
	
	// retrieves Book information from query result set
	private void loadReservation(Reservation reserv, ResultSet resultSet, int index) throws SQLException {
		reserv.setReservID(resultSet.getInt(index++));
		reserv.setUserID(resultSet.getInt(index++));
		reserv.setSite(resultSet.getString(index++));
		reserv.setRoom(resultSet.getString(index++));
		reserv.setCheckInDate(resultSet.getString(index++));
		reserv.setCheckOutDate(resultSet.getString(index++));
		reserv.setCost(Integer.parseInt(resultSet.getString(index++)));
	}
	
	//  creates the Account and reservation tables
	public void createTables() {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt1 = null;
				PreparedStatement stmt2 = null;
				
				try {
					stmt1 = conn.prepareStatement(
						"create table account (" +
						"userid integer primary key generated by default as identity,"+
						"name varchar(40)," +
						"username varchar(40)," +
						"password varchar(40)," +
						"payment varchar(16)," +
						"secCode varchar(3)," +
						"email varchar(40)," +
						"address varchar(60)" +						
						")"
					);	
					stmt1.executeUpdate();
					
					System.out.println("Account table created");
					
					stmt2 = conn.prepareStatement(
							"create table reservations (" +
							"reservationID integer primary key generated by default as identity, " +
							"userID integer constraint userID references account," +
							"site varchar(40)," +
							"room varchar(40)," +
							"checkInDate varchar(9)," +
							"checkOutDate varchar(9)," +
							"cost varchar(8)" +
							")"
					);
					stmt2.executeUpdate();
					
					System.out.println("Reservations table created");					
				
					return true;
				} finally {
					DBUtil.closeQuietly(stmt1);
					DBUtil.closeQuietly(stmt2);
				}
			}
		});
	}
	
	// loads data retrieved from CSV files into DB tables in batch mode
	public void loadInitialData() {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				List<Account> accountList;
				List<Reservation> reservList;
				
				try {
					accountList = InitialData.getAccount();
					reservList = InitialData.getReservation();
				} catch (IOException e) {
					throw new SQLException("Couldn't read initial data", e);
				}

				PreparedStatement insertAccount = null;
				PreparedStatement insertReservation = null;

				try {
					insertAccount = conn.prepareStatement("insert into Account (name, userName, password, payment, secCode, email, address) values (?, ?, ?, ?, ?, ?, ?)");
					for (Account acc : accountList) {
						insertAccount.setString(1, acc.getName());
						insertAccount.setString(2, acc.getUsername());
						insertAccount.setString(3, acc.getPassword());
						insertAccount.setString(4, acc.getPayment());
						insertAccount.setString(5, acc.getSecCode());
						insertAccount.setString(6, acc.getEmail());
						insertAccount.setString(7, acc.getAddress());
						insertAccount.addBatch();
					}
					insertAccount.executeBatch();
					
					insertReservation = conn.prepareStatement("insert into reservations (userID, site, room, checkIndate, checkOutDate, cost) values (?, ?, ?, ?, ?, ?)");
					for (Reservation reserv : reservList) {
						insertReservation.setInt(1, reserv.getUserID());
						insertReservation.setString(2, reserv.getSite());
						insertReservation.setString(3, reserv.getRoom());
						insertReservation.setString(4, reserv.getCheckInDate());
						insertReservation.setString(5, reserv.getCheckOutDate());
						insertReservation.setInt(6, reserv.getCost());
						insertReservation.addBatch();
					}
					insertReservation.executeBatch();
					
					return true;
				} finally {
					DBUtil.closeQuietly(insertReservation);
					DBUtil.closeQuietly(insertAccount);
				}
			}
		});
	}
	
	// The main method creates the database tables and loads the initial data.
	public static void main(String[] args) throws IOException {
		System.out.println("Creating tables...");
		DerbyDatabase db = new DerbyDatabase();
		db.createTables();
		
		System.out.println("Loading initial data...");
		db.loadInitialData();
		
		System.out.println("Success!");
	}
}
