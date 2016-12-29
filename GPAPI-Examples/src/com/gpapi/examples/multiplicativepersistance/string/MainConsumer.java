/* ===============================================================
 * GPAPI : A Genetic-Programming library for the Java(tm) platform
 * ===============================================================
 *
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Project Info:  https://github.com/jrmsayag/gpapi
 * 
 * This file is part of GPAPI.
 *
 * This library is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library. If not, see http://www.gnu.org/licenses/.
 *
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 * Other names may be trademarks of their respective owners.]
 *
 * ---------------
 * MainConsumer.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.examples.multiplicativepersistance.string;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import com.gpapi.DataManager;
import com.gpapi.individuals.EvolvedIndividual;
import com.gpapi.individuals.cells.nucleuses.types.RealValue;



public class MainConsumer {

	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException, InvocationTargetException, InterruptedException {
		EvolvedIndividual individual = DataManager.loadIndividual("multiplicativepersistance/best_string.ser");
		DigitsList phenotype = (DigitsList) individual.executeUnconditionallySameArgs().get(0);
		
		
		
		System.out.println("Persistance :  " + FitnessFunction.multiplicativePersistance(FitnessFunction.interpret(phenotype.getValue())));
		
		int contributions = 0;
		for(RealValue rawDigit : phenotype.getValue()){
			if(FitnessFunction.digit(rawDigit) > 1)
				contributions++;
		}
		System.out.println("Contributions :  " + contributions);
		
		String s = "";
		for(RealValue rawDigit : phenotype.getValue())
			s = s + FitnessFunction.digit(rawDigit) + ",";
		System.out.println("Raw value :  " + s);
		
		System.out.println("Value :  " + FitnessFunction.interpret(phenotype.getValue()));
	}
}
