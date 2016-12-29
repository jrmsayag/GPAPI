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
 * SteadyStateLocalFitnessAlgorithmBuilder.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.algorithm;

import java.util.List;

import com.gpapi.Population;
import com.gpapi.algorithm.breedingoperators.SteadyStateBreedingOperatorInterface;
import com.gpapi.algorithm.fitnessfunction.LocalFitnessFunctionInterface;
import com.gpapi.algorithm.naturalselectionstrategies.NaturalSelectionStrategyInterface;
import com.gpapi.individuals.EvolvedIndividual;



public class SteadyStateLocalFitnessAlgorithmBuilder extends AbstractAlgorithmBuilder<SteadyStateLocalFitnessAlgorithmBuilder> {

	private final LocalFitnessFunctionInterface fitnessFunction;
	public final LocalFitnessFunctionInterface getFitnessFunction() {
		return fitnessFunction;
	}
	
	private NaturalSelectionStrategyInterface removalSelectionStrategy = null; // TODO : Set a default strategy !
	public final NaturalSelectionStrategyInterface getRemovalSelectionStrategy() {
		return removalSelectionStrategy;
	}
	
	private SteadyStateBreedingOperatorInterface breedingOperator = null; // TODO : Set a default operator !
	public final SteadyStateBreedingOperatorInterface getBreedingOperator() {
		return breedingOperator;
	}
	
	public final SteadyStateLocalFitnessAlgorithmBuilder setBreedingOperatorAndRemovalSelectionStrategy(
			SteadyStateBreedingOperatorInterface breedingOperator, 
			NaturalSelectionStrategyInterface removalSelectionStrategy) {
		if(breedingOperator != null && removalSelectionStrategy == null)
			throw new NullPointerException("removalSelectionStrategy can't be null when breedingOperator isn't!");
		
		this.breedingOperator = breedingOperator;
		this.removalSelectionStrategy = removalSelectionStrategy;
		
		return this;
	}
	
	
	
	protected SteadyStateLocalFitnessAlgorithmBuilder(
			LocalFitnessFunctionInterface fitnessFunction, 
			int generations, 
			EvolvedIndividual initIndividual, 
			int populationsSize, 
			int nPopulations) {
		super(generations, initIndividual, populationsSize, nPopulations);
		
		if(fitnessFunction == null)
			throw new NullPointerException("fitnessFunction can't be null!");
		else
			this.fitnessFunction = fitnessFunction;
	}
	protected SteadyStateLocalFitnessAlgorithmBuilder(LocalFitnessFunctionInterface fitnessFunction, int generations, List<Population> populations){
		super(generations, populations);
		
		if(fitnessFunction == null)
			throw new NullPointerException("fitnessFunction can't be null!");
		else
			this.fitnessFunction = fitnessFunction;
	}
	
	
	
	public static final SteadyStateLocalFitnessAlgorithmBuilder create(
			LocalFitnessFunctionInterface fitnessFunction, 
			int generations, 
			EvolvedIndividual initIndividual, 
			int populationsSize, 
			int nPopulations) {
		return new SteadyStateLocalFitnessAlgorithmBuilder(fitnessFunction, generations, initIndividual, populationsSize, nPopulations);
	}
	public static final SteadyStateLocalFitnessAlgorithmBuilder create(
			LocalFitnessFunctionInterface fitnessFunction, 
			int generations, 
			List<Population> populations) {
		return new SteadyStateLocalFitnessAlgorithmBuilder(fitnessFunction, generations, populations);
	}
	
	
	
	public final SteadyStateLocalFitnessAlgorithm build(){
		return new SteadyStateLocalFitnessAlgorithm(this);
	}
}
