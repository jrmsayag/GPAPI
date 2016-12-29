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
 * FitnessFunctionInterface.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.algorithm.fitnessfunction;

import javax.swing.JPanel;

import com.gpapi.individuals.EvolvedIndividual;
import com.gpapi.individuals.EvolvedIndividual.RawFitnessType;



public interface FitnessFunctionInterface {
	
	public JPanel getPhenotypeView(EvolvedIndividual individual);
	
	
	public default void modulateWithExecutionCost(EvolvedIndividual individual, double coeff){
		RawFitnessType fitnessType = individual.getRawFitnessType();
		
		if(fitnessType == null)
			throw new NullPointerException("The fitness must be set!");
		
		double newFitness;
		if(fitnessType.equals(RawFitnessType.Score))
			newFitness = individual.getRawFitness() - coeff * individual.getLastExecutionCost();
		else
			newFitness = individual.getRawFitness() + coeff * individual.getLastExecutionCost();
		individual.setRawFitness(newFitness);
	}
	
	public default void modulateWithGenomeSize(EvolvedIndividual individual, double coeff){
		RawFitnessType fitnessType = individual.getRawFitnessType();
		
		if(fitnessType == null)
			throw new NullPointerException("The fitness must be set!");
		
		double newFitness;
		if(fitnessType.equals(RawFitnessType.Score))
			newFitness = individual.getRawFitness() - coeff * individual.getEggCell().getNucleus().getTotalSize();
		else
			newFitness = individual.getRawFitness() + coeff * individual.getEggCell().getNucleus().getTotalSize();
		individual.setRawFitness(newFitness);
	}
}
