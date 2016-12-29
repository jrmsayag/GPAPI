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
 * GreedyOverSelection.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.algorithm.naturalselectionstrategies;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import com.gpapi.Population;
import com.gpapi.individuals.EvolvedIndividual;



public final class GreedyOverSelection implements NaturalSelectionStrategyInterface {
	
	private final NaturalSelectionStrategyInterface baseStrategy;
	
	private final double alphaPopulation;
	
	private final double alphaSampling;
	
	
	
	public GreedyOverSelection(
			NaturalSelectionStrategyInterface baseStrategy, 
			double alphaPopulation, 
			double alphaSampling){
		if(baseStrategy == null)
			throw new NullPointerException("A base strategy must be specified!");
		else if(alphaPopulation <= 0.0)
			throw new IllegalArgumentException("alphaPopulation must be strictly greater than 0.0!");
		else if(alphaPopulation >= 1.0)
			throw new IllegalArgumentException("alphaPopulation must be strictly lower than 1.0!");
		else if (alphaSampling < 0.0 || alphaSampling > 1.0)
			throw new IllegalArgumentException("alphaSampling must be between 0.0 and 1.0!");
		
		this.baseStrategy = baseStrategy;
		this.alphaPopulation = alphaPopulation;
		this.alphaSampling = alphaSampling;
	}

	@Override
	public final Map<EvolvedIndividual,Integer> select(Population population, int n, ExecutorService executor) throws InterruptedException, ExecutionException {
		if(n <= 0)
			return new HashMap<EvolvedIndividual,Integer>();
		else if(population.isEmpty())
			throw new IllegalArgumentException("n is positive but the population is empty!");
		
		int firstPartLimitIndex = (int) (alphaPopulation * population.size());
		int firstPartSelectionSize = (int) (alphaSampling * n);
		
		population.sort();
		
		Map<EvolvedIndividual,Integer> firstPartSelection = subSelect(
				population, 0, firstPartLimitIndex, firstPartSelectionSize, executor);
		Map<EvolvedIndividual,Integer> secondPartSelection = subSelect(
				population, firstPartLimitIndex, population.size(), population.getTargetSize() - firstPartSelectionSize, executor);
		
		firstPartSelection.putAll(secondPartSelection);
		
		return firstPartSelection;
	}
	
	private final Map<EvolvedIndividual,Integer> subSelect(
			Population population, 
			int beginIndex, 
			int endIndex, 
			int subTargetSize, 
			ExecutorService executor) throws InterruptedException, ExecutionException {
		Population subPopulation = new Population();
		for(int i = beginIndex; i < endIndex; i++)
			subPopulation.add(population.get(i));
		subPopulation.setTargetSize(subTargetSize);
		return baseStrategy.select(subPopulation, subPopulation.getTargetSize(), executor);
	}
}
