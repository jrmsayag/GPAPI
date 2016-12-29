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
 * Random.java
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



public final class Random implements NaturalSelectionStrategyInterface {
	
	private final boolean withReplacement;
	
	
	
	public Random(boolean withReplacement){
		this.withReplacement = withReplacement;
	}
	public Random(){
		this(true);
	}

	@Override
	public final HashMap<EvolvedIndividual,Integer> select(Population population, int n, ExecutorService executor) {
		if(n <= 0)
			return new HashMap<EvolvedIndividual,Integer>();
		else if(population.isEmpty())
			throw new IllegalArgumentException("n is positive but the population is empty!");
		
		HashMap<EvolvedIndividual,Integer> selectionMap = new HashMap<EvolvedIndividual,Integer>();
		
		int firstValidIndex = 0;
		ArrayList<EvolvedIndividual> selectionableIndividuals = new ArrayList<EvolvedIndividual>(population);
		
		ThreadLocalRandom generator = ThreadLocalRandom.current();
		for(int i = 0; i < n; i++){
			if(firstValidIndex == selectionableIndividuals.size())
				firstValidIndex = 0;
			
			int index = generator.nextInt(firstValidIndex, selectionableIndividuals.size());
			EvolvedIndividual individual = selectionableIndividuals.get(index);
			
			if(!withReplacement){
				Collections.swap(selectionableIndividuals, firstValidIndex, index);
				firstValidIndex++;
			}
			
			recordSelection(selectionMap, individual);
		}
		
		return selectionMap;
	}
	@Override
	public final EvolvedIndividual selectOne(Population population) {
		return population.get(ThreadLocalRandom.current().nextInt(population.size()));
	}
	
	private final void recordSelection(HashMap<EvolvedIndividual,Integer> selectionMap, EvolvedIndividual individual){
		if(!selectionMap.containsKey(individual))
			selectionMap.put(individual, 1);
		else
			selectionMap.put(individual, selectionMap.get(individual)+1);
	}
}
