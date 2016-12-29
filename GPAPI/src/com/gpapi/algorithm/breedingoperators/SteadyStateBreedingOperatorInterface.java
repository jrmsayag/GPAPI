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
 * SteadyStateBreedingOperatorInterface.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.algorithm.breedingoperators;

import com.gpapi.Population;
import com.gpapi.algorithm.fitnessfunction.LocalFitnessFunctionInterface;
import com.gpapi.individuals.EvolvedIndividual;



/**
 * 
 * Classes implementing this interface define an operator that performs one mating and
 * reproduction operation among individuals of a given population, in the context of a
 * steady state algorithm.
 * <p>
 * Note : Because classes implementing this interface are likely to be used concurrently
 * by algorithms, care should be taken to synchronize on individuals that are used in
 * the breeding process, in order to avoid memory consistency effects if one individual
 * is used concurrently in several reproductions.
 * 
 * @author jeremy
 *
 */
public interface SteadyStateBreedingOperatorInterface {
	
	/**
	 * Performs one mating and reproduction operation between individuals of the given 
	 * population and returns the newly created individual.
	 * <p>
	 * The newly created individual must be submitted to the fitness function before
	 * being returned, in order to have its fitness set.
	 * <p>
	 * This method doesn't modify the given population.
	 * 
	 * @param population
	 * 			A non-null reference to the population that is to be bred.
	 * @param generation
	 * 			The current generation. As defined in the AbstractAlgorithm class, the 
	 * 			generation count always starts at one.
	 * @param fitnessFunction
	 * 			A non-null reference to the function that is used to evaluate individuals'
	 * 			fitness.
	 * @throws InterruptedException
	 * 			Thrown if the thread from which this method is called is interrupted and the
	 * 			implementing class decides to throw the exception. This should be the case if
	 * 			the operator's computation is heavy, in order not to introduce a delay before 
	 * 			the application can be effectively stopped.
	 */
	public EvolvedIndividual breed(Population population, int generation, LocalFitnessFunctionInterface fitnessFunction) throws InterruptedException;
	
}
