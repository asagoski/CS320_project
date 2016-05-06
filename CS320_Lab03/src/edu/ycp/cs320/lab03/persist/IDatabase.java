package edu.ycp.cs320.lab03.persist;

import java.util.List;

import edu.ycp.cs320.lab03.model.Account;
//import edu.ycp.cs320.lab03.model.Pair;
import edu.ycp.cs320.lab03.model.Reservation;

public interface IDatabase {
	public Integer insertUserIntoAccountTable(String name, String username, String pass, String payment, String secCode, String email, String address);
	public Integer insertReservationIntoReservationsTable(int usr, String site, String room, String dateStart, String dateEnd, String cost);
	public List<Reservation> findAllReservationsWithUser(String userName);
	public List<Account> findUsersWithUsername(String userName);
	public List<Reservation> removeReservationByReservID(int reservID);
	}
