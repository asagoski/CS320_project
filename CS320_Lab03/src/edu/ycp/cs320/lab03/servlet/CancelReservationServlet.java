package edu.ycp.cs320.lab03.servlet;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.ycp.cs320.lab03.controller.AccountController;
import edu.ycp.cs320.lab03.model.Account;
import edu.ycp.cs320.lab03.model.Reservation;
import edu.ycp.cs320.lab03.persist.DatabaseProvider;
import edu.ycp.cs320.lab03.persist.DerbyDatabase;
import edu.ycp.cs320.lab03.persist.IDatabase;

public class CancelReservationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.getRequestDispatcher("/_view/cancelReservation.jsp").forward(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		// Decode form parameters and dispatch to controller
				Account model = new Account();
				String user = req.getParameter("Username");
				AccountController controller = new AccountController();
				model = controller.setModel(model, user);
				String errorMessage = null;
				int result = 0;
				ArrayList<Reservation> reservations = model.getReservation();
				
				IDatabase db = null;
				DatabaseProvider.setInstance(new DerbyDatabase());
				db = DatabaseProvider.getInstance();
				
				String userName = "ryan";
//				Integer reservationListADD = null;
				java.util.List<Reservation> reservationList = null;
				
				//reservationListADD = db.insertReservationIntoReservationsTable(1, "HotelA", "1", "5/5/2016", "5/6/2016", "200");
				
				reservationList = db.findAllReservationsWithUser(userName);
//				if (reservationList.isEmpty()) {
//					System.out.println("No books in database");
//				}
				
//				reservations1 = new ArrayList<Reservation>();
//				Reservation reserv = reservations.get(0);
//				reservations.add(reserv);
//				System.out.println(reserv.getSite());
				
				try {
						//errorMessage = "No Reservations";
					result=Integer.parseInt(req.getParameter("cancelReservation"));
				} catch (NumberFormatException e) {
					errorMessage = "Invalid double";
				}
				
				// Add parameters as request attributes
				req.setAttribute("first", req.getParameter("first"));
				req.setAttribute("second", req.getParameter("second"));
				
				db.removeReservationByReservID(result);
				
				// Add result objects as request attributes
				req.setAttribute("errorMessage", errorMessage);
				req.setAttribute("result", result);
				req.setAttribute("cancelReservation", req.getParameter("cancelReservation"));
				//Set a series of strings as the current reservations
//				for(int i = 0; i < reservations.size(); i++){
//					req.setAttribute("reservation" + i, reservations.get(0).getSite());							
//				}
//				req.setAttribute("reservation0", "Hotel A | 05/25/16 | 05/27/16 | $400");
//				req.setAttribute("reservation1", reservationList.get(0).getSite()+" | "+reservationList.get(0).getCheckInDate()+" | "+reservationList.get(0).getCheckOutDate()+" | "+reservationList.get(0).getCost());
//				req.setAttribute("reservation2", reservationList.get(1).getSite()+" | "+reservationList.get(1).getCheckInDate()+" | "+reservationList.get(1).getCheckOutDate()+" | "+reservationList.get(1).getCost());
				for(int i = 0; i < reservationList.size(); i++){
					req.setAttribute("reservation" + i, reservationList.get(i).getRoom()+" | "+reservationList.get(i).getSite()+" | "+reservationList.get(i).getCheckInDate()+" | "+reservationList.get(i).getCheckOutDate()+" | $"+reservationList.get(i).getCost());							
				}	
				
				
				
				// Forward to view to render the result HTML document
				req.getRequestDispatcher("/_view/cancelReservation.jsp").forward(req, resp);
			}

		}