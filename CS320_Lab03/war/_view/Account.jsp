<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html>
<head>
<link href='https://fonts.googleapis.com/css?family=Open+Sans'
	rel='stylesheet' type='text/css'>
<title>Account</title>
<style type="text/css">
.error {
	color: red;
}

td.label {
	text-align: right;
}

h2.titleCR {
	text-align: center;
	color: black;
	font-family: 'Open Sans', sans-serif;
}

ol.currentReservations {
	padding-top: 10px;
	padding-bottom: 10px;
	border-style: solid;
	border-color: black;
	color: black;
	text-align: left;
}
form.reservations{
	
	padding-bottom: 10px;
}
</style>
</head>

<body>
	<h1 style="font-family: 'Open Sans', sans-serif">Account</h1>
	<form action="../HotelReservationSystem/checkRoomAvailability">
		<input type="submit" name="checkRA" value="Check Room Availability">
	</form>
	<h2 class="titleCR">Current Reservations</h2>
	<form class="reservations" action="${pageContext.servletContext.contextPath}/Account" method="post">
		<ol class="currentReservations">
			<li>${reservation0}</li>
			<li>${reservation1}</li>
			<li>${reservation2}</li>
			<li>${reservation3}</li>
			<li>${reservation4}</li>
			<li>${reservation5}</li>
		</ol>

	 	<input class="selectButton" type="Submit" name="select" value="Display Reservations">
	</form>
	<form action="../HotelReservationSystem/cancelReservation">
		<input type="submit" name="cancel" value="Cancel Reservation">
	</form>
	<br>
	<br>
	<form action="../HotelReservationSystem/index">
		<input type="submit" name="logout" value="Log out">
	</form>
</body>
</html>