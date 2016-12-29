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
 * GlobalFitnessAlgorithmBuilder.java
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
import com.gpapi.algorithm.breedingoperators.GenerationalBreedingOperatorInterface;
import com.gpapi.algorithm.breedingoperators.ParentSelection;
import com.gpapi.algorithm.fitnessfunction.GlobalFitnessFunctionInterface;
import com.gpapi.algorithm.naturalselectionstrategies.Tournament;
import com.gpapi.individuals.EvolvedIndividual;



public class GlobalFitnessAlgorithmBuilder extends AbstractAlgorithmBuilder<GlobalFitnessAlgorithmBuilder> {
	
	private final GlobalFitnessFunctionInterface fitnessFunction;
	public final GlobalFitnessFunctionInterface getFitnessFunction() {
		return fitnessFunction;
	}
	
	private GenerationalBreedingOperatorInterface breedingOperator = new ParentSelection(new Tournament(2));
	public final GenerationalBreedingOperatorInterface getBreedingOperator() {
		return breedingOperator;
	}
	public final GlobalFitnessAlgorithmBuilder setBreedingOperator(GenerationalBreedingOperatorInterface breedingOperator) {
		this.breedingOperator = breedingOperator;
		return this;
	}
	
	
	
	protected GlobalFitnessAlgorithmBuilder(GlobalFitnessFunctionInterface fitnessFunction, int generations, EvolvedIndividual initIndividual, int populationsSize, int nPopulations) {
		super(generations, initIndividual, populationsSize, nPopulations);
		
		if(fitnessFunction == null)
			throw new NullPointerException("fitnessFunction can't be null!");
		else
			this.fitnessFunction = fitnessFunction;
	}
	protected GlobalFitnessAlgorithmBuilder(GlobalFitnessFunctionInterface fitnessFunction, int generations, List<Population> populations){
		super(generations, populations);
		
		if(fitnessFunction == null)
			throw new NullPointerException("fitnessFunction can't be null!");
		else
			this.fitnessFunction = fitnessFunction;
	}
	
	
	
	public static final GlobalFitnessAlgorithmBuilder create(
			GlobalFitnessFunctionInterface fitnessFunction, 
			int generations, 
			EvolvedIndividual initIndividual, 
			int populationsSize, 
			int nPopulations) {
		return new GlobalFitnessAlgorithmBuilder(fitnessFunction, generations, initIndividual, populationsSize, nPopulations);
	}
	public static final GlobalFitnessAlgorithmBuilder create(
			GlobalFitnessFunctionInterface fitnessFunction, 
			int generations, 
			List<Population> populations) {
		return new GlobalFitnessAlgorithmBuilder(fitnessFunction, generations, populations);
	}
	
	
	
	public final GlobalFitnessAlgorithm build(){
		return new GlobalFitnessAlgorithm(this);
	}
}
