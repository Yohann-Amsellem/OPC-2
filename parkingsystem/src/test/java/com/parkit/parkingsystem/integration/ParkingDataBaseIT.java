package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.ParkingServiceTest;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
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
import static org.mockito.Mockito.when;

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
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
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
        ParkingService.processIncomingVehicle();
        Ticket ticket = new Ticket();
        TicketDAO.updateTicket(ticket);
     //   assertEquals(,);
            
        //TODO: check that a ticket is actualy saved in DB and Parking table is updated with availability
    }

    @Test
    public void testParkingLotExit(){
    
        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        Ticket ticket = new Ticket();
        parkingService.processExitingVehicle();
        TicketDAO.updateTicket(ticket);
        double price = 2*Fare.CAR_RATE_PER_HOUR ;
        Date outTime = new Date();
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  2 * 60 * 60 * 1000) );
        ticket.setOutTime(outTime);
        ticket.setPrice(price);
        assertEquals(outTime,ticket.getOutTime());
        assertEquals(price,ticket.getPrice());
        //TODO: check that the fare generated and out time are populated correctly in the database
    }

    
    @Test
    public void testParkingDiscount() {
    //TODO: Entrée et sortie d'un véhicule deux fois , et vérifiez avec un assert qu'a la deuxieme fois il a une remise de 5% en tant que client régulier.
    	ParkingServiceTest.setUpPerTest() ;
    	Ticket ticket = new Ticket();
    	ParkingService.processIncomingVehicle();
    	ParkingServiceTest.processExitingVehicleTest();
        TicketDAO.updateTicket(ticket);
        ParkingService.processIncomingVehicle();
    	ParkingServiceTest.processExitingVehicleTest();
        TicketDAO.updateTicket(ticket); 
        FareCalculatorService.calculateFare(ticket, false);
         // assertEquals( discount* Fare.CAR_RATE_PER_HOUR, ticket.getPrice());   // A CORRIGER
    }
    	
    	
    }
    
