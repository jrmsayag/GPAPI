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
 * EvolvedIndividual.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import com.gpapi.individuals.cells.Cell;
import com.gpapi.individuals.cells.nucleuses.modules.adss.ADS;
import com.gpapi.individuals.cells.nucleuses.types.AbstractType;
import com.gpapi.individuals.cells.nucleuses.types.Result;



/**
 * 
 * TODO : Description.
 * <p>
 * Note : This class is not thread-safe and must be externally synchronized if
 * at least one of several concurrent threads can access methods that could 
 * alter its state (or the state of its attributes, like the egg cell).
 * 
 * @author jeremy
 *
 */
public final class EvolvedIndividual implements Comparable<EvolvedIndividual>, Serializable {
	
	private static final long serialVersionUID = -5846672608188467031L;
	
	
	public enum RawFitnessType {
		Penalty,
		Score
	}
	
	
	private final EvolvedIndividualBuilder builder;
	public final EvolvedIndividualBuilder getBuilder() {
		return builder;
	}


	private final Cell eggCell;
	public final Cell getEggCell() {
		return eggCell;
	}
	
	
	private int lastExecutionCost = 0;
	public final int getLastExecutionCost() {
		return lastExecutionCost;
	}
	public final void setLastExecutionCost(int lastExecutionCost) {
		this.lastExecutionCost = lastExecutionCost;
	}
	
	
	private RawFitnessType rawFitnessType = null;
	public final RawFitnessType getRawFitnessType() {
		return rawFitnessType;
	}
	public final void setRawFitnessType(RawFitnessType rawFitnessType) {
		this.rawFitnessType = rawFitnessType;
	}
	
	
	private double rawFitness = Double.NaN;
	public final double getRawFitness() {
		if(rawFitnessType == null)
			throw new NullPointerException("The raw fitness type must be set first!");
		else
			return interpretRawFitness(rawFitness);
	}
	public final void setRawFitness(double rawFitness) {
		this.rawFitness = rawFitness;
	}
	
	
	private final LinkedHashMap<Object,Double> rawFitnessPerFitnessCase = new LinkedHashMap<Object,Double>();
	/**
	 * Returns a list of all fitness cases for which this individual has a fitness value
	 * recorded. The list contains the fitness cases in the order they were added to the
	 * individual.
	 * 
	 * @return
	 * 			The list of fitness cases.
	 */
	public final ArrayList<Object> getAllFitnessCases(){
		return new ArrayList<Object>(rawFitnessPerFitnessCase.keySet());
	}
	public final void addFitnessCase(Object fitnessCase, double fitness){
		rawFitnessPerFitnessCase.put(fitnessCase, fitness);
	}
	public final double getFitnessCase(Object fitnessCase){
		if(rawFitnessType == null)
			throw new NullPointerException("The raw fitness type must be set first!");
		else
			return interpretRawFitness(rawFitnessPerFitnessCase.getOrDefault(fitnessCase, Double.NaN));
	}
	
	
	
	protected EvolvedIndividual(EvolvedIndividualBuilder builder, Cell eggCell) {
		this.builder = builder;
		this.eggCell = eggCell;
	}
	
	
	public final void clearMemory(){
		for(ADS ads : getEggCell().getNucleus().getAdss())
			ads.clear();
	}
	public final boolean isFitnessReady(){
		return getRawFitnessType() != null;
	}
	
	
	public final EvolvedIndividual copy(boolean keepFitness) {
		EvolvedIndividual copy = new EvolvedIndividual(builder, getEggCell().copy());
		
		if(keepFitness){
			copy.setRawFitnessType(getRawFitnessType());
			copy.setRawFitness(getRawFitness());
			for(Object fitnessCase : getAllFitnessCases())
				copy.addFitnessCase(fitnessCase, getFitnessCase(fitnessCase));
		}
		
		return copy;
	}
	public final EvolvedIndividual generateNew() {
		return builder.build();
	}
	public final EvolvedIndividual makeChildWith(EvolvedIndividual other) {
		return new EvolvedIndividual(
				builder, 
				getEggCell().mergeWith(other.getEggCell()));
	}
	
	
	private final double interpretRawFitness(double rawFitness){
		if(Double.isNaN(rawFitness)){
			if(rawFitnessType.equals(RawFitnessType.Penalty))
				return Double.POSITIVE_INFINITY;
			else
				return Double.NEGATIVE_INFINITY;
		} else
			return rawFitness;
	}
	
	
	
	public final List<AbstractType> executeUnconditionally(List<List<AbstractType>> args, List<List<List<AbstractType>>> collections) {
		if(args == null)
			throw new NullPointerException("Argument args can't be null !");
		
		int nRpbs = getEggCell().getNucleus().getRpbs().size();
		if(args.size() != nRpbs)
			throw new IllegalArgumentException("The given number of argument lists doesn't match the number of rpbs !");
		
		ArrayList<AbstractType> result = new ArrayList<AbstractType>(nRpbs);
		
		int rpbIndex = 0;
		int executionCost = 0;
		for(List<AbstractType> currentRpbArgs : args){
			Result rpbResult = getEggCell().executeUnconditionally(rpbIndex, currentRpbArgs, collections);
			if(rpbResult != null){
				result.add(rpbResult.getValue());
				executionCost += rpbResult.getCost();
			} else
				result.add(null);
			rpbIndex++;
		}
		setLastExecutionCost(executionCost);
		
		return result;
	}
	public final List<AbstractType> executeUnconditionally(List<List<AbstractType>> args) {
		return executeUnconditionally(args, Collections.emptyList());
	}
	
	public final List<AbstractType> executeUnconditionallySameArgs(List<AbstractType> commonArgs, List<List<List<AbstractType>>> collections) {
		int nRpbs = getEggCell().getNucleus().getRpbs().size();
		
		ArrayList<List<AbstractType>> args = new ArrayList<List<AbstractType>>(nRpbs);
		for(int i = 0; i < nRpbs; i++)
			args.add(commonArgs);
		
		return executeUnconditionally(args, collections);
	}
	public final List<AbstractType> executeUnconditionallySameArgs(List<AbstractType> args) {
		return executeUnconditionallySameArgs(args, Collections.emptyList());
	}
	public final List<AbstractType> executeUnconditionallySameArgs(AbstractType arg) {
		return executeUnconditionallySameArgs(Arrays.asList(arg));
	}
	public final List<AbstractType> executeUnconditionallySameArgs(){
		return executeUnconditionallySameArgs(Collections.emptyList());
	}
	
	public final AbstractType executeUnconditionally(int rpbIndex, List<AbstractType> args, List<List<List<AbstractType>>> collections) {
		Result result = getEggCell().executeUnconditionally(rpbIndex, args, collections);
		if(result != null){
			setLastExecutionCost(result.getCost());
			return result.getValue();
		} else {
			setLastExecutionCost(0);
			return null;
		}
	}
	public final AbstractType executeUnconditionally(int rpbIndex, List<AbstractType> args) {
		return executeUnconditionally(rpbIndex, args, Collections.emptyList());
	}
	public final AbstractType executeUnconditionally(int rpbIndex, AbstractType arg) {
		return executeUnconditionally(rpbIndex, Arrays.asList(arg));
	}
	public final AbstractType executeUnconditionally(int rpbIndex){
		return executeUnconditionally(rpbIndex, Collections.emptyList());
	}
	
	
	
	public final List<AbstractType> execute(List<List<AbstractType>> args, List<List<List<AbstractType>>> collections) throws Exception {
		if(args == null)
			throw new NullPointerException("Argument args can't be null !");
		
		int nRpbs = getEggCell().getNucleus().getRpbs().size();
		if(args.size() != nRpbs)
			throw new IllegalArgumentException("The given number of argument lists doesn't match the number of rpbs !");
		
		ArrayList<AbstractType> result = new ArrayList<AbstractType>(nRpbs);
		
		int rpbIndex = 0;
		int executionCost = 0;
		for(List<AbstractType> currentRpbArgs : args){
			Result rpbResult = getEggCell().execute(rpbIndex, currentRpbArgs, collections);
			result.add(rpbResult.getValue());
			executionCost += rpbResult.getCost();
			rpbIndex++;
		}
		setLastExecutionCost(executionCost);
		
		return result;
	}
	public final List<AbstractType> execute(List<List<AbstractType>> args) throws Exception {
		return execute(args, Collections.emptyList());
	}
	
	public final List<AbstractType> executeSameArgs(List<AbstractType> commonArgs, List<List<List<AbstractType>>> collections) throws Exception {
		int nRpbs = getEggCell().getNucleus().getRpbs().size();
		
		ArrayList<List<AbstractType>> args = new ArrayList<List<AbstractType>>(nRpbs);
		for(int i = 0; i < nRpbs; i++)
			args.add(commonArgs);
		
		return execute(args, collections);
	}
	public final List<AbstractType> executeSameArgs(List<AbstractType> args) throws Exception {
		return executeSameArgs(args, Collections.emptyList());
	}
	public final List<AbstractType> executeSameArgs(AbstractType arg) throws Exception {
		return executeSameArgs(Arrays.asList(arg));
	}
	public final List<AbstractType> executeSameArgs() throws Exception {
		return executeSameArgs(Collections.emptyList());
	}
	
	public final AbstractType execute(int rpbIndex, List<AbstractType> args, List<List<List<AbstractType>>> collections) throws Exception {
		Result result = getEggCell().execute(rpbIndex, args, collections);
		setLastExecutionCost(result.getCost());
		return result.getValue();
	}
	public final AbstractType execute(int rpbIndex, List<AbstractType> args) throws Exception {
		return execute(rpbIndex, args, Collections.emptyList());
	}
	public final AbstractType execute(int rpbIndex, AbstractType arg) throws Exception {
		return execute(rpbIndex, Arrays.asList(arg));
	}
	public final AbstractType execute(int rpbIndex) throws Exception {
		return execute(rpbIndex, Collections.emptyList());
	}
	
	
	
	@Override
	/**
	 * A negative value indicates that this individual is better.
	 */
	public final int compareTo(EvolvedIndividual other){
		if(getRawFitnessType() == null || other.getRawFitnessType() == null)
			throw new RuntimeException(
					"Both individuals' raw fitness type must be set to be able to compare them!");
		else if(!getRawFitnessType().equals(other.getRawFitnessType()))
			throw new RuntimeException(
					"The individuals must have the same raw fitness type to be able to compare them!");
		return compareFitness(getRawFitness(), other.getRawFitness());
	}
	/**
	 * A negative value indicates that this individual is better for
	 * the given fitness case.
	 */
	public final int compareToForFitnessCase(EvolvedIndividual other, Object fitnessCase){
		if(getRawFitnessType() == null || other.getRawFitnessType() == null)
			throw new RuntimeException(
					"Both individuals' raw fitness type must be set to be able to compare them!");
		else if(!getRawFitnessType().equals(other.getRawFitnessType()))
			throw new RuntimeException(
					"The individuals must have the same raw fitness type to be able to compare them!");
		return compareFitness(getFitnessCase(fitnessCase), other.getFitnessCase(fitnessCase));
	}
	private final int compareFitness(double fitness, double otherFitness){
		if(fitness < otherFitness) {
			if(getRawFitnessType().equals(RawFitnessType.Score))
				return 1;
			else
				return -1;
		} else if(fitness > otherFitness){
			if(getRawFitnessType().equals(RawFitnessType.Score))
				return -1;
			else
				return 1;
		} else
			return 0;
	}
}
