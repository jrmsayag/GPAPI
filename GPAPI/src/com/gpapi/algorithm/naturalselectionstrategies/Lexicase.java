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
 * Lexicase.java
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

import com.gpapi.Population;
import com.gpapi.individuals.EvolvedIndividual;



/**
 * 
 * TODO : Description.
 * <p>
 * Different weights can be implicitly assigned to different fitness cases
 * simply by adding several times a unique fitness case that must have more
 * weight to all individuals.
 * <p>
 * Note that since the fitness cases are stored in a map in {@link EvolvedIndividual}, 
 * it is however necessary to use different objects to represent the multiple 
 * occurrences of a single fitness case.
 * 
 * @author jeremy
 *
 */

public final class Lexicase implements NaturalSelectionStrategyInterface {
	
	private final int maxPoolSize;
	
	private final int maxFitnessCases;
	
	private final double survivalRate;
	
	private final boolean withReplacement;
	
	
	
	/**
	 * TODO : Description.
	 * 
	 * @param maxPoolSize
	 * @param maxFitnessCases
	 * @param survivalRate
	 * 			If strictly positive, indicates the proportion of candidates that survive each
	 * 			round of selection (a round being the consideration of a single fitness case).
	 * 			Otherwise, only those individuals with the best fitness for the current fitness
	 * 			case survive for further consideration.
	 * @param withReplacement
	 */
	public Lexicase(int maxPoolSize, int maxFitnessCases, double survivalRate, boolean withReplacement){
		if(maxPoolSize < 1)
			throw new IllegalArgumentException("maxPoolSize must be strictly greater than 0!");
		
		this.maxPoolSize = maxPoolSize;
		this.maxFitnessCases = maxFitnessCases;
		this.survivalRate = survivalRate;
		this.withReplacement = withReplacement;
	}
	public Lexicase(){
		this(Integer.MAX_VALUE, Integer.MAX_VALUE, 0.0, true);
	}

	@Override
	public Map<EvolvedIndividual, Integer> select(Population population, int n, ExecutorService executor) throws InterruptedException, ExecutionException {
		if(n <= 0)
			return new HashMap<EvolvedIndividual,Integer>();
		else if(population.isEmpty())
			throw new IllegalArgumentException("n is positive but the population is empty!");
		else if(executor == null)
			executor = Executors.newSingleThreadExecutor();
		
		int selectedIndividuals = 0;
		HashMap<EvolvedIndividual,Integer> selectionMap = new HashMap<EvolvedIndividual,Integer>();
		
		HashSet<EvolvedIndividual> currentlySelectionableIndividuals = new HashSet<EvolvedIndividual>(population);
		ArrayList<EvolvedIndividual> localSelectionableIndividuals = new ArrayList<EvolvedIndividual>(currentlySelectionableIndividuals);
		
		ArrayList<Object> allFitnessCases = population.get(0).getAllFitnessCases();
		LinkedList<Future<EvolvedIndividual>> waitingTasks = new LinkedList<Future<EvolvedIndividual>>();
		
		try {
			while(selectedIndividuals < n){
				if(Thread.interrupted())
					throw new InterruptedException();
				
				if(!withReplacement)
					localSelectionableIndividuals = new ArrayList<EvolvedIndividual>(currentlySelectionableIndividuals);
				
				loadTasksIfNeeded(waitingTasks, n, localSelectionableIndividuals, allFitnessCases, executor);
				LinkedList<EvolvedIndividual> resultsReady = fetchResultsReady(waitingTasks);
				
				for(EvolvedIndividual currentResult : resultsReady){
					if(handleResult(currentResult, currentlySelectionableIndividuals, selectionMap, population))
						selectedIndividuals++;
				}
			}
		} finally {
			for(Future<EvolvedIndividual> task : waitingTasks)
				task.cancel(true);
		}
		
		return selectionMap;
	}
	private final void loadTasksIfNeeded(
			LinkedList<Future<EvolvedIndividual>> waitingTasks, 
			int n, 
			ArrayList<EvolvedIndividual> selectionableIndividuals, 
			ArrayList<Object> allFitnessCases, 
			ExecutorService executor) {
		if(waitingTasks.size() < Math.max(1, n/2)){
			for(int i = 0; i < n; i++){
				waitingTasks.add(executor.submit(() ->
					{
						ArrayList<Integer> competitorsIndexes = selectCompetitorsIndexes(selectionableIndividuals.size(), 0);
						ArrayList<Object> fitnessCases = selectFitnessCases(allFitnessCases);
						return selectionableIndividuals.get(filter(competitorsIndexes, selectionableIndividuals, fitnessCases));
					}));
			}
		}
	}
	private final LinkedList<EvolvedIndividual> fetchResultsReady(LinkedList<Future<EvolvedIndividual>> waitingTasks) throws InterruptedException, ExecutionException {
		LinkedList<EvolvedIndividual> resultsReady = new LinkedList<EvolvedIndividual>();
		while(resultsReady.isEmpty()){
			for(Iterator<Future<EvolvedIndividual>> waitingTasksIterator = waitingTasks.iterator(); waitingTasksIterator.hasNext(); ){
				Future<EvolvedIndividual> result = waitingTasksIterator.next();
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
	private final boolean handleResult(
			EvolvedIndividual result, 
			HashSet<EvolvedIndividual> currentlySelectionableIndividuals, 
			HashMap<EvolvedIndividual,Integer> selectionMap, 
			Population population) {
		if(!withReplacement){
			if(currentlySelectionableIndividuals.remove(result)){
				recordSelection(selectionMap, result);
				if(currentlySelectionableIndividuals.isEmpty())
					currentlySelectionableIndividuals.addAll(population);
			} else
				return false;
		} else
			recordSelection(selectionMap, result);
		return true;
	}
	
	
	private final ArrayList<Integer> selectCompetitorsIndexes(int nSelectionableIndividuals, int begin){
		ArrayList<Integer> competitorsIndexes = new ArrayList<Integer>(Math.min(nSelectionableIndividuals - begin, maxPoolSize));
		
		double remainingCompetitors = maxPoolSize;
		
		ThreadLocalRandom generator = ThreadLocalRandom.current();
		for(int index = begin; index < nSelectionableIndividuals; index++){
			double selectionProba = remainingCompetitors / (nSelectionableIndividuals - index);
			if(generator.nextDouble() < selectionProba){
				competitorsIndexes.add(index);
				remainingCompetitors--;
				if(remainingCompetitors <= 0.0)
					break;
			}
		}
		
		return competitorsIndexes;
	}
	
	
	private final ArrayList<Object> selectFitnessCases(ArrayList<Object> allFitnessCases){
		ArrayList<Object> fitnessCases = new ArrayList<Object>(Math.min(allFitnessCases.size(), maxFitnessCases));
		
		double remainingFitnessCases = maxFitnessCases;
		
		ThreadLocalRandom generator = ThreadLocalRandom.current();
		for(int i = 0; i < allFitnessCases.size(); i++){
			double selectionProba = remainingFitnessCases / (allFitnessCases.size() - i);
			if(generator.nextDouble() < selectionProba){
				fitnessCases.add(allFitnessCases.get(i));
				remainingFitnessCases--;
				if(remainingFitnessCases <= 0.0)
					break;
			}
		}
		
		Collections.shuffle(fitnessCases);
		
		return fitnessCases;
	}
	
	
	private final int filter(List<Integer> competitorsIndexes, ArrayList<EvolvedIndividual> allIndividuals, ArrayList<Object> fitnessCases){
		for(Object fitnessCase : fitnessCases){
			competitorsIndexes.sort((arg0, arg1) -> 
				{
					EvolvedIndividual individual0 = allIndividuals.get(arg0);
					EvolvedIndividual individual1 = allIndividuals.get(arg1);
					return individual0.compareToForFitnessCase(individual1, fitnessCase);
				});
			
			int limitIndex = buildLimitIndexFor(competitorsIndexes.size());
			EvolvedIndividual limitIndividual = allIndividuals.get(competitorsIndexes.get(limitIndex));
			
			for(limitIndex++; limitIndex < competitorsIndexes.size(); limitIndex++){
				if(allIndividuals.get(competitorsIndexes.get(limitIndex)).compareToForFitnessCase(limitIndividual, fitnessCase) > 0)
					break;
			}
			
			competitorsIndexes = competitorsIndexes.subList(0, limitIndex);
			if(competitorsIndexes.size() < 2)
				break;
		}
		
		return competitorsIndexes.get(ThreadLocalRandom.current().nextInt(competitorsIndexes.size()));
	}
	private final int buildLimitIndexFor(int nCompetitors){
		int limitIndex = (int) (survivalRate * nCompetitors) - 1;
		return Math.max(0, Math.min(nCompetitors - 1, limitIndex));
	}
	
	
	private final void recordSelection(HashMap<EvolvedIndividual,Integer> selectionMap, EvolvedIndividual individual){
		if(!selectionMap.containsKey(individual))
			selectionMap.put(individual, 1);
		else
			selectionMap.put(individual, selectionMap.get(individual)+1);
	}
}
