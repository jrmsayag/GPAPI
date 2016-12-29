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
 * FitnessFunction.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.examples.architecturealteringtests.adss;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JPanel;

import com.gpapi.Population;
import com.gpapi.algorithm.fitnessfunction.GlobalFitnessFunctionInterface;
import com.gpapi.individuals.EvolvedIndividual;
import com.gpapi.individuals.EvolvedIndividual.RawFitnessType;
import com.gpapi.individuals.cells.nucleuses.types.AbstractType;
import com.gpapi.individuals.cells.nucleuses.types.RealValue;



public final class FitnessFunction implements GlobalFitnessFunctionInterface {
	
	public static final List<AbstractType> ADIS_FIELDS_TYPES = Arrays.asList(RealValue.create());
	public static final List<String> ADIS_FIELDS_NAMES = Arrays.asList("X_n");
	
	private final Map<Population,ArrayList<ArrayList<List<AbstractType>>>> populationsSeriesList = 
			Collections.synchronizedMap(new HashMap<Population,ArrayList<ArrayList<List<AbstractType>>>>());
	
	private final double maxValue;
	
	private final int nValues;
	
	private final int nRounds;
	
	private final int maxGenomeDepth;
	
	
	
	public FitnessFunction(double maxValue, int nValues, int nRounds, int maxGenomeDepth){
		this.maxValue = Math.max(1.0, maxValue);
		this.nValues = Math.max(1, nValues);
		this.nRounds = Math.max(1, nRounds);
		this.maxGenomeDepth = Math.max(1, maxGenomeDepth);
	}
	
	@Override
	public final void applyTo(Population population, int generation) {
		if(!populationsSeriesList.containsKey(population))
			populationsSeriesList.put(population, generateSeriesList());
		
		ArrayList<ArrayList<List<AbstractType>>> seriesList = populationsSeriesList.get(population);
		for(EvolvedIndividual individual : population){
			individual.setRawFitnessType(RawFitnessType.Penalty);
			if(individual.getEggCell().getNucleus().getMaxModuleDepth() <= maxGenomeDepth){
				double error = 0.0;
				for(ArrayList<List<AbstractType>> series : seriesList){
					individual.clearMemory();
					
					RealValue product = (RealValue) individual.executeUnconditionally(
							0, Arrays.asList(RealValue.create(series.size())), Arrays.asList(series));
					
					if(product != null)
						error += Math.abs(product(series) - product.getValue()) / product(series);
					else
						error += Double.POSITIVE_INFINITY;
				}
				individual.setRawFitness(error);
			}
		}
		
		modulateWithExecutionCost(population, 1.0E-10);
	}
	private final double product(ArrayList<List<AbstractType>> series){
		double product = 1.0;
		for(List<AbstractType> currentFields : series)
			product *= ((RealValue) currentFields.get(0)).getValue();
		return product;
	}
	
	private final ArrayList<ArrayList<List<AbstractType>>> generateSeriesList(){
		ArrayList<ArrayList<List<AbstractType>>> seriesList = new ArrayList<ArrayList<List<AbstractType>>>(nRounds);
		
		ThreadLocalRandom generator = ThreadLocalRandom.current();
		for(int i = 0; i < nRounds; i++){
			double temp1 = generator.nextDouble(maxValue);
			double temp2 = generator.nextDouble(maxValue);
			
			double min = Math.min(temp1, temp2);
			double max = Math.max(temp1, temp2);
			
			ArrayList<List<AbstractType>> values = new ArrayList<List<AbstractType>>(nValues);
			for(int j = 0; j < nValues; j++)
				values.add(Arrays.asList(RealValue.create(generator.nextDouble(min, max))));
			seriesList.add(values);
		}
		
		return seriesList;
	}

	@Override
	public final JPanel getPhenotypeView(EvolvedIndividual individual) {
		return new JPanel();
	}
}
