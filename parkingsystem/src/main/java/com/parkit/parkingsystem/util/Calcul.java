package com.parkit.parkingsystem.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class Calcul {

	
public static double  calculCentieme (double d) {
	
    DecimalFormat f = new DecimalFormat("##.00");
    NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
    Number number = null;
    try {
    	number = format.parse(f.format(d));
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	return number.doubleValue();		
	
}
	
	
}
