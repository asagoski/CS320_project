package edu.ycp.cs320.lab03.persist;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.ycp.cs320.lab03.model.Pair;
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
	
	private interface Transaction<ResultType> {
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

				// try to retrieve Authors and Books based on Author's last name, passed into query
				try {
					stmt = conn.prepareStatement(
							"select reservations.*" +
							"  from reservations " +
							" where Account.userID = reservations.userID " +
							"       and account.userName = ? " +
							"   order by reservations.site asc"
					);
					stmt.setString(1, userName);
					
					// establish the list of (Author, Book) Pairs to receive the result
					List<Reservation> result = new ArrayList<Reservation>();
					
					// execute the query, get the results, and assemble them in an ArrayLsit
					resultSet = stmt.executeQuery();
					while (resultSet.next()) {
						Reservation reserv = new Reservation();
						loadReservation(reserv, resultSet, 4);
						
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
				
				try {
					stmt = conn.prepareStatement(
							"select Account.*" +
							"from Account " +
							"where account.userName = ?"
					);
					stmt.setString(1, userName);
					
					// establish the list of (Author, Book) Pairs to receive the result
					List<Account> result = new ArrayList<Account>();
					
					// execute the query, get the results, and assemble them in an ArrayLsit
					resultSet = stmt.executeQuery();
					while (resultSet.next()) {
						Account acc = new Account();
						loadAccount(acc, resultSet, 4);
						
						result.add(acc);
					}
					
					return result;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
				}			
		});
	}
	
	
	// transaction that inserts new Book into the Books table
	// also first inserts new Author into Authors table, if necessary
	@Override
	public Integer insertReservationIntoReservationsTable(final String usr, final String site, final String dateStart, final String dateEnd, final int cost) {
		return executeTransaction(new Transaction<Integer>() {
			@Override
			public Integer execute(Connection conn) throws SQLException {
				PreparedStatement stmt1 = null;
				PreparedStatement stmt2 = null;
				PreparedStatement stmt3 = null;
				PreparedStatement stmt4 = null;
				PreparedStatement stmt5 = null;
				PreparedStatement stmt6 = null;
				
				ResultSet resultSet1 = null;
//	(unused)	ResultSet resultSet2 = null;
				ResultSet resultSet3 = null;
//	(unused)	ResultSet resultSet4 = null;
				ResultSet resultSet5 = null;
				ResultSet resultSet6 = null;
				
				// for saving user ID and reservation ID
				Integer userID = -1;
				Integer reservID   = -1;

				// try to retrieve userID (if it exists) from DB, for user name, passed into query
				try {
					stmt1 = conn.prepareStatement(
							"select userID from account " +
							"  where username = ?"
					);
					stmt1.setString(1, usr);
					
					// execute the query, get the result
					resultSet1 = stmt1.executeQuery();

					
					// if Author was found then save author_id					
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
							stmt2.setString(1, usr);
							
							// execute the update
							stmt2.executeUpdate();
							
							System.out.println("New account <" + usr + "> inserted in Authors table");						
						
							// try to retrieve usrID for new Account - DB auto-generates userID
							stmt3 = conn.prepareStatement(
									"select userID from account " +
									"  where userName = ? "
							);
							stmt3.setString(1, usr);
							
							// execute the query							
							resultSet3 = stmt3.executeQuery();
							
							// get the result - there had better be one							
							if (resultSet3.next())
							{
								userID = resultSet3.getInt(1);
								System.out.println("New account <" + usr + "> ID: " + userID);						
							}
							else	// really should throw an exception here - the new author should have been inserted, but we didn't find them
							{
								System.out.println("New account <" + usr + "> not found in Account table (ID: " + userID);
							}
						}
					}
					
					// now that we have all the information, insert new Reservation into Reservation table
					// prepare SQL insert statement to add new Reservation to Reservation table
					stmt4 = conn.prepareStatement(
							"insert into reservation (usr, site, dateStart, dateEnd, cost) " +
							"  values(?, ?, ?) "
					);
					stmt4.setInt(1, userID);
					stmt4.setString(2, site);
					stmt4.setString(3, dateStart);
					stmt4.setString(4, dateEnd);
					stmt4.setString(5, Integer.toString(cost));
					
					// execute the update
					stmt4.executeUpdate();
					
					System.out.println("New reservation <" + site + "> inserted into reservation table");					

					// now retrieve book_id for new Book, so that we can return it
					// DB auto-generates book_id
					// prepare SQL statement to retrieve book_id for new Book
					stmt5 = conn.prepareStatement(
							"select reservID from books " +
							"  where userID = ? and site = ? and dateStart = ? and dateEnd = ? and cost = ?"
					);
					stmt5.setInt(1, userID);
					stmt5.setString(2, site);
					stmt5.setString(3, dateStart);
					stmt5.setString(4, dateEnd);
					stmt5.setString(5, Integer.toString(cost));
					
					// execute the query
					resultSet5 = stmt5.executeQuery();
					
					// get the result - there had better be one
					if (resultSet5.next())
					{
						reservID = resultSet5.getInt(1);
						System.out.println("New reservation <" + site + "> ID: " + reservID);						
					}
					else	// really should throw an exception here - the new book should have been inserted, but we didn't find it
					{
						System.out.println("New reservation <" + site + "> not found in Books table (ID: " + reservID);
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
		public Integer insertUserIntoAccountTable(final String name, final String userName, final String pass, 
				final String payment, final String secCode, final String email, final String address) {
			return executeTransaction(new Transaction<Integer>() {
				@Override
				public Integer execute(Connection conn) throws SQLException {
					PreparedStatement stmt1 = null;
					PreparedStatement stmt2 = null;
					PreparedStatement stmt3 = null;
					PreparedStatement stmt4 = null;
					PreparedStatement stmt5 = null;
					PreparedStatement stmt6 = null;
					PreparedStatement stmt7 = null;
					PreparedStatement stmt8 = null;
					
					ResultSet resultSet1 = null;
//		(unused)	ResultSet resultSet2 = null;
					ResultSet resultSet3 = null;
//		(unused)	ResultSet resultSet4 = null;
					ResultSet resultSet5 = null;
					ResultSet resultSet6 = null;
					ResultSet resultSet7 = null;
					ResultSet resultSet8 = null;
					
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
								
								System.out.println("New account <" + userName + "> inserted in Authors table");						
							
								// try to retrieve usrID for new Account - DB auto-generates userID
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
									System.out.println("New account <" + userName + "> ID: " + userID);						
								}
								else	// really should throw an exception here - the new author should have been inserted, but we didn't find them
								{
									System.out.println("New account <" + userName + "> not found in Account table (ID: " + userID);
								}
							}
						}
						
						// now that we have all the information, insert new Account into Account table
						// prepare SQL insert statement to add new Reservation to Reservation table
						stmt4 = conn.prepareStatement(
								"insert into Account (name, userName, pass, payment, secCode, email, address) " +
								"  values(?, ?, ?) "
						);
						stmt4.setString(1, name);
						stmt4.setString(2, userName);
						stmt4.setString(3, pass);
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
								"select reservID from Account " +
								"  where name = ? and userName = ? and pass = ? and payment = ? and secCode = ? and email = ? and address = ?"
						);
						stmt5.setString(1, name);
						stmt5.setString(2, userName);
						stmt5.setString(3, pass);
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
							System.out.println("New account <" + userName + "> not found in Books table (ID: " + userID);
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
		Connection conn = DriverManager.getConnection("jdbc:derby:C:/CS320/CS320_Library_Example/CS320_Lab06/library.db;create=true");		
		
		// Set autocommit to false to allow multiple the execution of
		// multiple queries/statements as part of the same transaction.
		conn.setAutoCommit(false);
		
		return conn;
	}
	
	// retrieves Author information from query result set
	private void loadAccount(Account user, ResultSet resultSet, int index) throws SQLException {
		user.setUserId(resultSet.getInt(index++));
		user.setName(resultSet.getString(index++));
		user.setUsername(resultSet.getString(index++));
		user.setPassword(resultSet.getString(index++));
		user.setPayment(Integer.parseInt(resultSet.getString(index++)));
		user.setSecCode(Integer.parseInt(resultSet.getString(index++)));
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
						"create table Account (" +
						"	usrID integer primary key " +
						"		generated always as identity (start with 1, increment by 1), " +									
						"	name varchar(40)," +
						"	username varchar(40)" +
						"	password varchar(40)" +
						"	payment varchar(16)" +
						"	secCode varchar(3)" +
						"	email varchar(40)" +
						"	address varchar(60)" +
						")"
					);	
					stmt1.executeUpdate();
					
					stmt2 = conn.prepareStatement(
							"create table reservations (" +
							"	reservID integer primary key " +
							"		generated always as identity (start with 1, increment by 1), " +
							"	userID integer constraint author_id references authors, " +
							"	site varchar(50)," +
							"	room varchar(20)" +
							"	CheckInDate varchar(8)" +
							"	CheckOutDate varchar(8)" +
							"	cost varchar(10)" +
							")"
					);
					stmt2.executeUpdate();
					
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
					insertAccount = conn.prepareStatement("insert into Account (name, usrName, password, payment,secCode, email, address) values (?, ?, ?, ?, ?, ?)");
					for (Account acc : accountList) {
//						insertAuthor.setInt(1, author.getAuthorId());	// auto-generated primary key, don't insert this
						insertAccount.setString(1, acc.getName());
						insertAccount.setString(2, acc.getUsername());
						insertAccount.setString(3, acc.getPassword());
						insertAccount.setString(4, Integer.toString(acc.getPayment()));
						insertAccount.setString(4, Integer.toString(acc.getSecCode()));
						insertAccount.setString(5, acc.getEmail());
						insertAccount.setString(6, acc.getAddress());
						insertAccount.addBatch();
					}
					insertAccount.executeBatch();
					
					insertReservation = conn.prepareStatement("insert into reservations (author_id, title, isbn) values (?, ?, ?)");
					for (Reservation reserv : reservList) {
//					//	insertBook.setInt(1, book.getBookId());		// auto-generated primary key, don't insert this
						insertReservation.setInt(1, reserv.getUserID());
						insertReservation.setString(2, reserv.getSite());
						insertReservation.setString(3, reserv.getCheckInDate());
						insertReservation.setString(4, reserv.getCheckOutDate());
						insertReservation.setString(5, Integer.toString(reserv.getCost()));
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
