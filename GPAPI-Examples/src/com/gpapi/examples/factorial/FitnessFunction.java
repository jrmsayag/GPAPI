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


package com.gpapi.examples.factorial;

import java.util.Arrays;

import javax.swing.JPanel;

import com.gpapi.Population;
import com.gpapi.algorithm.fitnessfunction.GlobalFitnessFunctionInterface;
import com.gpapi.individuals.EvolvedIndividual;
import com.gpapi.individuals.EvolvedIndividual.RawFitnessType;
import com.gpapi.individuals.cells.nucleuses.modules.adss.ADS;
import com.gpapi.individuals.cells.nucleuses.types.RealValue;



public final class FitnessFunction implements GlobalFitnessFunctionInterface {
	
	private final int maxFitnessCase;
	
	private final int maxGenomeDepth;
	
	
	
	public FitnessFunction(int maxFitnessCase, int maxGenomeDepth){
		this.maxFitnessCase = Math.max(1, maxFitnessCase);
		this.maxGenomeDepth = Math.max(1, maxGenomeDepth);
	}
	public FitnessFunction(int maxFitnessCase){
		this(maxFitnessCase, Integer.MAX_VALUE);
	}
	
	@Override
	public final void applyTo(Population population, int generation) {
		for(EvolvedIndividual individual : population){
			individual.setRawFitnessType(RawFitnessType.Penalty);
			if(individual.getEggCell().getNucleus().getMaxModuleDepth() <= maxGenomeDepth){
				double error = 0.0;
				int totalExecutionCost = 0;
				for(int i = 1; i <= maxFitnessCase; i++){
					for(ADS ads : individual.getEggCell().getNucleus().getAdss())
						ads.clear();
					
					RealValue result = (RealValue) individual.executeUnconditionally(0, Arrays.asList(RealValue.create(i)));
					if(result != null)
						error += Math.abs((targetFunction(i) - ((long) result.getValue())) / ((double) targetFunction(i)));
					else
						error += Double.POSITIVE_INFINITY;
					
					totalExecutionCost += individual.getLastExecutionCost();
				}
				individual.setLastExecutionCost(totalExecutionCost);
				individual.setRawFitness(error);
			}
		}
		
		modulateWithExecutionCost(population, 1E-10);
	}
	private final long targetFunction(long n){
		if(n > 1)
			return n * targetFunction(n-1);
		else
			return 1;
	}

	@Override
	public final JPanel getPhenotypeView(EvolvedIndividual individual) {
		return new JPanel();
	}
}
