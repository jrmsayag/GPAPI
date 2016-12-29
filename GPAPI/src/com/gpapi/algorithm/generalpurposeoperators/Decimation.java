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
 * Decimation.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.algorithm.generalpurposeoperators;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import com.gpapi.Population;



public final class Decimation implements GeneralPurposeOperatorInterface {
	
	private final GeneralPurposeOperatorInterface next;
	
	private final double survivalRate;
	
	
	
	public Decimation(double survivalRate, GeneralPurposeOperatorInterface next){
		if(survivalRate <= 0.0)
			throw new IllegalArgumentException("Survival rate must be strictly greater than 0.0!");
		else if(survivalRate >= 1.0)
			throw new IllegalArgumentException("Survival rate must be strictly lower than 1.0!");
		
		this.survivalRate = survivalRate;
		this.next = next;
	}
	public Decimation(double survivalRate){
		this(survivalRate, null);
	}

	@Override
	public final void perform(List<Population> populations, int generation, boolean fitnessFunctionIsLocal, ExecutorService executor) throws InterruptedException, ExecutionException {
		if(generation == 1){
			for(Population population : populations){
				int targetSize = (int) (survivalRate * population.getTargetSize());
				if(targetSize < population.size()){
					population.sort();
					population.clearBetween(targetSize, population.size());
				}
				population.setTargetSize(targetSize);
			}
		}
		
		if(next != null)
			next.perform(populations, generation, fitnessFunctionIsLocal, executor);
	}
}
