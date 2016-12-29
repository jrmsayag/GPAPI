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


package com.gpapi.examples.multiplicativepersistance.string;

import java.awt.BorderLayout;
import java.math.BigInteger;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.gpapi.Population;
import com.gpapi.algorithm.fitnessfunction.GlobalFitnessFunctionInterface;
import com.gpapi.individuals.EvolvedIndividual;
import com.gpapi.individuals.EvolvedIndividual.RawFitnessType;
import com.gpapi.individuals.cells.nucleuses.types.RealValue;



public final class FitnessFunction implements GlobalFitnessFunctionInterface {
	
	private static final BigInteger ten = new BigInteger("10");
	private static final BigInteger nine = new BigInteger("9");
	private static final BigInteger one = new BigInteger("1");
	private static final BigInteger zero = new BigInteger("0");
	
	private final int maxGenomeSize;
	
	
	
	public FitnessFunction(int maxGenomeSize){
		this.maxGenomeSize = maxGenomeSize;
	}
	public FitnessFunction(){
		this(Integer.MAX_VALUE);
	}

	@Override
	public void applyTo(Population population, int generation) {
		for(EvolvedIndividual individual : population){
			individual.setRawFitnessType(RawFitnessType.Score);
			if(individual.getEggCell().getNucleus().getTotalSize() <= maxGenomeSize){
				DigitsList phenotype = (DigitsList) individual.executeUnconditionallySameArgs().get(0);
				if(phenotype != null){
					int positiveContributions = 0;
					int negativeContributions = 0;
					for(RealValue rawDigit : phenotype.getValue()){
						if(digit(rawDigit) > 1)
							positiveContributions++;
						else
							negativeContributions++;
					}
					double modulation = Math.max(-0.20, Math.min(0.20, 0.01*(positiveContributions - negativeContributions)));
					double score = multiplicativePersistance(interpret(phenotype.getValue())) + modulation;
					individual.setRawFitness(score);
				}
			}
		}
	}
	public static final int multiplicativePersistance(BigInteger value){
		value = value.abs();
		int persistance = 0;
		while(value.compareTo(nine) > 0){
			BigInteger nextValue = one;
			BigInteger tempValue = value;
			while(tempValue.compareTo(zero) > 0){
				nextValue = nextValue.multiply(tempValue.mod(ten));
				tempValue = tempValue.divide(ten);
			}
			value = nextValue;
			persistance++;
		}
		return persistance;
	}
	public static final BigInteger interpret(List<RealValue> rawDigits){
		String s = "";
		for(int i = 0; i < rawDigits.size(); i++)
			s = s + digit(rawDigits.get(i));
		if(!s.isEmpty())
			return new BigInteger(s);
		else
			return zero;
	}
	public static final int digit(RealValue rawDigit){
		return ((int) Math.abs(rawDigit.getValue())) % 10;
	}

	@Override
	public JPanel getPhenotypeView(EvolvedIndividual individual) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel(String.valueOf(
				interpret(((DigitsList) individual.executeUnconditionallySameArgs().get(0)).getValue()))));
		return panel;
	}
}
