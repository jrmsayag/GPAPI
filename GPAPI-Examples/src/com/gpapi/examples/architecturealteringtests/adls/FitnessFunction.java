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


package com.gpapi.examples.architecturealteringtests.adls;

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
import com.gpapi.individuals.cells.nucleuses.types.RealValue;



public final class FitnessFunction implements GlobalFitnessFunctionInterface {
	
	private final Map<Population,List<Double>> populationsValuesList = Collections.synchronizedMap(new HashMap<Population,List<Double>>());
	
	private final double maxValue;
	
	private final int nValues;
	
	private final int maxPower;
	
	private final int maxGenomeDepth;
	
	
	
	public FitnessFunction(double maxValue, int nValues, int maxPower, int maxGenomeDepth){
		this.maxValue = Math.max(1.0, maxValue);
		this.nValues = Math.max(1, nValues);
		this.maxPower = Math.max(1, maxPower);
		this.maxGenomeDepth = Math.max(1, maxGenomeDepth);
	}
	public FitnessFunction(double maxValue, int nValues, int maxPower){
		this(maxValue, nValues, maxPower, Integer.MAX_VALUE);
	}
	
	@Override
	public final void applyTo(Population population, int generation) {
		if(!populationsValuesList.containsKey(population))
			populationsValuesList.put(population, generateValuesList());
		
		List<Double> valuesList = populationsValuesList.get(population);
		for(EvolvedIndividual individual : population){
			individual.setRawFitnessType(RawFitnessType.Penalty);
			if(individual.getEggCell().getNucleus().getMaxModuleDepth() <= maxGenomeDepth){
				double error = 0.0;
				for(Double x : valuesList){
					for(int power = 0; power <= maxPower; power++){
						individual.clearMemory();
						
						RealValue result = (RealValue) individual.executeUnconditionally(
								0, Arrays.asList(RealValue.create(x), RealValue.create(power)));
						if(result != null)
							error += Math.abs((Math.pow(x, power) - result.getValue()) / Math.pow(x, power));
						else {
							error += Double.POSITIVE_INFINITY;
							break;
						}
					}
					if(Double.isInfinite(error))
						break;
				}
				individual.setRawFitness(error);
			}
		}
	}
	private final ArrayList<Double> generateValuesList(){
		ArrayList<Double> valuesList = new ArrayList<Double>(nValues);
		
		ThreadLocalRandom generator = ThreadLocalRandom.current();
		for(int i = 0; i < nValues; i++)
			valuesList.add(generator.nextDouble(maxValue));
		
		return valuesList;
	}

	@Override
	public final JPanel getPhenotypeView(EvolvedIndividual individual) {
		return new JPanel();
	}
}
