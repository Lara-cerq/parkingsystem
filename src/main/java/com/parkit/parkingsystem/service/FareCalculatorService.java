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

//		TicketDAO ticketDAO = new TicketDAO();
//
//		String vehicleRegNumber = ticket.getVehicleRegNumber();
//
//		int ticketDB = ticketDAO.countVehiculeReg(vehicleRegNumber);

		switch (ticket.getParkingSpot().getParkingType()) {
		case CAR: {
			if (duration > 0.5) {
//				if (ticketDB > 1) { // -> voir comment faire pour
																							// comparer celui qu'on paye
																							// et
					// ceux deja presents dans DB
//					ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR - (duration * Fare.CAR_RATE_PER_HOUR * 0.05));
//				} else {
					ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
//				}
			} else {
				ticket.setPrice(0);
			}

			break;
		}
		case BIKE: {
			if (duration > 0.5) {
				ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
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