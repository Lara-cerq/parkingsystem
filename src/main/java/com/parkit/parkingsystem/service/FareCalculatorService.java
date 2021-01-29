package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	public void calculateFare(Ticket ticket) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		long inHour = ticket.getInTime().getTime();
		long outHour = ticket.getOutTime().getTime();

		double duration = (outHour - inHour) / (1000 * 60 * 60.0);

		TicketDAO ticketDAO = new TicketDAO();
		int numberOfTimes = ticketDAO.countVehiculeReg(ticket.getVehicleRegNumber());

		switch (ticket.getParkingSpot().getParkingType()) {
		case CAR: {
			if (duration > 0.5) {
				if (numberOfTimes > 1) {
					ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR - (duration * Fare.CAR_RATE_PER_HOUR * 0.05));
					System.out.println(numberOfTimes);
					System.out.println("Congratulations! You are a recurring customer, so you have a discount of 5%!!");
				} else {
					ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
				}
			} else {
				ticket.setPrice(0);
			}

			break;
		}
		case BIKE: {
			if (duration > 0.5) {
				if (numberOfTimes > 1) {
					ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR - (duration * Fare.BIKE_RATE_PER_HOUR * 0.05));
					System.out.println("Congratulations! You are a recurring customer, so you have a discount of 5%!!");
				} else {
					ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
				}
			} else {
				ticket.setPrice(0);
			}

			break;
		}

		default:
			throw new IllegalArgumentException("Unkown Parking Type");
		}
	}
}