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
 * AbstractAlgorithmBuilder.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.algorithm;

import java.util.ArrayList;
import java.util.List;

import com.gpapi.Population;
import com.gpapi.algorithm.AbstractAlgorithm.State;
import com.gpapi.algorithm.generalpurposeoperators.GeneralPurposeOperatorInterface;
import com.gpapi.individuals.EvolvedIndividual;



public abstract class AbstractAlgorithmBuilder<T extends AbstractAlgorithmBuilder<T>> {
	
	private final int generations;
	public final int getGenerations() {
		return generations;
	}

	private final List<Population> populations;
	public final List<Population> getPopulations() {
		return populations;
	}
	
	private GeneralPurposeOperatorInterface generalPurposeOperator = null;
	public final GeneralPurposeOperatorInterface getGeneralPurposeOperator() {
		return generalPurposeOperator;
	}
	@SuppressWarnings("unchecked")
	public final T setGeneralPurposeOperator(GeneralPurposeOperatorInterface generalPurposeOperator) {
		this.generalPurposeOperator = generalPurposeOperator;
		return (T) this;
	}

	private State initialState = State.Paused;
	public final State getInitialState() {
		return initialState;
	}
	@SuppressWarnings("unchecked")
	public final T setInitialState(State initialState) {
		if(initialState == null)
			throw new NullPointerException("initialState can't be null!");
		this.initialState = initialState;
		return (T) this;
	}
	
	private boolean recordOnlyBestGeneration = true;
	public final boolean isRecordOnlyBestGeneration() {
		return recordOnlyBestGeneration;
	}
	@SuppressWarnings("unchecked")
	public final T setRecordOnlyBestGeneration(boolean recordOnlyBestGeneration) {
		this.recordOnlyBestGeneration = recordOnlyBestGeneration;
		return (T) this;
	}
	
	private int recordingFrequency = 1;
	public final int getRecordingFrequency() {
		return recordingFrequency;
	}
	@SuppressWarnings("unchecked")
	public final T setRecordingFrequency(int recordingFrequency) {
		this.recordingFrequency = recordingFrequency;
		return (T) this;
	}
	
	private int nThreads = 1;
	public final int getNThreads() {
		return nThreads;
	}
	@SuppressWarnings("unchecked")
	public final T setNThreads(int nThreads) {
		if(nThreads < 1)
			throw new IllegalArgumentException("nThreads must be strictly greater than 0!");
		this.nThreads = nThreads;
		return (T) this;
	}
	
	
	
	protected AbstractAlgorithmBuilder(int generations, EvolvedIndividual initIndividual, int populationsSize, int nPopulations) {
		if(generations < 2)
			throw new IllegalArgumentException("generations must be strictly greater than 1!");
		else if(initIndividual == null)
			throw new NullPointerException("initIndividual can't be null!");
		else if(populationsSize < 2)
			throw new IllegalArgumentException("populationsSize must be strictly greater than 1!");
		else if(nPopulations < 1)
			throw new IllegalArgumentException("There must be at least one population !");
		
		this.generations = generations;
		
		populations = new ArrayList<Population>(nPopulations);
		for(int i = 0; i < nPopulations; i++)
			populations.add(new Population(initIndividual, populationsSize, i));
	}
	protected AbstractAlgorithmBuilder(int generations, List<Population> populations) {
		if(generations < 2)
			throw new IllegalArgumentException("generations must be strictly greater than 1!");
		else if(populations == null)
			throw new NullPointerException("populations can't be null!");
		else if(populations.isEmpty())
			throw new IllegalArgumentException("populations can't be empty!");
		else {
			for(Population population : populations){
				if(population.isEmpty())
					throw new IllegalArgumentException("A population can't be empty!");
			}
		}
		
		this.generations = generations;
		this.populations = populations;
	}
}
