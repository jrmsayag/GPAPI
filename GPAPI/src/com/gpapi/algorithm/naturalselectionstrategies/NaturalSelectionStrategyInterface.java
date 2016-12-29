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
 * NaturalSelectionStrategyInterface.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.algorithm.naturalselectionstrategies;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import com.gpapi.Population;
import com.gpapi.individuals.EvolvedIndividual;



/**
 * 
 * Classes implementing this interface define a method to select some individuals
 * within a population.
 * 
 * @author jeremy
 *
 */
public interface NaturalSelectionStrategyInterface {
	
	/**
	 * Selects a given number of individuals, possibly with multiplicity, from the given
	 * population. Multiplicity here means that the selected individuals can be counted 
	 * multiple times, with the matching values in the returned map indicating how many 
	 * times each individual is to be counted.
	 * 
	 * @param population
	 * 			A non-null reference to the population in which individuals are to be
	 * 			selected.
	 * @param n
	 * 			The total number of individuals that are to be selected from the given
	 * 			population, with multiplicity (i.e. each key appearing in the returned
	 * 			map amounts to a number of individuals equal to the corresponding value).
	 * 			This parameter must be positive, and must be exactly zero if the given
	 * 			population is empty.
	 * @param executor
	 * 			A possibly null reference to an executor service which, if not null, 
	 * 			can be used in order to parallelize the computations. If it is used, 
	 * 			implementations must ensure that the tasks submitted all are finished 
	 * 			or cancelled before returning.
	 * @return
	 * 			A modifiable map with keys indicating the unique individuals selected
	 * 			and values indicating how many times each one is to be counted.
	 * @throws InterruptedException
	 * 			Thrown if the thread from which this method is called is interrupted 
	 * 			and the implementing class decides to throw the exception. This should 
	 * 			be the case if the strategy's computation is heavy, in order not to 
	 * 			introduce a delay before the application can be effectively stopped.
	 * @throws ExecutionException 
	 * 			Thrown if one of the tasks submitted to the given executor terminated
	 * 			by throwing an exception.
	 */
	public Map<EvolvedIndividual,Integer> select(Population population, int n, ExecutorService executor) throws InterruptedException, ExecutionException;
	
	/**
	 * Convenient method that provides a shorter way to select only one individual in
	 * the given population using this strategy. This permits to avoid using the default
	 * select() method and then pick the only individual in the returned map.
	 * <p>
	 * Implementing classes may optionally overwrite the default implementation in
	 * order to provide an optimized implementation when it is known in advance that
	 * only one individual is needed.
	 * 
	 * @param population
	 * @param executor
	 * @return
	 * @throws InterruptedException
	 */
	public default EvolvedIndividual selectOne(Population population) throws InterruptedException {
		try {
			for(EvolvedIndividual result : select(population, 1, null).keySet())
				return result;
		} catch (ExecutionException e) {
			// Will never happen since no executor is given to select().
		}
		return null;
	}
}
