package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }
        //On change le type en double pour gérer les cas limite (en millisecondes)
        double inHour = ticket.getInTime().getTime();
        double outHour = ticket.getOutTime().getTime();

      // On oublie pas la conversion en heures a la fin de la ligne
        double duration = (outHour - inHour)/(60*60*1000);
        // duration = 0.5  correspond au test des 30 premières minutes gratuites.
        
        if (duration<0.5) {
    		ticket.setPrice(0);
    	}
        else {
        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
            	
            		ticket.setPrice((duration * Fare.CAR_RATE_PER_HOUR)- (0.5 * Fare.CAR_RATE_PER_HOUR));
            		}
                break;
            
            case BIKE: {
            	
            	ticket.setPrice((duration * Fare.BIKE_RATE_PER_HOUR)- (0.5 * Fare.BIKE_RATE_PER_HOUR));
            		
            		break;
            	
            }
            
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
    }
}