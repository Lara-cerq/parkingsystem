package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.Date;


@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterAll
	private static void tearDown() {

	}

	@Test
	public void testParkingACar() {
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		int before = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR); // -> donne le numero/nombre de place
																			// disponible
		parkingService.processIncomingVehicle();
        Ticket ticket= ticketDAO.getTicket("ABCDEF");
		int after = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
		assertEquals(before, after - 1); //3 places de parking pouvant etre occupées
		//test that ticket is saved in DB
		assertNotNull(ticket);
	}
	
	@Test
	public void testParkingABike() {
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		int before = parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE); // -> donne le numero/nombre de place
																			// disponible
		parkingService.processIncomingVehicle();
        Ticket ticket= ticketDAO.getTicket("ABCDEF");
		int after = parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE);
		assertEquals(before, after); // -> pour bike : assertEquals(before, after); car il y a deux places libres
											// pouvant etre occupées
		// test that ticket saved in DB
		assertNotNull(ticket);
	}

	@Test
	public void testParkingLotExit() {
		testParkingACar();
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processExitingVehicle();
		Ticket ticket= ticketDAO.getTicket("ABCDEF");
		// verify that fare is savend in DB
		double price= ticket.getPrice();
		assertEquals(1.5, Math.round(price*100)/100.0);
		// verify that outTime is saved in DB
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		Date date= new Date();		
		String outTimebefore= dateFormat.format(date);
		Date outTime=ticket.getOutTime();
		String outTimeAfter= dateFormat.format(outTime);
		assertEquals(outTimebefore, outTimeAfter);
	}
	
	@Test
	public void testReccurringCustomer() {
		testParkingACar();
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		Ticket ticket1= ticketDAO.getTicket("ABCDEF");
		parkingService.processExitingVehicle();		
		int nb= ticketDAO.countVehiculeReg(ticket1.getVehicleRegNumber());
		Ticket ticket= ticketDAO.getTicket("ABCDEF");
		parkingService.processExitingVehicle();
		int nb2= ticketDAO.countVehiculeReg(ticket.getVehicleRegNumber());
		assertEquals(nb, nb2 - 1);
	}

}
