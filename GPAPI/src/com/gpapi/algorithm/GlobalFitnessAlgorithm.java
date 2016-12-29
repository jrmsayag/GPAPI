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
 * GlobalFitnessAlgorithm.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.algorithm;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.gpapi.Population;
import com.gpapi.algorithm.breedingoperators.GenerationalBreedingOperatorInterface;
import com.gpapi.algorithm.fitnessfunction.GlobalFitnessFunctionInterface;



/**
 * 
 * TODO : Description.
 * 
 * Note : Only those populations whose isFitnessReady() method returns
 * true after execution of the general purpose operator are submitted 
 * to the breeding operator.
 * 
 * @author jeremy
 *
 */
public final class GlobalFitnessAlgorithm extends AbstractAlgorithm {
	
	private final GlobalFitnessFunctionInterface fitnessFunction;
	@Override
	public final GlobalFitnessFunctionInterface getFitnessFunction() {
		return fitnessFunction;
	}
	
	private final GenerationalBreedingOperatorInterface breedingOperator;
	public final GenerationalBreedingOperatorInterface getBreedingOperator() {
		return breedingOperator;
	}
	
	
	
	protected GlobalFitnessAlgorithm(GlobalFitnessAlgorithmBuilder builder) {
		super(builder);
		
		this.fitnessFunction = builder.getFitnessFunction();
		this.breedingOperator = builder.getBreedingOperator();
	}
	
	
	@Override
	public final void performGeneration(int generation) throws InterruptedException, ExecutionException {
		if(generation > 1)
			evolve(generation - 1);
		evaluateFitness(generation);
	}
	
	
	private final void evaluateFitness(int generation) throws InterruptedException, ExecutionException {
		ArrayList<Future<Population>> futures = new ArrayList<Future<Population>>(getPopulations().size());
		
		for(Population population : getPopulations()){
			futures.add(getExecutor().submit(() ->
				{
					getFitnessFunction().applyTo(population, generation);
					return population;
				}));
		}
		
		for(Future<Population> threadResult : futures)
			threadResult.get();
	}
	
	
	private final void evolve(int generation) throws InterruptedException, ExecutionException {
		if(getGeneralPurposeOperator() != null)
			getGeneralPurposeOperator().perform(getPopulations(), generation, false, getExecutor());
		
		if(getBreedingOperator() != null){
			for(Population population : getPopulations()){
				if(population.isFitnessReady())
					getBreedingOperator().breed(population, generation, getFitnessFunction(), getExecutor());
			}
		}
	}
}
