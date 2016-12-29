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


package com.gpapi.examples.multiplicativepersistance.primepowers;

import java.awt.BorderLayout;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.gpapi.Population;
import com.gpapi.algorithm.fitnessfunction.GlobalFitnessFunctionInterface;
import com.gpapi.individuals.EvolvedIndividual;
import com.gpapi.individuals.EvolvedIndividual.RawFitnessType;
import com.gpapi.individuals.cells.nucleuses.types.AbstractType;
import com.gpapi.individuals.cells.nucleuses.types.RealValue;



public final class FitnessFunction implements GlobalFitnessFunctionInterface {
	
	private static final BigInteger ten = new BigInteger("10");
	private static final BigInteger nine = new BigInteger("9");
	private static final BigInteger seven = new BigInteger("7");
	private static final BigInteger five = new BigInteger("5");
	private static final BigInteger three = new BigInteger("3");
	private static final BigInteger two = new BigInteger("2");
	private static final BigInteger one = new BigInteger("1");
	private static final BigInteger zero = new BigInteger("0");
	
	private static final ArrayList<BigInteger> poisons = new ArrayList<BigInteger>(){
		private static final long serialVersionUID = 3154631280008173367L;
		{
			//add(new BigInteger("937638166841712"));
			//add(new BigInteger("4996238671872"));
			
			//add(new BigInteger("438939648"));
			//add(new BigInteger("4478976"));
			//add(new BigInteger("338688"));
			//add(new BigInteger("27648"));
			//add(new BigInteger("2688"));
			//add(new BigInteger("768"));
			//add(new BigInteger("336"));
			//add(new BigInteger("54"));
			//add(new BigInteger("20"));
			//add(new BigInteger("0"));
		}
	};
	
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
				int score = multiplicativePersistance(buildValue(individual.executeUnconditionallySameArgs()));
				individual.setRawFitness(score);
			}
		}
	}
	public static final int multiplicativePersistance(BigInteger value){
		for(BigInteger poison : poisons){
			if(value.equals(poison))
				return 0;
		}
		
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
	public static final BigInteger buildValue(List<AbstractType> phenotype){
		for(AbstractType result : phenotype){
			if(result == null)
				return zero;
		}
		
		int power2 = buildPowerInteger((RealValue) phenotype.get(0));
		int power3 = buildPowerInteger((RealValue) phenotype.get(1));
		int power5 = buildPowerInteger((RealValue) phenotype.get(2));
		int power7 = buildPowerInteger((RealValue) phenotype.get(3));
		
		return two.pow(power2)
				.multiply(three.pow(power3))
				.multiply(five.pow(power5))
				.multiply(seven.pow(power7));
	}
	private static final int buildPowerInteger(RealValue rawValue){
		int value = (int) Math.abs(rawValue.getValue());
		return Math.min(1000, value);
	}

	@Override
	public JPanel getPhenotypeView(EvolvedIndividual individual) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel(buildValue(individual.executeUnconditionallySameArgs()).toString()));
		return panel;
	}
}
