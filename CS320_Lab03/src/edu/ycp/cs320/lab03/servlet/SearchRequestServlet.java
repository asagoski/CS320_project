package edu.ycp.cs320.lab03.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.ycp.cs320.lab03.FindUserWithUsername;
import edu.ycp.cs320.lab03.controller.SearchRequestController;
import edu.ycp.cs320.lab03.model.Account;
import edu.ycp.cs320.lab03.model.Reservation;
import edu.ycp.cs320.lab03.model.SearchRequest;
import edu.ycp.cs320.lab03.persist.DatabaseProvider;
import edu.ycp.cs320.lab03.persist.DerbyDatabase;
import edu.ycp.cs320.lab03.persist.IDatabase;

public class SearchRequestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.getRequestDispatcher("/_view/checkRoomAvailability.jsp").forward(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		// Decode form parameters and dispatch to controller
		SearchRequest model = new SearchRequest();
		
		SearchRequestController controller = new SearchRequestController();
		controller.setModel(model);
		
		String errorMessage = null;
		int result = 0;
		int reservation = 1;
		
		IDatabase db = null;
		DatabaseProvider.setInstance(new DerbyDatabase());
		db = DatabaseProvider.getInstance();
		
		String userName = "ryan";
//		Integer reservationListADD = null;
		java.util.List<Reservation> reservationList = null;
		
		try {
			//String CheckInDate = req.getParameter("CheckInDate");
			//String CheckOutDate = req.getParameter("CheckOutDate");
			//int roomType = Integer.parseInt(req.getParameter("roomType"));
			
			//int reservation = Integer.parseInt(req.getParameter("reservation"));
						
			String CheckInDay = req.getParameter("CheckInDay");
			String CheckInMonth = req.getParameter("CheckInMonth");
			String CheckOutDay = req.getParameter("CheckOutDay");
			String CheckOutMonth = req.getParameter("CheckOutMonth");
			String City = req.getParameter("City");
			reservation = Integer.parseInt(req.getParameter("reservation"));
			
			
			model.setCheckInDay(CheckInDay);
			model.setCheckInMonth(CheckInMonth);
			model.setCheckOutDay(CheckOutDay);
			model.setCheckOutMonth(CheckOutMonth);
			//model.setRoomType(roomType);
			model.setCity(City);
			
			if (CheckOutDay == null || CheckInDay == null) {
				errorMessage = "Please enter dates";
			} else {
				
				String CheckInDate2 = model.getCheckInDay();
				String City2 = model.getCity();
				System.out.println(CheckInDate2);
				System.out.println(CheckOutDay);
				System.out.println(City2);
				result = reservation;
				System.out.println(result);
				controller.webScraper(CheckInDay, CheckInMonth, CheckOutDay, CheckOutMonth, City);
				//System.out.println(model.getHotelNames().get(1));
				//resp.sendRedirect("/HotelReservationSystem/Results");
				
			}	
		
		} catch (NumberFormatException e) {
			errorMessage = "Invalid";
		}
		
		// Add parameters as request attributes
		req.setAttribute("CheckInDay", req.getParameter("CheckInDay"));
		req.setAttribute("CheckOutDay", req.getParameter("CheckOutDay"));
		req.setAttribute("CheckInMonth", req.getParameter("CheckInMonth"));
		req.setAttribute("CheckOutMonth", req.getParameter("CheckOutMonth"));
		//req.setAttribute("roomType", req.getParameter("roomType"));
		req.setAttribute("City", req.getParameter("City"));
		req.setAttribute("reservation", req.getParameter("reservation"));
		req.setAttribute("result", result);
		
	
		// Add result objects as request attributes
		req.setAttribute("hotels0", model.getHotelNames().get(0));
		req.setAttribute("prices0", model.getPrices().get(0));
		req.setAttribute("rooms0", model.getRooms().get(0));
		req.setAttribute("hotels1", model.getHotelNames().get(1));
		req.setAttribute("prices1", model.getPrices().get(1));
		req.setAttribute("rooms1", model.getRooms().get(1));
		req.setAttribute("hotels2", model.getHotelNames().get(2));
		req.setAttribute("prices2", model.getPrices().get(2));
		req.setAttribute("rooms2", model.getRooms().get(2));
		req.setAttribute("hotels3", model.getHotelNames().get(3));
		req.setAttribute("prices3", model.getPrices().get(3));
		req.setAttribute("rooms3", model.getRooms().get(3));
		req.setAttribute("hotels4", model.getHotelNames().get(4));
		req.setAttribute("prices4", model.getPrices().get(4));
		req.setAttribute("rooms4", model.getRooms().get(4));
		req.setAttribute("hotels5", model.getHotelNames().get(5));
		req.setAttribute("prices5", model.getPrices().get(5));
		req.setAttribute("rooms5", model.getRooms().get(5));
		req.setAttribute("hotels6", model.getHotelNames().get(6));
		req.setAttribute("prices6", model.getPrices().get(6));
		req.setAttribute("rooms6", model.getRooms().get(6));
		req.setAttribute("hotels7", model.getHotelNames().get(7));
		req.setAttribute("prices7", model.getPrices().get(7));
		req.setAttribute("rooms7", model.getRooms().get(7));
		req.setAttribute("hotels8", model.getHotelNames().get(8));
		req.setAttribute("prices8", model.getPrices().get(8));
		req.setAttribute("rooms8", model.getRooms().get(8));
		req.setAttribute("hotels9", model.getHotelNames().get(9));
		req.setAttribute("prices9", model.getPrices().get(9));
		req.setAttribute("rooms9", model.getRooms().get(9));
		req.setAttribute("hotels10", model.getHotelNames().get(10));
		req.setAttribute("prices10", model.getPrices().get(10));
		req.setAttribute("rooms10", model.getRooms().get(10));
		req.setAttribute("hotels11", model.getHotelNames().get(11));
		req.setAttribute("prices11", model.getPrices().get(11));
		req.setAttribute("rooms11", model.getRooms().get(11));
		req.setAttribute("errorMessage", errorMessage);
		req.setAttribute("result", result);
		req.setAttribute("reservation", req.getParameter("reservation"));
		
		int usrID = 2;
		String site = model.getHotelNames().get(1);
		String room = model.getCity();
		String dateStart = model.getCheckInMonth()+"-"+model.getCheckInDay()+"-16";
		String dateEnd = model.getCheckOutMonth()+"-"+model.getCheckOutDay()+"-16";
		String cost = model.getPrices().get(1).substring(3, model.getPrices().get(1).length()).replace(",", "");
		//String cost = "500";
				

		// insert new book (and possibly new author) into DB
		db.insertReservationIntoReservationsTable(usrID, site, room, dateStart, dateEnd, cost);
		
		// Forward to view to render the result HTML document
		//req.getRequestDispatcher("/_view/checkRoomAvailability.jsp").forward(req, resp);
			
		System.out.println(reservation);
			// Forward to view to render the result HTML document
		req.getRequestDispatcher("/_view/checkRoomAvailability.jsp").forward(req, resp);
	}

}
