package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	public static void calculateFare(Ticket ticket, boolean discount){
		if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
			throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
		}
		//On change le type en double pour gérer les cas limite (en millisecondes)
		double inHour = ticket.getInTime().getTime();
		double outHour = ticket.getOutTime().getTime();

		// On oublie pas la conversion en heures a la fin de la ligne
		double duration = (outHour - inHour)/(60*60*1000);
		// duration = 0.5  correspond au test des 30 premières minutes gratuites.
		duration = duration -0.5;
		if (duration<0) {
			ticket.setPrice(0);
		}
		else {
			double discountRate = 1 ;
			if (discount) discountRate = 0.95 ; 
			switch (ticket.getParkingSpot().getParkingType()){
			case CAR:
				ticket.setPrice((duration * Fare.CAR_RATE_PER_HOUR * discountRate ));
				break;
			case BIKE: 
				ticket.setPrice((duration * Fare.BIKE_RATE_PER_HOUR * discountRate));
				break;
			default: throw new IllegalArgumentException("Unkown Parking Type");
			}
		}
	}
}