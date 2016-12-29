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
 * Recycling.java
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import com.gpapi.Population;
import com.gpapi.algorithm.stagnationdetectors.StagnationDetectorInterface;
import com.gpapi.individuals.EvolvedIndividual;



public final class Recycling implements GeneralPurposeOperatorInterface {
	
	private final int bestPopulationsKept;
	
	private final int minRecyclingInterval;
	
	private final StagnationDetectorInterface stagnationDetector;
	
	private final GeneralPurposeOperatorInterface next;
	
	private final HashMap<Population,Integer> lastRecyclingDates = new HashMap<Population,Integer>();
	
	
	
	public Recycling(
			int bestPopulationsKept, 
			int minRecyclingInterval, 
			StagnationDetectorInterface stagnationDetector, 
			GeneralPurposeOperatorInterface next){
		if(minRecyclingInterval < 1)
			throw new IllegalArgumentException("minRecyclingInterval must be strictly positive!");
		else if(stagnationDetector == null)
			throw new NullPointerException("A stagnation detector must be specified!");
		
		this.bestPopulationsKept = bestPopulationsKept;
		this.minRecyclingInterval = minRecyclingInterval;
		this.stagnationDetector = stagnationDetector;
		this.next = next;
	}
	public Recycling(int bestPopulationsKept, int minRecyclingInterval, StagnationDetectorInterface stagnationDetector){
		this(bestPopulationsKept, minRecyclingInterval, stagnationDetector, null);
	}
	
	
	@Override
	/**
	 * TODO : Description.
	 * <p>
	 * Note : The recycled populations are not passed to the next operators on the 
	 * generation during which they were recycled.
	 * <p>
	 * Note moreover that individuals of recycled populations don't have a fitness
	 * type set, so calling a recycled population's isFitnessReady() method would
	 * return false (which permits to not submit the recycled populations to the
	 * breeding operator before the fitness of their individuals has been evaluated).
	 */
	public void perform(List<Population> populations, int generation, boolean fitnessFunctionIsLocal, ExecutorService executor) throws InterruptedException, ExecutionException {
		Set<Population> populationsToRecycle = stagnationDetector.getStagnatingPopulationsAmong(populations);
		
		removeBestPopulations(populationsToRecycle, populations);
		
		for(Iterator<Population> it = populationsToRecycle.iterator(); it.hasNext(); ){
			Population population = it.next();
			if(generation - lastRecyclingDates.getOrDefault(population, 0) >= minRecyclingInterval){
				recycle(population);
				lastRecyclingDates.put(population, generation);
			} else
				it.remove();
		}
		
		if(next != null){
			ArrayList<Population> populationsNotRecycled = new ArrayList<Population>(populations.size() - populationsToRecycle.size());
			for(Population population : populations){
				if(!populationsToRecycle.contains(population))
					populationsNotRecycled.add(population);
			}
			next.perform(populationsNotRecycled, generation, fitnessFunctionIsLocal, executor);
		}
	}
	
	
	private final void removeBestPopulations(Set<Population> populationsToRecycle, List<Population> allPopulations){
		HashSet<Population> remainingPopulations = new HashSet<Population>(allPopulations);
		
		for(Population population : remainingPopulations)
			population.sort();
		
		for(int i = 0; i < bestPopulationsKept && !populationsToRecycle.isEmpty(); i++){
			Population bestPopulation = null;
			for(Population population : remainingPopulations){
				if(bestPopulation == null || population.get(0).compareTo(bestPopulation.get(0)) < 0)
					bestPopulation = population;
			}
			remainingPopulations.remove(bestPopulation);
			populationsToRecycle.remove(bestPopulation);
		}
	}
	
	
	private final void recycle(Population population) throws InterruptedException {
		EvolvedIndividual generator = population.get(0);
		population.clear();
		
		for(int i = 0; i < population.getTargetSize(); i++){
			if(Thread.interrupted())
				throw new InterruptedException();
			else
				population.add(generator.generateNew());
		}
	}
}
