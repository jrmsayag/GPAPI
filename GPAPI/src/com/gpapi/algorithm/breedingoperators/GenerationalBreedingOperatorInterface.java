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
 * GenerationalBreedingOperatorInterface.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.algorithm.breedingoperators;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import com.gpapi.Population;
import com.gpapi.algorithm.fitnessfunction.FitnessFunctionInterface;



/**
 * 
 * Classes implementing this interface define an operator that performs the mating and
 * reproduction parts of the transformation of a population into the next generation
 * population, in the context of a generational algorithm.
 * <p>
 * Note : Because classes implementing this interface are likely to be used concurrently
 * by algorithms, care should be taken to synchronize on individuals that are used in
 * the breeding process, in order to avoid memory consistency effects if one individual
 * is used concurrently in several reproductions.
 * 
 * @author jeremy
 *
 */
public interface GenerationalBreedingOperatorInterface {
	
	/**
	 * Performs mating and reproduction between individuals of the given population in
	 * order to build the next generation of this population.
	 * <p>
	 * This method is given a non-null reference to the fitness function, which can be 
	 * used to perform selection among the offspring based on new individuals' fitness 
	 * (individuals of the input population all have their fitness already set), but it 
	 * is not specified that individuals in the population after execution of this method 
	 * must have their fitness set, since the whole new population is to be submitted to 
	 * the fitness function by the algorithm (because it is a generational one).
	 * 
	 * @param population
	 * 			A non-null reference to the population that is to be bred.
	 * @param generation
	 * 			The current generation. As defined in the AbstractAlgorithm class, the 
	 * 			generation count always starts at one.
	 * @param fitnessFunction
	 * 			A non-null reference to the function that is used to evaluate individual's
	 * 			fitness.
	 * @param executor
	 * 			An non-null reference to an executor service which can be used in order 
	 * 			to parallelize the computations. If it is used, implementations must 
	 * 			ensure that the tasks submitted all are finished or cancelled before 
	 * 			returning.
	 * @throws InterruptedException
	 * 			Thrown if the thread from which this method is called is interrupted and the
	 * 			implementing class decides to throw the exception. This should be the case if
	 * 			the operator's computation is heavy, in order not to introduce a delay before 
	 * 			the application can be effectively stopped.
	 * @throws ExecutionException 
	 * 			Can be thrown if one of the tasks submitted to the given executor terminated
	 * 			by throwing an exception.
	 */
	public void breed(Population population, int generation, FitnessFunctionInterface fitnessFunction, ExecutorService executor) throws InterruptedException, ExecutionException;
	
}
