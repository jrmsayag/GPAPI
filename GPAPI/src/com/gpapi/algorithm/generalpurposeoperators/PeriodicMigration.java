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
 * PeriodicMigration.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.algorithm.generalpurposeoperators;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadLocalRandom;

import com.gpapi.Population;
import com.gpapi.algorithm.naturalselectionstrategies.NaturalSelectionStrategyInterface;
import com.gpapi.individuals.EvolvedIndividual;



/**
 * 
 * A periodic migration scheme, with a circular topology established between 
 * the populations.
 * 
 * @author sayag
 *
 */

public final class PeriodicMigration implements GeneralPurposeOperatorInterface {
	
	private final int epochLength;
	
	private final double migrationRate;
	
	private final NaturalSelectionStrategyInterface migrantsSelectionStrategy;
	
	
	
	public PeriodicMigration(int epochLength, double migrationRate, NaturalSelectionStrategyInterface migrantsSelectionStrategy){
		if(epochLength < 1)
			throw new IllegalArgumentException("epochLength must be strictly greater than 0!");
		else if(migrationRate < 0.0 || migrationRate > 1.0)
			throw new IllegalArgumentException("migrationRate must be between 0.0 and 1.0!");
		else if(migrantsSelectionStrategy == null)
			throw new NullPointerException("A migrants selection strategy must be specified!");
		
		this.epochLength = epochLength;
		this.migrationRate = migrationRate;
		this.migrantsSelectionStrategy = migrantsSelectionStrategy;
	}
	
	
	@Override
	/**
	 * TODO : Description.
	 * <p>
	 * Note that when the fitness function is not a local one, calling the method 
	 * isFitnessReady() on a population that received or emitted migrants returns 
	 * false.
	 */
	public final void perform(List<Population> populations, int generation, boolean fitnessFunctionIsLocal, ExecutorService executor) throws InterruptedException, ExecutionException {
		if(generation % epochLength != 0)
			return;
		
		HashMap<Integer,Integer> migrantsCountPerPopulation = buildMigrantsCountPerPopulation(populations);
		HashMap<Integer,Set<EvolvedIndividual>> migrantsPerPopulation = buildMigrantsPerPopulation(migrantsCountPerPopulation, populations, executor);
		
		for(Entry<Integer,Set<EvolvedIndividual>> entry : migrantsPerPopulation.entrySet())
			migrate(entry.getValue(), entry.getKey(), populations, fitnessFunctionIsLocal);
	}
	
	
	private final HashMap<Integer,Integer> buildMigrantsCountPerPopulation(List<Population> populations){
		int totalSize = buildTotalSize(populations);
		
		HashMap<Integer,Integer> migrantsCountPerPopulation = new HashMap<Integer,Integer>();
		
		ThreadLocalRandom generator = ThreadLocalRandom.current();
		for(int i = 0; i < migrationRate*totalSize; i++){
			int currentIndex = 0;
			int migrantIndex = generator.nextInt(totalSize);
			for(int j = 0; j < populations.size(); j++){
				currentIndex += populations.get(j).size();
				if(migrantIndex < currentIndex){
					if(!migrantsCountPerPopulation.containsKey(j))
						migrantsCountPerPopulation.put(j, 1);
					else
						migrantsCountPerPopulation.put(j, migrantsCountPerPopulation.get(j)+1);
					break;
				}
			}
		}
		
		return migrantsCountPerPopulation;
	}
	private final int buildTotalSize(List<Population> populations){
		int totalSize = 0;
		for(Population population : populations)
			totalSize += population.size();
		return totalSize;
	}
	
	
	private final HashMap<Integer,Set<EvolvedIndividual>> buildMigrantsPerPopulation(
			HashMap<Integer,Integer> migrantsCountPerPopulation, 
			List<Population> populations, 
			ExecutorService executor) throws InterruptedException, ExecutionException {
		HashMap<Integer,Set<EvolvedIndividual>> migrantsPerPopulation = new HashMap<Integer,Set<EvolvedIndividual>>();
		for(Entry<Integer,Integer> entry : migrantsCountPerPopulation.entrySet())
			migrantsPerPopulation.put(entry.getKey(), migrantsSelectionStrategy.select(populations.get(entry.getKey()), entry.getValue(), executor).keySet());
		return migrantsPerPopulation;
	}
	
	
	private final void migrate(Set<EvolvedIndividual> migrants, int sourcePopulationIndex, List<Population> populations, boolean keepFitness){
		Population sourcePopulation = populations.get(sourcePopulationIndex);
		
		sourcePopulation.removeAll(migrants);
		if(!sourcePopulation.isEmpty() && !keepFitness)
			sourcePopulation.get(0).setRawFitnessType(null);
		
		for(EvolvedIndividual migrant : migrants){
			if(!keepFitness)
				migrant.setRawFitnessType(null);
			populations.get(chooseNeighbour(sourcePopulationIndex, populations.size())).add(migrant);
		}
	}
	private final int chooseNeighbour(int currentPopulationIndex, int populationsSize){
		if(ThreadLocalRandom.current().nextBoolean())
			return (currentPopulationIndex + 1) % populationsSize;
		else
			return currentPopulationIndex == 0 ? populationsSize - 1 : currentPopulationIndex - 1;
	}
}
