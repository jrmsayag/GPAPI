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
 * Migration.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.algorithm.generalpurposeoperators;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import com.gpapi.Population;
import com.gpapi.algorithm.naturalselectionstrategies.NaturalSelectionStrategyInterface;
import com.gpapi.algorithm.stagnationdetectors.StagnationDetectorInterface;
import com.gpapi.individuals.EvolvedIndividual;



public final class Migration implements GeneralPurposeOperatorInterface {
	
	private final double migrationRate;
	
	private final int minImmigrationWavesInterval;
	
	private final NaturalSelectionStrategyInterface immigrantsSelectionStrategy;
	
	private final NaturalSelectionStrategyInterface emigrantsSelectionStrategy;
	
	private final StagnationDetectorInterface stagnationDetector;
	
	private final HashMap<Population,Integer> lastImmigrationWaveDates = new HashMap<Population,Integer>();
	
	
	
	public Migration(
			double migrationRate, 
			int minImmigrationWavesInterval, 
			NaturalSelectionStrategyInterface immigrantsSelectionStrategy, 
			NaturalSelectionStrategyInterface emigrantsSelectionStrategy, 
			StagnationDetectorInterface stagnationDetector){
		if(migrationRate < 0.0 || migrationRate > 1.0)
			throw new IllegalArgumentException("migrationRate must be between 0.0 and 1.0!");
		else if(minImmigrationWavesInterval < 1)
			throw new IllegalArgumentException("minImmigrationWavesInterval must be strictly positive!");
		else if(immigrantsSelectionStrategy == null)
			throw new NullPointerException("An immigrants selection strategy must be specified!");
		else if(emigrantsSelectionStrategy == null)
			throw new NullPointerException("An emigrants selection strategy must be specified!");
		else if(stagnationDetector == null)
			throw new NullPointerException("A stagnation detector must be specified!");
		
		this.migrationRate = migrationRate;
		this.minImmigrationWavesInterval = minImmigrationWavesInterval;
		this.immigrantsSelectionStrategy = immigrantsSelectionStrategy;
		this.emigrantsSelectionStrategy = emigrantsSelectionStrategy;
		this.stagnationDetector = stagnationDetector;
	}
	
	
	@Override
	/**
	 * TODO : Description.
	 * <p>
	 * Note that immigrants only keep a fitness type set if the fitness function 
	 * is a local one.
	 */
	public final void perform(List<Population> populations, int generation, boolean fitnessFunctionIsLocal, ExecutorService executor) throws InterruptedException, ExecutionException {
		Set<Population> stagnatingPopulations = stagnationDetector.getStagnatingPopulationsAmong(populations);
		
		for(Iterator<Population> it = stagnatingPopulations.iterator(); it.hasNext(); ){
			Population stagnatingPopulation = it.next();
			if(generation - lastImmigrationWaveDates.getOrDefault(stagnatingPopulation, 0) < minImmigrationWavesInterval)
				it.remove();
			else
				lastImmigrationWaveDates.put(stagnatingPopulation, generation);
		}
		
		HashMap<Population,HashSet<EvolvedIndividual>> immigrantsPerPopulation = 
				buildImmigrantsPerPopulation(stagnatingPopulations, populations, fitnessFunctionIsLocal, executor);
		
		for(Entry<Population,HashSet<EvolvedIndividual>> immigrants : immigrantsPerPopulation.entrySet())
			migrate(immigrants.getValue(), immigrants.getKey(), executor);
	}
	
	
	private final HashMap<Population,HashSet<EvolvedIndividual>> buildImmigrantsPerPopulation(
			Set<Population> stagnatingPopulations, 
			List<Population> allPopulations, 
			boolean keepFitness, 
			ExecutorService executor) throws InterruptedException, ExecutionException {
		int totalSize = buildTotalSize(allPopulations);
		
		HashMap<Population,HashSet<EvolvedIndividual>> immigrantsPerPopulation = new HashMap<Population,HashSet<EvolvedIndividual>>();
		for(Population stagnatingPopulation : stagnatingPopulations){
			int nImmigrants = (int) (migrationRate * stagnatingPopulation.size());
			immigrantsPerPopulation.put(
					stagnatingPopulation, 
					buildImmigrants(stagnatingPopulation, allPopulations, nImmigrants, totalSize, keepFitness, executor));
		}
		
		return immigrantsPerPopulation;
	}
	private final int buildTotalSize(List<Population> allPopulations){
		int totalSize = 0;
		for(Population population : allPopulations)
			totalSize += population.size();
		return totalSize;
	}
	private final HashSet<EvolvedIndividual> buildImmigrants(
			Population stagnatingPopulation, 
			List<Population> allPopulations, 
			int nImmigrants, 
			int totalSize, 
			boolean keepFitness, 
			ExecutorService executor) throws InterruptedException, ExecutionException {
		HashSet<Population> otherPopulations = new HashSet<Population>(allPopulations);
		otherPopulations.remove(stagnatingPopulation);
		double otherPopulationsTotalSize = totalSize - stagnatingPopulation.size();
		
		HashSet<EvolvedIndividual> immigrants = new HashSet<EvolvedIndividual>();
		for(Population otherPopulation : otherPopulations){
			int nImmigrantsFromOtherPopulation = (int) (nImmigrants * otherPopulation.size() / otherPopulationsTotalSize);
			for(EvolvedIndividual immigrant : immigrantsSelectionStrategy.select(otherPopulation, nImmigrantsFromOtherPopulation, executor).keySet()){
				if(Thread.interrupted())
					throw new InterruptedException();
				else
					immigrants.add(immigrant.copy(keepFitness));
			}
		}
		
		return immigrants;
	}
	
	
	private final void migrate(HashSet<EvolvedIndividual> immigrants, Population destinationPopulation, ExecutorService executor) throws InterruptedException, ExecutionException {
		destinationPopulation.removeAll(emigrantsSelectionStrategy.select(destinationPopulation, immigrants.size(), executor).keySet());
		destinationPopulation.addAll(immigrants);
	}
}
