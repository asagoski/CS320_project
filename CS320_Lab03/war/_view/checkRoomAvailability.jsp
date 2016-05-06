<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<html>
	<head>
	<link href='https://fonts.googleapis.com/css?family=Open+Sans'
	rel='stylesheet' type='text/css'>
		<title >Check Room Availability</title>
		<style type="text/css">
		.error {
			color: red;
		}
		
		td.RoomSelection {
			text-align: left;
			padding-top: 20px;
			padding-bottom: 30px;
			font-family: 'Open Sans', sans-serif;
		}
		td.checkinDay {
			text-align: left;
			font-family: 'Open Sans', sans-serif;
		}
		td.coutDay {
			text-align: left;
			font-family: 'Open Sans', sans-serif;
		}
		td.checkinMonth {
			text-align: left;
			font-family: 'Open Sans', sans-serif;
		}
		td.coutMonth {
			text-align: left;
			font-family: 'Open Sans', sans-serif;
		}
		h2.titleSR {
			padding-top: 10px;
			text-align: Left;
			color: black;
			font-family: 'Open Sans', sans-serif;
		}
		ol.roomTypes {
			padding-bottom: 10px;
			color:black; 
		    text-align: left;
		}
		form.search {
			text-align: left;
			padding-top: 20px;
			font-family: 'Open Sans', sans-serif;
		}
		form.search {
			text-align: left;
			padding-top: 20px;
			font-family: 'Open Sans', sans-serif;
		}
		input.searchButton {
			
			font-size: 150%;
		}
		input.selectButton {
			
			font-size: 150%;
		}
		form.makeReservation {
			text-align: left;
			color: black;
			padding-top: 10px;
			padding-bottom: 10px;
			font-family: 'Open Sans', sans-serif;
		}

		form.reserve {
			text-align: left;
			color: black;
			padding-top: 10px;
			padding-bottom: 10px;
			font-family: 'Open Sans', sans-serif;
		}
		input.reserveButton {
			text-align: left;
			color: black;
			font-size: 150%;
		}
		h1.title {
			padding-top: 10px;
			text-align: Left;
			color: black;
			font-family: 'Open Sans', sans-serif;
		}
		h2.titleDates {
			padding-top: 10px;
			text-align: Left;
			color: black;
			font-family: 'Open Sans', sans-serif;
		}
		td.City {
			padding-bottom: 10px;
			text-align: Left;
			color: black;
			font-family: 'Open Sans', sans-serif;
		}

		</style>
	</head>

	<body>
		<c:if test="${! empty errorMessage}">
			<div class="error">${errorMessage}</div>
		</c:if>
		<h1 class="title">
		Check Room Availability
		</h1>
		<h2 class="titleDates">Dates of Stay</h2>
		<form action="${pageContext.servletContext.contextPath}/checkRoomAvailability" method="post">
   			<table>
				<tr>
   				<td class="checkinDay">Check-In Day:</td>
   				<td class="checkinMonth">Check-In Month:</td>
   				<td class="coutDay">Check-Out Day:</td>
   				<td class="coutMonth">Check-Out Month:</td>
   				</tr>
  				<tr>
  				<td><input type="text" name="CheckInDay" value="${CheckInDay}" /></td>	
  				<td><input type="text" name="CheckInMonth" value="${CheckInMonth}" /></td>	  		
  				<td><input type="text" name="CheckOutDay" value="${CheckOutDay}" /></td>
  				<td><input type="text" name="CheckOutMonth" value="${CheckOutMonth}" /></td>
  				</tr> 		
				<tr>
  				<td class="City">City:</td>
  				<td class="City"><input type="text" name="City" value="${City}" />(Baltimore or Harrisburg) </td>
  				</tr>
  			</table>
  			<!-- input class="selectButton" type="Submit" name="select" value="Search"-->
  			<h2 class="titleSR">Search Results</h2>
			<table>
				<tr>
				<td></td>
	  			<td>HOTEL</td>
	  			<td></td>
	   			<td>ROOM</td>
	   			<td></td>
	  			<td>PRICE</td>
	  			</tr>
	  			<tr>
	  			<td>1: </td>
	  			<td>${hotels0}</td>
	  			<td> | </td>
	  			<td>${rooms0}</td>
	  			<td> | </td>
	   			<td>${prices0}</td>
	  			</tr>
				<tr>
				<td>2: </td>
	  			<td>${hotels1}</td>
	  			<td> | </td>
	  			<td>${rooms1}</td>
	  			<td> | </td>
	   			<td>${prices1}</td>
	  			</tr>
	  			<tr>
	  			<td>3: </td>
	  			<td>${hotels2}</td>
	  			<td> | </td>
	  			<td>${rooms2}</td>
	  			<td> | </td>
	   			<td>${prices2}</td>
	  			</tr>
	  			<tr>
	  			<td>4: </td>
	  			<td>${hotels3}</td>
	  			<td> | </td>
	  			<td>${rooms3}</td>
	  			<td> | </td>
	   			<td>${prices3}</td>
	  			</tr>
	  			<tr>
	  			<td>5: </td>
	  			<td>${hotels4}</td>
	  			<td> | </td>
	  			<td>${rooms4}</td>
	  			<td> | </td>
	   			<td>${prices4}</td>
	  			</tr>
	  			<tr>
	  			<td>6: </td>
	  			<td>${hotels5}</td>
	  			<td> | </td>
	  			<td>${rooms5}</td>
	  			<td> | </td>
	   			<td>${prices5}</td>
	  			</tr>
	  			<tr>
	  			<td>7: </td>
	  			<td>${hotels6}</td>
	  			<td> | </td>
	  			<td>${rooms6}</td>
	  			<td> | </td>
	   			<td>${prices6}</td>
	  			</tr>
				<tr>
				<td>8: </td>
	  			<td>${hotels7}</td>
	  			<td> | </td>
	  			<td>${rooms7}</td>
	  			<td> | </td>
	   			<td>${prices7}</td>
	  			</tr>
	  			<tr>
	  			<td>9: </td>
	  			<td>${hotels8}</td>
	  			<td> | </td>
	  			<td>${rooms8}</td>
	  			<td> | </td>
	   			<td>${prices8}</td>
	  			</tr>
	  			<tr>
	  			<td>10: </td>
	  			<td>${hotels9}</td>
	  			<td> | </td>
	  			<td>${rooms9}</td>
	  			<td> | </td>
	   			<td>${prices9}</td>
	  			</tr>
	  			<tr>
	  			<td>11: </td>
	  			<td>${hotels10}</td>
	  			<td> | </td>
	  			<td>${rooms10}</td>
	  			<td> | </td>
	   			<td>${prices10}</td>
	  			</tr>
	  			<tr>
	  			<td>12: </td>
	  			<td>${hotels11}</td>
	  			<td> | </td>
	  			<td>${rooms11}</td>
	  			<td> | </td>
	   			<td>${prices11}</td>
	  			</tr>
			</table> 
  		
  		
  		<!-- form class="makeReservation" action="${pageContext.servletContext.contextPath}/checkRoomAvailability" method="post"-->
		  	Make Reservation:
  			<input type="text" name="reservation" value="${reservation}"/>
  			<input class="selectButton" type="Submit" name="confirm" value="Confirm Selection">
  			<table>
  			<tr>
  			<td class="label">Selected: ${result}</td>
			</tr>
		</table>
		</form>
  		
  		<form class="reserve" action="../HotelReservationSystem/Confirmation">
		<input class="reserveButton" type="submit" name="reserve" value="Reserve">
		</form>	
	</body>
</html>