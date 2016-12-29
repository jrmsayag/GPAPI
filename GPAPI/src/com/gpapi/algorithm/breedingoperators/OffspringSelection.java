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
 * OffspringSelection.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.algorithm.breedingoperators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

import com.gpapi.Population;
import com.gpapi.algorithm.fitnessfunction.FitnessFunctionInterface;
import com.gpapi.algorithm.fitnessfunction.GlobalFitnessFunctionInterface;
import com.gpapi.algorithm.fitnessfunction.LocalFitnessFunctionInterface;
import com.gpapi.algorithm.naturalselectionstrategies.NaturalSelectionStrategyInterface;
import com.gpapi.individuals.EvolvedIndividual;


/**
 * 
 * TODO : Description.
 * <p>
 * Note : It is better if the fitness function submitted to the breed() method reacts to
 * 			thread interrupt events, otherwise this could cause unnecessary delays due to
 * 			the need to wait for some fitness evaluations to finish while their result won't
 * 			be used.
 * 
 * @author jeremy
 *
 */
public final class OffspringSelection implements GenerationalBreedingOperatorInterface, SteadyStateBreedingOperatorInterface {
	
	private final NaturalSelectionStrategyInterface strategy;
	
	private final int newIndivPerGeneration;
	
	private final double crossoverBias;
	
	private final int elitism;
	
	private final double successRatio;
	
	private final int maxSelectionPressure;
	
	private final HashMap<Integer,HashMap<Population,Boolean>> maxPressureReachedHistory = new HashMap<Integer,HashMap<Population,Boolean>>();
	
	
	
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
	 * 			Indicates the number of top individuals kept for the new generation.
	 * @param successRatio
	 * @param maxSelectionPressure
	 */
	public OffspringSelection(
			NaturalSelectionStrategyInterface strategy, 
			int newIndivPerGeneration, 
			double crossoverBias, 
			int elitism, 
			double successRatio, 
			int maxSelectionPressure){
		if(strategy == null)
			throw new NullPointerException("A natural selection strategy must be specified!");
		else if(newIndivPerGeneration < 0)
			throw new IllegalArgumentException("newIndivPerGeneration can't be negative!");
		else if(elitism < 0)
			throw new IllegalArgumentException("elitism can't be negative!");
		else if(successRatio < 0.0 || successRatio > 1.0)
			throw new IllegalArgumentException("successRatio must be between 0.0 and 1.0!");
		else if(maxSelectionPressure < 1)
			throw new IllegalArgumentException("maxSelectionPressure must be greater than 0!");
		
		this.strategy = strategy;
		this.newIndivPerGeneration = newIndivPerGeneration;
		this.crossoverBias = crossoverBias;
		this.elitism = elitism;
		this.successRatio = successRatio;
		this.maxSelectionPressure = maxSelectionPressure;
	}
	public OffspringSelection(
			NaturalSelectionStrategyInterface strategy, 
			double crossoverBias, 
			double successRatio, 
			int maxSelectionPressure){
		this(strategy, 0, crossoverBias, 1, successRatio, maxSelectionPressure);
	}
	public OffspringSelection(
			NaturalSelectionStrategyInterface strategy, 
			double crossoverBias, 
			int maxSelectionPressure){
		this(strategy, 0, crossoverBias, 1, 0.75, maxSelectionPressure);
	}
	
	
	@Override
	public void breed(Population population, int generation, FitnessFunctionInterface fitnessFunction, ExecutorService executor) throws InterruptedException, ExecutionException {
		int nChildrenRequired = population.getTargetSize() - newIndivPerGeneration - elitism;
		Map<EvolvedIndividual,Integer> selectionMap = strategy.select(population, nChildrenRequired, executor);
		
		ArrayList<EvolvedIndividual> newPopulation = buildNewIndividuals(selectionMap, population.get(0));
		
		if(fitnessFunction instanceof LocalFitnessFunctionInterface)
			newPopulation.addAll(buildChildrenLocal(
				population, 
				selectionMap, 
				nChildrenRequired, 
				(LocalFitnessFunctionInterface) fitnessFunction, 
				generation, 
				executor));
		else if(fitnessFunction instanceof GlobalFitnessFunctionInterface)
			newPopulation.addAll(buildChildrenGlobal(
				population, 
				selectionMap, 
				nChildrenRequired, 
				(GlobalFitnessFunctionInterface) fitnessFunction, 
				generation, 
				executor));
		else
			throw new RuntimeException("Unknown fitness function type !");
		
		newPopulation.addAll(buildElite(population));
		
		population.clear();
		population.addAll(newPopulation);
	}
	@Override
	public final EvolvedIndividual breed(Population population, int generation, LocalFitnessFunctionInterface fitnessFunction) throws InterruptedException {
		ArrayList<EvolvedIndividual> unsuccessfulChildren = new ArrayList<EvolvedIndividual>();
		for(int i = 0; i < maxSelectionPressure; i++){
			EvolvedIndividual parent1 = strategy.selectOne(population);
			EvolvedIndividual parent2 = strategy.selectOne(population);
			
			EvolvedIndividual mainParent;
			EvolvedIndividual otherParent;
			EvolvedIndividual child;
			
			EvolvedIndividual lock1 = parent1.hashCode() < parent2.hashCode() ? parent1 : parent2;
			EvolvedIndividual lock2 = lock1 == parent1 ? parent2 : parent1;
			synchronized(lock1){
			synchronized(lock2){
				mainParent = chooseMainParent(parent1, parent2, 0);
				otherParent = mainParent == parent1 ? parent2 : parent1;
				child = mainParent.makeChildWith(otherParent);
			}
			}
			
			fitnessFunction.applyTo(child, generation);
			if(childIsBetter(child, mainParent)){
				updateMaxPressureReachedHistory(population, generation, false);
				return child;
			} else
				unsuccessfulChildren.add(child);
		}
		updateMaxPressureReachedHistory(population, generation, true);
		
		return unsuccessfulChildren.get(ThreadLocalRandom.current().nextInt(unsuccessfulChildren.size()));
	}
	
	
	private final ArrayList<EvolvedIndividual> buildNewIndividuals(
			Map<EvolvedIndividual,Integer> selectionMap, 
			EvolvedIndividual generator) throws InterruptedException {
		ArrayList<EvolvedIndividual> newIndividuals = new ArrayList<EvolvedIndividual>();
		for(int i = 0; i < newIndivPerGeneration; i++){
			if(Thread.interrupted())
				throw new InterruptedException();
			
			EvolvedIndividual newIndividual = generator.generateNew();
			selectionMap.put(newIndividual, 0);
			newIndividuals.add(newIndividual);
		}
		return newIndividuals;
	}
	private final ArrayList<EvolvedIndividual> buildElite(Population population){
		ArrayList<EvolvedIndividual> elite = new ArrayList<EvolvedIndividual>(elitism);
		if(elitism > 0){
			population.sort();
			for(int i = 0; i < elitism && i < population.size(); i++)
				elite.add(population.get(i));
		}
		return elite;
	}
	
	
	private final ArrayList<EvolvedIndividual> buildChildrenLocal(
			Population population, 
			Map<EvolvedIndividual,Integer> selectionMap, 
			int nChildrenRequired, 
			LocalFitnessFunctionInterface fitnessFunction, 
			int generation, 
			ExecutorService executor) throws InterruptedException, ExecutionException {
		ArrayList<EvolvedIndividual> parents = new ArrayList<EvolvedIndividual>(selectionMap.keySet());
		
		ArrayList<EvolvedIndividual> selectedChildren = new ArrayList<EvolvedIndividual>(nChildrenRequired);
		ArrayList<EvolvedIndividual> unsuccessfulChildren = new ArrayList<EvolvedIndividual>();
		
		LinkedList<Future<EvolvedIndividual[]>> waitingTasks = new LinkedList<Future<EvolvedIndividual[]>>();
		try {
			while(!doneOrGiveUp(selectedChildren, unsuccessfulChildren, nChildrenRequired)){
				if(Thread.interrupted())
					throw new InterruptedException();
				
				loadTasksIfNeededLocal(waitingTasks, parents, generation, fitnessFunction, selectionMap, executor);
				ArrayList<EvolvedIndividual[]> resultsReady = fetchResultsReadyLocal(waitingTasks);
				
				for(EvolvedIndividual[] currentResult : resultsReady){
					if(!done(selectedChildren, nChildrenRequired) && childIsBetter(currentResult[0], currentResult[1])) 
						selectedChildren.add(currentResult[0]);
					else
						unsuccessfulChildren.add(currentResult[0]);
				}
			}
		} finally {
			for(Future<EvolvedIndividual[]> task : waitingTasks)
				task.cancel(true);
		}
		
		updateMaxPressureReachedHistory(population, generation, !done(selectedChildren, nChildrenRequired));
		fillSelectedChildren(selectedChildren, unsuccessfulChildren, parents, nChildrenRequired);
		
		return selectedChildren;
	}
	private final void loadTasksIfNeededLocal(
			LinkedList<Future<EvolvedIndividual[]>> waitingTasks, 
			ArrayList<EvolvedIndividual> parents, 
			int generation, 
			LocalFitnessFunctionInterface fitnessFunction, 
			Map<EvolvedIndividual,Integer> selectionMap, 
			ExecutorService executor){
		if(waitingTasks.size() < parents.size()){
			ThreadLocalRandom generator = ThreadLocalRandom.current();
			for(int i = 0; i < parents.size(); i++){
				EvolvedIndividual parent1 = parents.get(i);
				int nChildren = selectionMap.get(parent1);
				for(int j = 0; j < nChildren; j++){
					EvolvedIndividual parent2 = parents.get(generator.nextInt(parents.size()));
					
					int childIndex = j;
					waitingTasks.add(executor.submit(() ->
						{
							EvolvedIndividual mainParent;
							EvolvedIndividual otherParent;
							EvolvedIndividual child;
							
							EvolvedIndividual lock1 = parent1.hashCode() < parent2.hashCode() ? parent1 : parent2;
							EvolvedIndividual lock2 = lock1 == parent1 ? parent2 : parent1;
							synchronized(lock1){
							synchronized(lock2){
								mainParent = chooseMainParent(parent1, parent2, childIndex);
								otherParent = mainParent == parent1 ? parent2 : parent1;
								child = mainParent.makeChildWith(otherParent);
							}
							}
							
							fitnessFunction.applyTo(child, generation);
							return new EvolvedIndividual[]{child, mainParent};
						}));
				}
			}
		}
	}
	private final ArrayList<EvolvedIndividual[]> fetchResultsReadyLocal(LinkedList<Future<EvolvedIndividual[]>> waitingTasks) throws InterruptedException, ExecutionException {
		ArrayList<EvolvedIndividual[]> resultsReady = new ArrayList<EvolvedIndividual[]>();
		while(resultsReady.isEmpty()){
			for(Iterator<Future<EvolvedIndividual[]>> waitingTasksIterator = waitingTasks.iterator(); waitingTasksIterator.hasNext(); ){
				Future<EvolvedIndividual[]> result = waitingTasksIterator.next();
				if(result.isDone()){
					waitingTasksIterator.remove();
					resultsReady.add(result.get());
				}
			}
			
			if(resultsReady.isEmpty())
				Thread.sleep(10);
		}
		return resultsReady;
	}
	
	
	private final ArrayList<EvolvedIndividual> buildChildrenGlobal(
			Population population, 
			Map<EvolvedIndividual,Integer> selectionMap, 
			int nChildrenRequired, 
			GlobalFitnessFunctionInterface fitnessFunction, 
			int generation, 
			ExecutorService executor) throws InterruptedException, ExecutionException {
		ArrayList<EvolvedIndividual> parents = new ArrayList<EvolvedIndividual>(selectionMap.keySet());
		
		ArrayList<EvolvedIndividual> selectedChildren = new ArrayList<EvolvedIndividual>(nChildrenRequired);
		ArrayList<EvolvedIndividual> unsuccessfulChildren = new ArrayList<EvolvedIndividual>();
		
		ArrayList<Future<ArrayList<EvolvedIndividual[]>>> waitingTasks = loadTasksGlobal(parents, generation, fitnessFunction, selectionMap, executor);
		try {
			while(!waitingTasks.isEmpty() && !done(selectedChildren, nChildrenRequired)){
				if(Thread.interrupted())
					throw new InterruptedException();
				
				ArrayList<ArrayList<EvolvedIndividual[]>> resultsReady = fetchResultsReadyGlobal(waitingTasks);
				
				for(ArrayList<EvolvedIndividual[]> currentResult : resultsReady){
					for(EvolvedIndividual[] container : currentResult){
						if(!done(selectedChildren, nChildrenRequired) && childIsBetter(container[0], container[1])) 
							selectedChildren.add(container[0]);
						else
							unsuccessfulChildren.add(container[0]);
					}
				}
			}
		} finally {
			for(Future<ArrayList<EvolvedIndividual[]>> task : waitingTasks)
				task.cancel(true);
		}
		
		updateMaxPressureReachedHistory(population, generation, !done(selectedChildren, nChildrenRequired));
		fillSelectedChildren(selectedChildren, unsuccessfulChildren, parents, nChildrenRequired);
		
		return selectedChildren;
	}
	private final ArrayList<Future<ArrayList<EvolvedIndividual[]>>> loadTasksGlobal(
			ArrayList<EvolvedIndividual> parents, 
			int generation, 
			GlobalFitnessFunctionInterface fitnessFunction, 
			Map<EvolvedIndividual,Integer> selectionMap, 
			ExecutorService executor){
		ArrayList<Future<ArrayList<EvolvedIndividual[]>>> waitingTasks = new ArrayList<Future<ArrayList<EvolvedIndividual[]>>>();
		for(int i = 0; i < maxSelectionPressure; i++){
			waitingTasks.add(executor.submit(() -> 
				{
					ThreadLocalRandom generator = ThreadLocalRandom.current();
					ArrayList<EvolvedIndividual[]> result = new ArrayList<EvolvedIndividual[]>();
					Population tempPopulation = new Population();
					for(int j = 0; j < parents.size(); j++){
						EvolvedIndividual parent1 = parents.get(j);
						int nChildren = selectionMap.get(parent1);
						for(int k = 0; k < nChildren; k++){
							EvolvedIndividual parent2 = parents.get(generator.nextInt(parents.size()));
							
							EvolvedIndividual mainParent;
							EvolvedIndividual otherParent;
							EvolvedIndividual child;
							
							EvolvedIndividual lock1 = parent1.hashCode() < parent2.hashCode() ? parent1 : parent2;
							EvolvedIndividual lock2 = lock1 == parent1 ? parent2 : parent1;
							synchronized(lock1){
							synchronized(lock2){
								mainParent = chooseMainParent(parent1, parent2, k);
								otherParent = mainParent == parent1 ? parent2 : parent1;
								child = mainParent.makeChildWith(otherParent);
							}
							}
							
							result.add(new EvolvedIndividual[]{child, mainParent});
							tempPopulation.add(child);
						}
					}
					fitnessFunction.applyTo(tempPopulation, generation);
					return result;
				}));
		}
		return waitingTasks;
	}
	private final ArrayList<ArrayList<EvolvedIndividual[]>> fetchResultsReadyGlobal(
			ArrayList<Future<ArrayList<EvolvedIndividual[]>>> waitingTasks) throws InterruptedException, ExecutionException {
		ArrayList<ArrayList<EvolvedIndividual[]>> resultsReady = new ArrayList<ArrayList<EvolvedIndividual[]>>();
		while(resultsReady.isEmpty()){
			for(Iterator<Future<ArrayList<EvolvedIndividual[]>>> waitingTasksIterator = waitingTasks.iterator(); waitingTasksIterator.hasNext(); ){
				Future<ArrayList<EvolvedIndividual[]>> result = waitingTasksIterator.next();
				if(result.isDone()){
					waitingTasksIterator.remove();
					resultsReady.add(result.get());
				}
			}
			
			if(resultsReady.isEmpty())
				Thread.sleep(10);
		}
		return resultsReady;
	}
	
	
	private final void fillSelectedChildren(
			ArrayList<EvolvedIndividual> selectedChildren, 
			ArrayList<EvolvedIndividual> unsuccessfulChildren, 
			ArrayList<EvolvedIndividual> parents, 
			int nChildrenRequired) throws InterruptedException {
		ThreadLocalRandom generator = ThreadLocalRandom.current();
		
		for(int i = 0; i < unsuccessfulChildren.size() && selectedChildren.size() < nChildrenRequired; i++)
			selectedChildren.add(unsuccessfulChildren.get(i));
		
		while(selectedChildren.size() < nChildrenRequired){
			if(Thread.interrupted())
				throw new InterruptedException();
			
			EvolvedIndividual parent1 = parents.get(generator.nextInt(parents.size()));
			EvolvedIndividual parent2 = parents.get(generator.nextInt(parents.size()));
			selectedChildren.add(parent1.makeChildWith(parent2));
		}
	}
	
	
	private final boolean doneOrGiveUp(
			ArrayList<EvolvedIndividual> successfulChildren, 
			ArrayList<EvolvedIndividual> unsuccessfulChildren, 
			int nChildrenRequired){
		return done(successfulChildren, nChildrenRequired) || giveUp(successfulChildren, unsuccessfulChildren, nChildrenRequired);
	}
	private final boolean done(
			ArrayList<EvolvedIndividual> successfulChildren, 
			int nChildrenRequired){
		return successfulChildren.size() >= successRatio * nChildrenRequired;
	}
	private final boolean giveUp(
			ArrayList<EvolvedIndividual> successfulChildren, 
			ArrayList<EvolvedIndividual> unsuccessfulChildren, 
			int nChildrenRequired){
		return successfulChildren.size() + unsuccessfulChildren.size() >= maxSelectionPressure * nChildrenRequired;
	}
	
	
	private final EvolvedIndividual chooseMainParent(EvolvedIndividual parent1, EvolvedIndividual parent2, int childIndex){
		if(crossoverBias >= 0.0){
			EvolvedIndividual bestParent = getBestParent(parent1, parent2);
			EvolvedIndividual worstParent = bestParent == parent1 ? parent2 : parent1;
			if(ThreadLocalRandom.current().nextDouble() < crossoverBias)
				return bestParent;
			else
				return worstParent;
		} else {
			if(childIndex % 2 == 0)
				return parent1;
			else
				return parent2;
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
	private final boolean childIsBetter(EvolvedIndividual child, EvolvedIndividual mainParent){
		if(mainParent.isFitnessReady())
			return child.compareTo(mainParent) < 0;
		else
			return true;
	}
	
	
	/**
	 * TODO : Description.
	 * 
	 * @param population
	 * @param successfulChildren
	 * @param nChildrenRequired
	 * @param generation
	 */
	private final void updateMaxPressureReachedHistory(
			Population population, 
			int generation, 
			boolean maxPressureReached){
		synchronized(maxPressureReachedHistory){
			HashMap<Population,Boolean> maxPressureReachedMap = maxPressureReachedHistory.get(generation);
			if(maxPressureReachedMap == null){
				maxPressureReachedMap = new HashMap<Population,Boolean>();
				maxPressureReachedHistory.put(generation, maxPressureReachedMap);
			}
			maxPressureReachedMap.put(population, maxPressureReached && maxPressureReachedMap.getOrDefault(population, true));
		}
	}
	/**
	 * TODO : Description.
	 * <p>
	 * Note 1 : If no information if available for the given generation, the most recent
	 * information that is anterior to the given generation is returned, and if no
	 * anterior information is available, false is returned.
	 * <p>
	 * Note 2 : This methods runs in O(generation - T) where T is the most recent
	 * generation anterior to the given generation for which the given population
	 * was submitted to this breeding operator, and 0 if no such anterior generation
	 * exists.
	 * 
	 * @param population
	 * @param generation
	 * @return
	 * @see
	 * 		{@link #updateMaxPressureReachedHistory(Population, ArrayList, int, int) updateMaxPressureReachedHistory}
	 */
	public final boolean wasMaxPressureReachedFor(Population population, int generation){
		synchronized(maxPressureReachedHistory){
			HashMap<Population,Boolean> maxPressureReachedMap = null;
			while(generation > 0 && (maxPressureReachedMap == null || !maxPressureReachedMap.containsKey(population))){
				maxPressureReachedMap = maxPressureReachedHistory.get(generation);
				generation--;
			}
			return maxPressureReachedMap != null ? maxPressureReachedMap.getOrDefault(population, false) : false;
		}
	}
}
