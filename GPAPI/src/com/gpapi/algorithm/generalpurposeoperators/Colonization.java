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
 * Colonization.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.algorithm.generalpurposeoperators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import com.gpapi.Population;
import com.gpapi.algorithm.naturalselectionstrategies.NaturalSelectionStrategyInterface;
import com.gpapi.algorithm.stagnationdetectors.StagnationDetectorInterface;
import com.gpapi.individuals.EvolvedIndividual;



public final class Colonization implements GeneralPurposeOperatorInterface {
	
	private final double colonizationRate;
	
	private final int minColonizationInterval;
	
	private final NaturalSelectionStrategyInterface colonizatorsSelectionStrategy;
	
	private final NaturalSelectionStrategyInterface survivorsSelectionStrategy;
	
	private final StagnationDetectorInterface stagnationDetector;
	
	private final HashMap<Population,Integer> lastColonizationDates = new HashMap<Population,Integer>();
	
	
	
	public Colonization(
			double colonizationRate, 
			int minColonizationInterval, 
			NaturalSelectionStrategyInterface colonizatorsSelectionStrategy, 
			NaturalSelectionStrategyInterface survivorsSelectionStrategy, 
			StagnationDetectorInterface stagnationDetector){
		if(colonizationRate < 0.0 || colonizationRate > 1.0)
			throw new IllegalArgumentException("colonizationRate must be between 0.0 and 1.0!");
		else if(minColonizationInterval < 1)
			throw new IllegalArgumentException("minColonizationInterval must be strictly positive!");
		else if(colonizatorsSelectionStrategy == null)
			throw new NullPointerException("A colonizators selection strategy must be specified!");
		else if(survivorsSelectionStrategy == null)
			throw new NullPointerException("A survivors selection strategy must be specified!");
		else if(stagnationDetector == null)
			throw new NullPointerException("A stagnation detector must be specified!");
		
		this.colonizationRate = colonizationRate;
		this.minColonizationInterval = minColonizationInterval;
		this.colonizatorsSelectionStrategy = colonizatorsSelectionStrategy;
		this.survivorsSelectionStrategy = survivorsSelectionStrategy;
		this.stagnationDetector = stagnationDetector;
	}
	
	
	@Override
	/**
	 * TODO : Description.
	 * <p>
	 * Note that individuals of colonized populations only have a fitness type 
	 * set if fitnessFunctionIsLocal is true.
	 */
	public final void perform(List<Population> populations, int generation, boolean fitnessFunctionIsLocal, ExecutorService executor) throws InterruptedException, ExecutionException {
		Set<Population> stagnatingPopulations = stagnationDetector.getStagnatingPopulationsAmong(populations);
		
		Population bestPopulation = findBestPopulation(populations);
		
		stagnatingPopulations.remove(bestPopulation);
		
		for(Population stagnatingPopulation : stagnatingPopulations){
			if(generation - lastColonizationDates.getOrDefault(stagnatingPopulation, 0) >= minColonizationInterval){
				colonize(stagnatingPopulation, bestPopulation, fitnessFunctionIsLocal, executor);
				lastColonizationDates.put(stagnatingPopulation, generation);
			}
		}
	}
	
	
	private final Population findBestPopulation(List<Population> allPopulations){
		Population bestPopulation = null;
		for(Population population : allPopulations){
			population.sort();
			if(bestPopulation == null || population.get(0).compareTo(bestPopulation.get(0)) < 0)
				bestPopulation = population;
		}
		return bestPopulation;
	}
	
	
	private final void colonize(Population colonizedPopulation, Population colonizingPopulation, boolean keepFitness, ExecutorService executor) throws InterruptedException, ExecutionException {
		int nColonizators = (int) (colonizationRate * colonizedPopulation.size());
		
		ArrayList<EvolvedIndividual> newIndividuals = new ArrayList<EvolvedIndividual>(colonizedPopulation.size());
		for(Entry<EvolvedIndividual,Integer> entry : colonizatorsSelectionStrategy.select(colonizingPopulation, nColonizators, executor).entrySet()){
			for(int i = 0; i < entry.getValue(); i++){
				if(Thread.interrupted())
					throw new InterruptedException();
				else
					newIndividuals.add(entry.getKey().copy(keepFitness));
			}
		}
		for(Entry<EvolvedIndividual,Integer> entry : survivorsSelectionStrategy.select(
				colonizedPopulation, colonizedPopulation.size() - nColonizators, executor).entrySet()){
			for(int i = 0; i < entry.getValue(); i++){
				if(Thread.interrupted())
					throw new InterruptedException();
				else
					newIndividuals.add(entry.getKey().copy(keepFitness));
			}
		}
		
		colonizedPopulation.clear();
		colonizedPopulation.addAll(newIndividuals);
	}
}
