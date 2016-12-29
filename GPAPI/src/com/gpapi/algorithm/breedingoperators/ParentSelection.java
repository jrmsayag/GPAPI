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
 * ParentSelection.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.algorithm.breedingoperators;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadLocalRandom;

import com.gpapi.Population;
import com.gpapi.algorithm.fitnessfunction.FitnessFunctionInterface;
import com.gpapi.algorithm.fitnessfunction.LocalFitnessFunctionInterface;
import com.gpapi.algorithm.naturalselectionstrategies.NaturalSelectionStrategyInterface;
import com.gpapi.individuals.EvolvedIndividual;



public final class ParentSelection implements GenerationalBreedingOperatorInterface, SteadyStateBreedingOperatorInterface {
	
	private final NaturalSelectionStrategyInterface strategy;
	
	private final int newIndivPerGeneration;
	
	private final double crossoverBias;
	
	private final int elitism;
	
	
	
	/**
	 * TODO : Description.
	 * 
	 * @param strategy
	 * @param newIndivPerGeneration
	 * @param crossoverBias
	 * 			If negative, the selection of the parent whose makeChildWith() method will be invoked 
	 * 			alternates between the first parent (parent1) and the second parent (parent2, randomly 
	 * 			selected among all individuals selected for reproduction by the {@link NaturalSelectionStrategyInterface}), 
	 * 			starting with the first parent, for each child of a given first parent.
	 * 			If positive, the value indicates the probability of choosing the best parent (according 
	 * 			to the compareTo() method) as the parent whose makeChildWith() method will be invoked.
	 * @param elitism
	 * 			If strictly negative, parents are inserted in the new population. If positive, indicates the number
	 * 			of top individuals kept for the new generation.
	 */
	public ParentSelection(
			NaturalSelectionStrategyInterface strategy, 
			int newIndivPerGeneration, 
			double crossoverBias, 
			int elitism){
		if(strategy == null)
			throw new NullPointerException("A natural selection strategy must be specified!");
		else if(newIndivPerGeneration < 0)
			throw new IllegalArgumentException("newIndivPerGeneration can't be negative!");
		
		this.strategy = strategy;
		this.newIndivPerGeneration = newIndivPerGeneration;
		this.crossoverBias = crossoverBias;
		this.elitism = elitism;
	}
	public ParentSelection(NaturalSelectionStrategyInterface strategy, double crossoverBias){
		this(strategy, 0, crossoverBias, 1);
	}
	public ParentSelection(NaturalSelectionStrategyInterface strategy){
		this(strategy, 0, 0.5, 1);
	}
	
	
	@Override
	public final void breed(Population population, int generation, FitnessFunctionInterface fitnessFunction, ExecutorService executor) throws InterruptedException, ExecutionException {
		int nSelection = population.getTargetSize() - newIndivPerGeneration - Math.max(0, elitism);
		
		Map<EvolvedIndividual,Integer> selectionMap = strategy.select(population, nSelection, executor);
		
		ArrayList<EvolvedIndividual> newPopulation;
		if(elitism < 0)
			newPopulation = selectWithParents(selectionMap, population);
		else
			newPopulation = selectWithElitism(selectionMap, population);
		
		population.clear();
		population.addAll(newPopulation);
	}
	@Override
	public final EvolvedIndividual breed(Population population, int generation, LocalFitnessFunctionInterface fitnessFunction) throws InterruptedException {
		EvolvedIndividual parent1 = strategy.selectOne(population);
		EvolvedIndividual parent2 = strategy.selectOne(population);
		EvolvedIndividual child;
		
		EvolvedIndividual lock1 = parent1.hashCode() < parent2.hashCode() ? parent1 : parent2;
		EvolvedIndividual lock2 = lock1 == parent1 ? parent2 : parent1;
		synchronized(lock1){
		synchronized(lock2){
			child = buildChildOf(parent1, parent2, 0);
		}
		}
		
		fitnessFunction.applyTo(child, generation);
		return child;
	}
	
	
	private final ArrayList<EvolvedIndividual> selectWithParents(
			Map<EvolvedIndividual,Integer> selectionMap, 
			Population population) throws InterruptedException {
		ArrayList<EvolvedIndividual> newPopulation = new ArrayList<EvolvedIndividual>(population.size());
		
		for(int i = 0; i < newIndivPerGeneration; i++){
			if(Thread.interrupted())
				throw new InterruptedException();
			
			EvolvedIndividual newIndividual = population.get(0).generateNew();
			selectionMap.put(newIndividual, 1);
		}
		
		ArrayList<EvolvedIndividual> parents = new ArrayList<EvolvedIndividual>(selectionMap.keySet());
		for(EvolvedIndividual parent1 : parents)
			newPopulation.addAll(buildChildrenOf(parent1, parents, selectionMap.get(parent1)-1));
		
		newPopulation.addAll(parents);
		
		return newPopulation;
	}
	
	
	private final ArrayList<EvolvedIndividual> selectWithElitism(
			Map<EvolvedIndividual,Integer> selectionMap, 
			Population population) throws InterruptedException {
		ArrayList<EvolvedIndividual> newPopulation = new ArrayList<EvolvedIndividual>(population.size());
		
		ArrayList<EvolvedIndividual> newIndividuals = new ArrayList<EvolvedIndividual>(newIndivPerGeneration);
		for(int i = 0; i < newIndivPerGeneration; i++){
			if(Thread.interrupted())
				throw new InterruptedException();
			
			EvolvedIndividual newIndividual = population.get(0).generateNew();
			selectionMap.put(newIndividual, 0);
			newIndividuals.add(newIndividual);
		}
		
		ArrayList<EvolvedIndividual> parents = new ArrayList<EvolvedIndividual>(selectionMap.keySet());
		for(EvolvedIndividual parent1 : parents)
			newPopulation.addAll(buildChildrenOf(parent1, parents, selectionMap.get(parent1)));
		
		newPopulation.addAll(newIndividuals);
		
		if(elitism > 0){
			population.sort();
			for(int i = 0; i < elitism && i < population.size(); i++)
				newPopulation.add(population.get(i));
		}
		
		return newPopulation;
	}
	
	
	private final ArrayList<EvolvedIndividual> buildChildrenOf(
			EvolvedIndividual parent1, 
			ArrayList<EvolvedIndividual> potentialOtheParents, 
			int nChildren) throws InterruptedException {
		ThreadLocalRandom generator = ThreadLocalRandom.current();
		
		ArrayList<EvolvedIndividual> children = new ArrayList<EvolvedIndividual>();
		for(int i = 0; i < nChildren; i++){
			if(Thread.interrupted())
				throw new InterruptedException();
			
			EvolvedIndividual parent2 = potentialOtheParents.get(generator.nextInt(potentialOtheParents.size()));
			
			EvolvedIndividual lock1 = parent1.hashCode() < parent2.hashCode() ? parent1 : parent2;
			EvolvedIndividual lock2 = lock1 == parent1 ? parent2 : parent1;
			synchronized(lock1){
			synchronized(lock2){
				children.add(buildChildOf(parent1, parent2, i));
			}
			}
		}
		
		return children;
	}
	private final EvolvedIndividual buildChildOf(EvolvedIndividual parent1, EvolvedIndividual parent2, int currentChild){
		if(crossoverBias >= 0.0){
			EvolvedIndividual bestParent = getBestParent(parent1, parent2);
			EvolvedIndividual worstParent = bestParent == parent1 ? parent2 : parent1;
			if(ThreadLocalRandom.current().nextDouble() < crossoverBias)
				return bestParent.makeChildWith(worstParent);
			else
				return worstParent.makeChildWith(bestParent);
		} else {
			if(currentChild % 2 == 0)
				return parent1.makeChildWith(parent2);
			else
				return parent2.makeChildWith(parent1);
		}
	}
	private final EvolvedIndividual getBestParent(EvolvedIndividual parent1, EvolvedIndividual parent2){
		EvolvedIndividual bestParent;
		if(parent1.isFitnessReady() && parent2.isFitnessReady())
			bestParent = parent1.compareTo(parent2) <= 0 ? parent1 : parent2;
		else if(parent1.isFitnessReady())
			bestParent = parent1;
		else if(parent2.isFitnessReady())
			bestParent = parent2;
		else
			bestParent = ThreadLocalRandom.current().nextBoolean() ? parent1 : parent2;
		return bestParent;
	}
}
