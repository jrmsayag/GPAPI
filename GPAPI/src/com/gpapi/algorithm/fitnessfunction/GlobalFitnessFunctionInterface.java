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
 * GlobalFitnessFunctionInterface.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.algorithm.fitnessfunction;

import com.gpapi.Population;
import com.gpapi.individuals.EvolvedIndividual;



public interface GlobalFitnessFunctionInterface extends FitnessFunctionInterface {
	
	/**
	 * TODO : Description.
	 * 
	 * Note : Implementations of this method should be thread-safe, and not
	 * synchronized in order to take advantage of the Algorithm's built in
	 * parallelism.
	 * 
	 * @param population
	 * @param generation
	 */
	public void applyTo(Population population, int generation) throws InterruptedException;
	
	
	
	public default void modulateWithExecutionCost(Population population, double coeff){
		for(EvolvedIndividual individual : population)
			modulateWithExecutionCost(individual, coeff);
	}
	
	public default void modulateWithGenomeSize(Population population, double coeff){
		for(EvolvedIndividual individual : population)
			modulateWithGenomeSize(individual, coeff);
	}
}
