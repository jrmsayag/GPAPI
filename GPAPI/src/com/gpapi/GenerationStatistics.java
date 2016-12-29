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
 * GenerationStatistics.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.gpapi.individuals.EvolvedIndividual;



public final class GenerationStatistics extends GenerationSnapshot {

	private static final long serialVersionUID = -2798456948057264087L;
	
	private final Population bestPopulation;
	public final Population getBestPopulation(){
		return bestPopulation;
	}
	
	private final HashMap<Population,EvolvedIndividual> bestIndividuals;
	public final EvolvedIndividual getBestIndividualFor(Population population){
		return bestIndividuals.get(population);
	}
	
	private final HashMap<Population,Integer> medianStructuralComplexities;
	public final Integer getMedianStructuralComplexityFor(Population population){
		return medianStructuralComplexities.get(population);
	}
	private final HashMap<Population,Integer> medianExecutionCosts;
	public final Integer getMedianExecutionCostFor(Population population){
		return medianExecutionCosts.get(population);
	}
	private final HashMap<Population,Double> medianFitnesses;
	public final Double getMedianFitnessFor(Population population){
		return medianFitnesses.get(population);
	}
	
	
	
	public GenerationStatistics(int generation, List<Population> populations){
		super(generation, populations);
		
		bestIndividuals = new HashMap<Population,EvolvedIndividual>();
		medianStructuralComplexities = new HashMap<Population,Integer>();
		medianExecutionCosts = new HashMap<Population,Integer>();
		medianFitnesses = new HashMap<Population,Double>();
		
		Population bestPopulation = null;
		for(Population population : populations){
			population.sort();
			
			bestIndividuals.put(population, population.get(0));
			medianFitnesses.put(population, population.get(population.size() / 2).getRawFitness());
			
			ArrayList<Integer> individualSizes = new ArrayList<Integer>(population.size());
			ArrayList<Integer> individualCosts = new ArrayList<Integer>(population.size());
			for(EvolvedIndividual individual : population){
				individualSizes.add(individual.getEggCell().getNucleus().getTotalSize());
				individualCosts.add(individual.getLastExecutionCost());
			}
			Collections.sort(individualSizes);
			Collections.sort(individualCosts);
			medianStructuralComplexities.put(population, individualSizes.get(population.size() / 2));
			medianExecutionCosts.put(population, individualCosts.get(population.size() / 2));
			
			if(bestPopulation == null)
				bestPopulation = population;
			else if(population.get(0).compareTo(bestPopulation.get(0)) < 0)
				bestPopulation = population;
		}
		this.bestPopulation = bestPopulation;
	}
}
