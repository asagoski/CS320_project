package edu.ycp.cs320.lab03.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import edu.ycp.cs320.lab03.controller.AccountController;
import edu.ycp.cs320.lab03.controller.LogInController;
import edu.ycp.cs320.lab03.controller.SearchRequestController;
import edu.ycp.cs320.lab03.model.Account;
import edu.ycp.cs320.lab03.model.SearchRequest;

public class IndexServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		//System.out.println("In the Index servlet");
		
		req.getRequestDispatcher("/_view/index.jsp").forward(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		// Decode form parameters and dispatch to controller
		// Decode form parameters and dispatch to controller
		Account model = new Account();
				
		AccountController controller = new AccountController();
		controller.setModel(model);
		
		String errorMessage = null;
		Double result = null;
		try {
			String username=req.getParameter("username"); 
			String password=req.getParameter("password");
			
			//Account act = controller.logIn(username, password);
			if((username.equals("adam") && password.equals("pass"))) { 
			//if(act !=null) { 
				//session.setAttribute("username",username); 
				model.setUsername(username);
				resp.sendRedirect("/HotelReservationSystem/Account"); 
			} else if((username.equals("ryan") && password.equals("password"))) { 
					//if(act !=null) { 
						//session.setAttribute("username",username); 
				model.setUsername(username);
				resp.sendRedirect("/HotelReservationSystem/Account");
			} else {
				//resp.sendRedirect("/HotelReservationSystem/index");
				errorMessage = "Invalid Username and/or Password";
			}

		} catch (NumberFormatException e) {
			errorMessage = "Invalid";
		}
		
		// Add parameters as request attributes
		req.setAttribute("first", req.getParameter("first"));
		req.setAttribute("second", req.getParameter("second"));
		
		// Add result objects as request attributes
		req.setAttribute("errorMessage", errorMessage);
		req.setAttribute("result", result);
		
		// Forward to view to render the result HTML document
		req.getRequestDispatcher("/_view/index.jsp").forward(req, resp);
	}
}
