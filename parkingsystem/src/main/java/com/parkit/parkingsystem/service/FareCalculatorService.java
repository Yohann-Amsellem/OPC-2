package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.Calcul;

public class FareCalculatorService {

	public void calculateFare(Ticket ticket, boolean discount){
		
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
		if (duration<=0) {
			ticket.setPrice(0);
		}
		else {
			double discountRate = 1 ;
			if (discount) { discountRate = Fare.DISCOUNT_RATE ;} 
			switch (ticket.getParkingSpot().getParkingType()){
			case CAR:
				ticket.setPrice(Calcul.calculCentieme(duration * Fare.CAR_RATE_PER_HOUR * discountRate));
				break;
			case BIKE: 
				ticket.setPrice(Calcul.calculCentieme(duration * Fare.BIKE_RATE_PER_HOUR * discountRate));
				break;
			default: throw new IllegalArgumentException("Unkown Parking Type");
			}
		}
	}
}