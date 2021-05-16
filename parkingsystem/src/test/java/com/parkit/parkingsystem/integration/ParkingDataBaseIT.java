package com.parkit.parkingsystem.integration;

import static org.mockito.Mockito.*;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.Calcul;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;

@ExtendWith(MockitoExtension.class)

public class ParkingDataBaseIT {
	public static FareCalculatorService fareCalculatorService = new FareCalculatorService();

    public static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO ;
    private static DataBasePrepareService dataBasePrepareService = new DataBasePrepareService();

    @Mock
    private static InputReaderUtil inputReaderUtil;
    
    
    @BeforeAll
    private static void setUp() throws Exception{

        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
    }

    @Rule
   public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.LENIENT);
    
    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    public void testParkingACar(){
    	
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();  // process new vehicle = new ticket
        Ticket ticket =   ticketDAO.getTicket("ABCDEF");      
        assertNotNull(ticket);
		assertEquals(2, parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
            
        //TODO: check that a ticket is actualy saved in DB and Parking table is updated with availability
    }

    @Test
    public void testParkingLotExit(){
        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();
        Ticket ticket =   ticketDAO.getTicket("ABCDEF");
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (80*60*1000));
        Date outTime= new Date();
        outTime.setTime( System.currentTimeMillis()+ (60*60*1000)) ;
        assertNotNull(ticket.getOutTime());
        assertNotNull(ticket.getPrice());
        
        //TODO: check that the fare generated and out time are populated correctly in the database
    }

    @Test
    public void testParkingABike(){
    	
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();  // process new vehicle = new ticket
        Ticket ticket =   ticketDAO.getTicket("ABCDEF");     
        assertNotNull(ticket);
		assertEquals(4, parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE));
            
        //TODO: check that a ticket is actualy saved in DB and Parking table is updated with availability
    }

    @Test
    public void testParkingLotExitBike(){
     //   testParkingABike();
    	
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        parkingService.processExitingVehicle();
        Ticket ticket =   ticketDAO.getTicket("ABCDEF");
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (80*60*1000));
        Date outTime= new Date();
        outTime.setTime(System.currentTimeMillis()+ (60*60*1000));
        assertNotNull(ticket.getOutTime());
        assertNotNull(ticket.getPrice());
        
        //TODO: check that the fare generated and out time are populated correctly in the database
    }
    
    
    @Test
    public void testParkingDiscount() {
    	ParkingService parkingService = spy(new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO));
    	Date date = new Date();
        Calendar c = Calendar.getInstance(); 
        c.setTime(date); 
        c.add(Calendar.HOUR, 2);
        date = c.getTime();
   when(parkingService.returnCurrentDate()).thenReturn(date);
    	
         parkingService.processIncomingVehicle();
         parkingService.processExitingVehicle();
         parkingService.processIncomingVehicle();
         parkingService.processExitingVehicle();
         Ticket ticket =   ticketDAO.getTicket("ABCDEF");
        assertEquals( Calcul.calculCentieme(Fare.DISCOUNT_RATE * Fare.CAR_RATE_PER_HOUR),2* ticket.getPrice()); // on doit annuler la demi heure gratuite a cause de l'utilisation du calendrier en int juste pour le test 
        }
        
    	
    	
    }
    
