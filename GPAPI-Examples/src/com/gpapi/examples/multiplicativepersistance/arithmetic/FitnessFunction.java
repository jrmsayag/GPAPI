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


package com.gpapi.examples.multiplicativepersistance.arithmetic;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.gpapi.Population;
import com.gpapi.algorithm.fitnessfunction.GlobalFitnessFunctionInterface;
import com.gpapi.individuals.EvolvedIndividual;
import com.gpapi.individuals.EvolvedIndividual.RawFitnessType;
import com.gpapi.individuals.cells.nucleuses.types.RealValue;



public final class FitnessFunction implements GlobalFitnessFunctionInterface {
	
	private final int maxGenomeDepth;
	
	
	
	public FitnessFunction(int maxGenomeDepth){
		this.maxGenomeDepth = maxGenomeDepth;
	}
	public FitnessFunction(){
		this(Integer.MAX_VALUE);
	}

	@Override
	public void applyTo(Population population, int generation) {
		for(EvolvedIndividual individual : population){
			individual.setRawFitnessType(RawFitnessType.Score);
			if(individual.getEggCell().getNucleus().getMaxModuleDepth() <= maxGenomeDepth){
				int score = 0;
				RealValue phenotype = (RealValue) individual.executeUnconditionallySameArgs().get(0);
				if(phenotype != null)
					score = multiplicativePersistance((long) phenotype.getValue());
				individual.setRawFitness(score);
			}
		}
	}
	private final int multiplicativePersistance(long value){
		value = Math.abs(value);
		int persistance = 0;
		while(value > 9){
			long nextValue = 1;
			long tempValue = value;
			while(tempValue > 0){
				nextValue *= (tempValue % 10);
				tempValue /= 10;
			}
			value = nextValue;
			persistance++;
		}
		return persistance;
	}

	@Override
	public final JPanel getPhenotypeView(EvolvedIndividual individual) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel(String.valueOf(
				((RealValue) individual.executeUnconditionallySameArgs().get(0)).getValue())));
		return panel;
	}
}
