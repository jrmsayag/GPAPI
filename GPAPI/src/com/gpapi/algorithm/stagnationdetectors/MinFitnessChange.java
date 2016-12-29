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
 * MinFitnessChange.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.algorithm.stagnationdetectors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import com.gpapi.GenerationSnapshot;
import com.gpapi.Population;



/**
 * 
 * A stagnation detector according to which a population is stagnating when the 
 * average change amplitude in fitness of its best individual over nIntervals 
 * intervals of intervalsLength generations hasn't exceeded stagnationThreshold 
 * during at least maxIntervalsStagnating*intervalsLength generations.
 * <p>
 * Note : Despite this observer will receive a GenerationSnapshot object through
 * its update() method every generation, the list of stagnating populations will
 * effectively be refreshed every intervalsLength generations.
 * 
 * @author jeremy
 *
 */

public final class MinFitnessChange implements StagnationDetectorInterface {
	
	private final int nIntervals;
	
	private final int maxIntervalsStagnating;
	
	private final double stagnationThreshold;
	
	private final int intervalsLength;
	
	private final HashMap<Population,LinkedList<Double>> bestFitnessHistories = new HashMap<Population,LinkedList<Double>>();
	
	private final HashMap<Population,Integer> lastSufficientChangeDates = new HashMap<Population,Integer>();
	
	private final HashSet<Population> allStagnatingPopulations = new HashSet<Population>();
	
	
	
	public MinFitnessChange(int nIntervals, int maxIntervalsStagnating, double stagnationThreshold, int intervalsLength){
		if(nIntervals < 1)
			throw new IllegalArgumentException("nIntervals must be strictly positive!");
		else if(maxIntervalsStagnating < 1)
			throw new IllegalArgumentException("maxIntervalsStagnating must be strictly positive!");
		else if(stagnationThreshold < 0.0)
			throw new IllegalArgumentException("stagnationThreshold must be positive!");
		else if(intervalsLength < 1)
			throw new IllegalArgumentException("intervalsLength must be strictly positive!");
		
		this.nIntervals = nIntervals;
		this.maxIntervalsStagnating = maxIntervalsStagnating;
		this.stagnationThreshold = stagnationThreshold;
		this.intervalsLength = intervalsLength;
	}
	public MinFitnessChange(int nIntervals, int maxDuration, double minChange){
		this(nIntervals, maxDuration, minChange, 1);
	}
	
	
	@Override
	public final HashSet<Population> getStagnatingPopulationsAmong(List<Population> populations) {
		HashSet<Population> stagnatingPopulations = new HashSet<Population>();
		for(Population population : populations){
			if(allStagnatingPopulations.contains(population))
				stagnatingPopulations.add(population);
		}
		return stagnatingPopulations;
	}
	
	
	@Override
	public final void update(Observable observable, Object arg) {
		if(arg instanceof GenerationSnapshot){
			GenerationSnapshot snapshot = (GenerationSnapshot) arg;
			if((snapshot.getGeneration()-1) % intervalsLength == 0)
				updateCurrentlyStagnatingPopulations(snapshot.getPopulations(), snapshot.getGeneration());
		}
	}
	private final void updateCurrentlyStagnatingPopulations(List<Population> populations, int generation){
		for(Population population : populations)
			updateHistoryFor(population, generation);
		
		allStagnatingPopulations.clear();
		for(Population population : populations){
			if(generation - lastSufficientChangeDates.get(population) >= maxIntervalsStagnating*intervalsLength)
				allStagnatingPopulations.add(population);
		}
	}
	private final void updateHistoryFor(Population population, int generation){
		LinkedList<Double> bestFitnessHistory = updateBestFitnessHistoryFor(population, generation);
		
		double averageChange;
		if(bestFitnessHistory.size() < nIntervals+1)
			averageChange = stagnationThreshold;
		else
			averageChange = computeAverageChange(bestFitnessHistory);
		
		if(averageChange >= stagnationThreshold)
			lastSufficientChangeDates.put(population, generation);
	}
	private final LinkedList<Double> updateBestFitnessHistoryFor(Population population, int generation){
		population.sort();
		
		LinkedList<Double> bestFitnessHistory = bestFitnessHistories.get(population);
		
		if(bestFitnessHistory == null){
			bestFitnessHistory = new LinkedList<Double>();
			bestFitnessHistories.put(population, bestFitnessHistory);
		}
		
		bestFitnessHistory.addFirst(population.get(0).getRawFitness());
		
		if(bestFitnessHistory.size() > nIntervals+1)
			bestFitnessHistory.removeLast();
		
		return bestFitnessHistory;
	}
	
	
	private final double computeAverageChange(LinkedList<Double> bestFitnessHistory){
		Iterator<Double> bestFitnessIterator = bestFitnessHistory.iterator();
		
		double newValue = bestFitnessIterator.next();
		
		double average = 0.0;
		while(bestFitnessIterator.hasNext()){
			double oldValue = bestFitnessIterator.next();
			average += Math.abs(newValue - oldValue);
			newValue = oldValue;
		}
		
		return average / (bestFitnessHistory.size() - 1);
	}
}
