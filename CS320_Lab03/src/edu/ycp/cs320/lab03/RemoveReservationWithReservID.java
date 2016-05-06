package edu.ycp.cs320.lab03;

import java.util.List;
import java.util.Scanner;

import edu.ycp.cs320.lab03.model.Reservation;
//import edu.ycp.cs320.lab03.model.Pair;
import edu.ycp.cs320.lab03.persist.DatabaseProvider;
import edu.ycp.cs320.lab03.persist.IDatabase;

public class RemoveReservationWithReservID {
	public static List<Reservation> main(int reservID) throws Exception {
		Scanner keyboard = new Scanner(System.in);

		// Create the default IDatabase instance
		InitDatabase.init(keyboard);
		
		// get the DB instance and execute transaction
		IDatabase db = DatabaseProvider.getInstance();
		List<Reservation> reservList = db.removeReservationByReservID(reservID);
		
		// check if anything was returned and output the list
		if (reservList.isEmpty()) {
			System.out.println("There are no remaining valid reservations.");
			return null;
		}
		else {
			for (int i = 0; i < reservList.size(); i++) {
				Reservation reserv = reservList.get(i);
				System.out.println(reserv.getSite() + ", " + reserv.getCheckInDate() + ", " + reserv.getCheckOutDate() + ", " + reserv.getCost());
			}
			return reservList;
		}
	}
}
