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
 * CompositeArchitectureAlterationOperator.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.geneticoperators.architecturealterationschemes.architecturealterationoperators;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import com.gpapi.individuals.cells.nucleuses.Nucleus;



public final class CompositeArchitectureAlterationOperator implements ArchitectureAlterationOperatorInterface {

	private static final long serialVersionUID = 1668745145706341667L;
	
	private final HashMap<ArchitectureAlterationOperatorInterface,Integer> operators = new HashMap<ArchitectureAlterationOperatorInterface,Integer>();
	
	private final int totalWeight;
	
	
	
	public CompositeArchitectureAlterationOperator(Map<ArchitectureAlterationOperatorInterface,Integer> operators){
		if(operators == null)
			throw new NullPointerException();
		else if(operators.isEmpty())
			throw new IllegalArgumentException("At least one operator must be specified !");
		
		int totalWeight = 0;
		for(Entry<ArchitectureAlterationOperatorInterface,Integer> operatorEntry : operators.entrySet()){
			ArchitectureAlterationOperatorInterface operator = operatorEntry.getKey().copy();
			int weight = Math.max(1, operatorEntry.getValue());
			
			this.operators.put(operator, weight);
			totalWeight += weight;
		}
		this.totalWeight = totalWeight;
	}
	
	
	@Override
	public final boolean architectureAlteration(Nucleus nucleus){
		int randomizer = ThreadLocalRandom.current().nextInt(totalWeight);
		
		int cumWeight = 0;
		for(Entry<ArchitectureAlterationOperatorInterface,Integer> operatorEntry : operators.entrySet()){
			cumWeight += operatorEntry.getValue();
			if(cumWeight > randomizer)
				return operatorEntry.getKey().architectureAlteration(nucleus);
		}
		
		return false;
	}
	
	@Override
	public final CompositeArchitectureAlterationOperator copy() {
		return new CompositeArchitectureAlterationOperator(operators);
	}
}
