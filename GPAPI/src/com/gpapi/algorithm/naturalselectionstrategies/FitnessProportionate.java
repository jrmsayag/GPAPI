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
 * FitnessProportionate.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.algorithm.naturalselectionstrategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadLocalRandom;

import com.gpapi.Population;
import com.gpapi.individuals.EvolvedIndividual;
import com.gpapi.individuals.EvolvedIndividual.RawFitnessType;



public final class FitnessProportionate implements NaturalSelectionStrategyInterface {
	
	private final boolean rankBased;
	
	private final boolean withReplacement;
	
	private final double adjustmentConstant;
	
	
	
	public FitnessProportionate(boolean rankBased, boolean withReplacement, double adjustmentConstant){
		this.rankBased = rankBased;
		this.withReplacement = withReplacement;
		this.adjustmentConstant = adjustmentConstant;
	}
	public FitnessProportionate(boolean rankBased, double adjustmentConstant){
		this(rankBased, true, adjustmentConstant);
	}
	public FitnessProportionate(boolean rankBased, boolean withReplacement){
		this(rankBased, withReplacement, 1.0);
	}
	public FitnessProportionate(boolean rankBased){
		this(rankBased, true, 1.0);
	}
	public FitnessProportionate(){
		this(false, true, 1.0);
	}


	@Override
	public final HashMap<EvolvedIndividual,Integer> select(Population population, int n, ExecutorService executor) {
		if(n <= 0)
			return new HashMap<EvolvedIndividual,Integer>();
		else if(population.isEmpty())
			throw new IllegalArgumentException("n is positive but the population is empty!");
		
		HashMap<EvolvedIndividual,Integer> selectionMap = new HashMap<EvolvedIndividual,Integer>();
		
		int firstValidIndex = 0;
		double remainingWeight = 1.0;
		ArrayList<EvolvedIndividual> selectionableIndividuals = new ArrayList<EvolvedIndividual>(population);
		
		HashMap<EvolvedIndividual,Double> normalizedFitnessMap = normalizeFitnessFor(population);
		for(int i = 0; i < n; i++){
			if(firstValidIndex == selectionableIndividuals.size()){
				firstValidIndex = 0;
				remainingWeight = 1.0;
			}
			
			int selectedIndividualIndex = performRouletteWheel(selectionableIndividuals, firstValidIndex, normalizedFitnessMap, remainingWeight);
			EvolvedIndividual selectedIndividual = selectionableIndividuals.get(selectedIndividualIndex);
			
			if(!withReplacement){
				remainingWeight -= normalizedFitnessMap.get(selectedIndividual);
				Collections.swap(selectionableIndividuals, firstValidIndex, selectedIndividualIndex);
				firstValidIndex++;
			}
			
			recordSelection(selectionMap, selectedIndividual);
		}
		
		return selectionMap;
	}
	
	
	/**
	 * Returns the normalized fitness, so that the sum of all fitnesses equals 1, with
	 * better individuals' fitness closer to 1.
	 * 
	 * @param population
	 * 			TODO : Description.
	 * @return
	 * 			TODO : Description.
	 */
	private final HashMap<EvolvedIndividual,Double> normalizeFitnessFor(Population population){
		HashMap<EvolvedIndividual,Double> adjustedFitnessMap;
		if(adjustmentConstant <= 0.0)
			adjustedFitnessMap = standardizedFitnessFor(population, true);
		else {
			HashMap<EvolvedIndividual,Double> standardizedFitnessMap = standardizedFitnessFor(population, false);
			adjustedFitnessMap = adjustFitness(standardizedFitnessMap, adjustmentConstant);
		}
		
		HashMap<EvolvedIndividual,Double> normalizedFitnessMap = new HashMap<EvolvedIndividual,Double>();
		double sumAdjustedFitness = 0;
		for(EvolvedIndividual individual : population)
			sumAdjustedFitness += adjustedFitnessMap.get(individual);
		for(EvolvedIndividual individual : population)
			normalizedFitnessMap.put(individual, adjustedFitnessMap.get(individual) / sumAdjustedFitness);
		
		return normalizedFitnessMap;
	}
	
	
	/**
	 * Returns the results in the standardized form, i.e. only positive or 0 values.
	 * 
	 * @param population
	 * 			TODO : Description.
	 * @param ascending
	 * 			If true, individuals' standardized fitnesses will be in ascending order,
	 * 			i.e better individuals will have higher fitnesses. 
	 * 			Otherwise better individuals will have a fitness closer to 0.
	 * @return
	 * 			TODO : Description.
	 */
	private final HashMap<EvolvedIndividual,Double> standardizedFitnessFor(Population population, boolean ascending){
		if(!rankBased){
			double[] extremumFiniteFitnesses = buildExtremumFiniteFitnesses(population);
			
			if(ascending){
				if(population.get(0).getRawFitnessType().equals(RawFitnessType.Score))
					return standardizeRawFitnessKeepingOrder(population, extremumFiniteFitnesses[0], extremumFiniteFitnesses[1]);
				else
					return standardizeRawFitnessReversingOrder(population, extremumFiniteFitnesses[0], extremumFiniteFitnesses[1]);
			} else {
				if(population.get(0).getRawFitnessType().equals(RawFitnessType.Score))
					return standardizeRawFitnessReversingOrder(population, extremumFiniteFitnesses[0], extremumFiniteFitnesses[1]);
				else
					return standardizeRawFitnessKeepingOrder(population, extremumFiniteFitnesses[0], extremumFiniteFitnesses[1]);
			}
		} else {
			population.sort();
			
			if(ascending)
				return ranksMapInAscendingOrderFor(population);
			else
				return ranksMapInDescendingOrderFor(population);
		}
	}
	private final double[] buildExtremumFiniteFitnesses(Population population){
		double minFiniteFitness = Double.POSITIVE_INFINITY;
		double maxFiniteFitness = Double.NEGATIVE_INFINITY;
		
		for(EvolvedIndividual individual : population){
			if(Double.isFinite(individual.getRawFitness())){
				minFiniteFitness = Math.min(minFiniteFitness, individual.getRawFitness());
				maxFiniteFitness = Math.max(maxFiniteFitness, individual.getRawFitness());
			}
		}
		
		return new double[]{minFiniteFitness, maxFiniteFitness};
	}
	private final HashMap<EvolvedIndividual,Double> standardizeRawFitnessKeepingOrder(
			Population population, 
			double minFiniteRawFitness, 
			double maxFiniteRawFitness){
		HashMap<EvolvedIndividual,Double> standardizedFitnessMap = new HashMap<EvolvedIndividual,Double>();
		for(EvolvedIndividual individual : population){
			if(Double.isFinite(individual.getRawFitness()))
				standardizedFitnessMap.put(individual, individual.getRawFitness() - minFiniteRawFitness);
			else if(individual.getRawFitness() > 0.0)
				standardizedFitnessMap.put(individual, maxFiniteRawFitness - minFiniteRawFitness);
			else
				standardizedFitnessMap.put(individual, 0.0);
		}
		return standardizedFitnessMap;
	}
	private final HashMap<EvolvedIndividual,Double> standardizeRawFitnessReversingOrder(
			Population population, 
			double minFiniteRawFitness, 
			double maxFiniteRawFitness){
		HashMap<EvolvedIndividual,Double> standardizedFitnessMap = new HashMap<EvolvedIndividual,Double>();
		for(EvolvedIndividual individual : population){
			if(Double.isFinite(individual.getRawFitness()))
				standardizedFitnessMap.put(individual, maxFiniteRawFitness - individual.getRawFitness());
			else if(individual.getRawFitness() > 0.0)
				standardizedFitnessMap.put(individual, 0.0);
			else
				standardizedFitnessMap.put(individual, maxFiniteRawFitness - minFiniteRawFitness);
		}
		return standardizedFitnessMap;
	}
	private final HashMap<EvolvedIndividual,Double> ranksMapInAscendingOrderFor(Population population){
		HashMap<EvolvedIndividual,Double> standardizedFitnessMap = new HashMap<EvolvedIndividual,Double>();
		double maxIndex = population.size() - 1;
		for(int i = 0; i < population.size(); i++)
			standardizedFitnessMap.put(population.get(i), maxIndex - i);
		return standardizedFitnessMap;
	}
	private final HashMap<EvolvedIndividual,Double> ranksMapInDescendingOrderFor(Population population){
		HashMap<EvolvedIndividual,Double> standardizedFitnessMap = new HashMap<EvolvedIndividual,Double>();
		for(int i = 0; i < population.size(); i++)
			standardizedFitnessMap.put(population.get(i), (double) i);
		return standardizedFitnessMap;
	}
	
	
	/**
	 * Returns the results in the adjusted form ( 1 / (Constant + standardizedFitness) ), 
	 * i.e between 0 and 1, with better individuals closer to 1.
	 * <p>
	 * Note : The higher the adjust constant is, the more uniform the results will be.
	 * 
	 * @param standardizedFitnessMap
	 * 			TODO : Description.
	 * @param constant
	 * 			TODO : Description.
	 * @return
	 * 			Results in the adjusted form.
	 */
	private final HashMap<EvolvedIndividual,Double> adjustFitness(HashMap<EvolvedIndividual,Double> standardizedFitnessMap, double constant){
		HashMap<EvolvedIndividual, Double> adjustedFitnessMap = new HashMap<EvolvedIndividual, Double>();
		for(EvolvedIndividual individual : standardizedFitnessMap.keySet()){
			double standardizedFitness = standardizedFitnessMap.get(individual);
			double adjustedFitness = 1.0 / (constant + standardizedFitness);
			adjustedFitnessMap.put(individual, adjustedFitness);
		}
		return adjustedFitnessMap;
	}
	
	
	private final int performRouletteWheel(
			ArrayList<EvolvedIndividual> selectionableIndividuals, 
			int firstValidIndex, 
			HashMap<EvolvedIndividual,Double> normalizedFitnessMap, 
			double totalWeight){
		int selectedIndividualIndex = firstValidIndex;
		
		double currentWeight = 0.0;
		double rand = ThreadLocalRandom.current().nextDouble(totalWeight);
		
		for(int j = firstValidIndex; j < selectionableIndividuals.size(); j++){
			EvolvedIndividual individual = selectionableIndividuals.get(j);
			currentWeight += normalizedFitnessMap.get(individual);
			if(rand < currentWeight){
				selectedIndividualIndex = j;
				break;
			}
		}
		
		return selectedIndividualIndex;
	}
	
	
	private final void recordSelection(HashMap<EvolvedIndividual,Integer> selectionMap, EvolvedIndividual individual){
		if(!selectionMap.containsKey(individual))
			selectionMap.put(individual, 1);
		else
			selectionMap.put(individual, selectionMap.get(individual)+1);
	}
}
