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
 * SteadyStateLocalFitnessAlgorithm.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.gpapi.Population;
import com.gpapi.algorithm.breedingoperators.SteadyStateBreedingOperatorInterface;
import com.gpapi.algorithm.fitnessfunction.LocalFitnessFunctionInterface;
import com.gpapi.algorithm.naturalselectionstrategies.NaturalSelectionStrategyInterface;
import com.gpapi.individuals.EvolvedIndividual;



public final class SteadyStateLocalFitnessAlgorithm extends AbstractAlgorithm {
	
	private final LocalFitnessFunctionInterface fitnessFunction;
	@Override
	public final LocalFitnessFunctionInterface getFitnessFunction() {
		return fitnessFunction;
	}
	
	private final SteadyStateBreedingOperatorInterface breedingOperator;
	public final SteadyStateBreedingOperatorInterface getBreedingOperator() {
		return breedingOperator;
	}
	
	private final NaturalSelectionStrategyInterface removalSelectionStrategy;
	public final NaturalSelectionStrategyInterface getRemovalSelectionStrategy() {
		return removalSelectionStrategy;
	}
	
	private int evaluations = 0;
	
	
	
	protected SteadyStateLocalFitnessAlgorithm(SteadyStateLocalFitnessAlgorithmBuilder builder) {
		super(builder);
		
		this.fitnessFunction = builder.getFitnessFunction();
		this.breedingOperator = builder.getBreedingOperator();
		this.removalSelectionStrategy = builder.getRemovalSelectionStrategy();
	}
	
	
	@Override
	public final void performGeneration(int generation) throws InterruptedException, ExecutionException {
		if(generation == 1)
			evaluations = 0;
		
		if(generation > 1 && getGeneralPurposeOperator() != null){
			getGeneralPurposeOperator().perform(getPopulations(), generation-1, true, getExecutor());
			fullFitnessEvaluation(generation-1, false);
		}
		
		if(generation == 1)
			fullFitnessEvaluation(generation, true);
		else if(getBreedingOperator() != null)
			evaluations = evolve(generation-1, evaluations);
	}
	
	
	private final void fullFitnessEvaluation(int generation, boolean refresh) throws InterruptedException, ExecutionException {
		ArrayList<Future<EvolvedIndividual>> futures = new ArrayList<Future<EvolvedIndividual>>();
		
		for(Population population : getPopulations()){
			for(EvolvedIndividual individual : population){
				if(refresh || !individual.isFitnessReady()){
					futures.add(getExecutor().submit(() ->
						{
							getFitnessFunction().applyTo(individual, generation);
							return individual;
						}));
				}
			}
		}
		
		for(Future<EvolvedIndividual> threadResult : futures)
			threadResult.get();
	}
	
	
	private final int evolve(int generation, int evaluations) throws InterruptedException, ExecutionException {
		HashMap<Future<EvolvedIndividual>,Population> populationsPerResult = new HashMap<Future<EvolvedIndividual>,Population>();
		for(int j = 0; j < getNThreads(); j++){
			Population population = getPopulations().get(evaluations % getPopulations().size());
			Future<EvolvedIndividual> result = getExecutor().submit(() -> getBreedingOperator().breed(population, generation, getFitnessFunction()));
			populationsPerResult.put(result, population);
			evaluations++;
		}
		
		HashMap<Population,ArrayList<EvolvedIndividual>> newIndividualsPerPopulation = new HashMap<Population,ArrayList<EvolvedIndividual>>();
		for(Entry<Future<EvolvedIndividual>,Population> resultEntry : populationsPerResult.entrySet()){
			ArrayList<EvolvedIndividual> populationNewIndividuals = newIndividualsPerPopulation.get(resultEntry.getValue());
			if(populationNewIndividuals == null){
				populationNewIndividuals = new ArrayList<EvolvedIndividual>();
				newIndividualsPerPopulation.put(resultEntry.getValue(), populationNewIndividuals);
			}
			populationNewIndividuals.add(resultEntry.getKey().get());
		}
		
		for(Entry<Population,ArrayList<EvolvedIndividual>> populationNewIndividualsEntry : newIndividualsPerPopulation.entrySet()){
			Population population = populationNewIndividualsEntry.getKey();
			
			while(!population.isEmpty() && population.size() > population.getTargetSize() - populationNewIndividualsEntry.getValue().size())
				population.remove(getRemovalSelectionStrategy().selectOne(population));
			
			population.addAll(populationNewIndividualsEntry.getValue());
		}
		
		return evaluations;
	}
}
