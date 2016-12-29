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
 * KillOldest.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.algorithm.generalpurposeoperators;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import com.gpapi.Population;
import com.gpapi.individuals.EvolvedIndividual;



public final class KillOldest implements GeneralPurposeOperatorInterface {
	
	private final GeneralPurposeOperatorInterface next;
	
	private final int maxAge;
	
	private final HashMap<EvolvedIndividual,Integer> birthDates = new HashMap<EvolvedIndividual,Integer>();
	
	
	
	public KillOldest(int maxAge, GeneralPurposeOperatorInterface next){
		if(maxAge < 1)
			throw new IllegalArgumentException("The limit age must be strictly greater than 0!");
		
		this.maxAge = maxAge;
		this.next = next;
	}
	public KillOldest(int limitAge){
		this(limitAge, null);
	}

	@Override
	public final void perform(List<Population> populations, int generation, boolean fitnessFunctionIsLocal, ExecutorService executor) throws InterruptedException, ExecutionException {
		HashSet<EvolvedIndividual> deadIndividuals = new HashSet<EvolvedIndividual>(birthDates.keySet());
		
		for(Population population : populations){
			for(Iterator<EvolvedIndividual> populationIterator = population.iterator(); populationIterator.hasNext(); ){
				EvolvedIndividual individual = populationIterator.next();
				Integer birthDate = birthDates.get(individual);
				
				if(birthDate == null){
					birthDate = generation - 1;
					birthDates.put(individual, birthDate);
				} else
					deadIndividuals.remove(individual);
				
				if(generation - birthDate > maxAge){
					populationIterator.remove();
					birthDates.remove(individual);
				}
			}
		}
		
		for(EvolvedIndividual deadIndividual : deadIndividuals)
			birthDates.remove(deadIndividual);
		
		if(next != null)
			next.perform(populations, generation, fitnessFunctionIsLocal, executor);
	}
}
