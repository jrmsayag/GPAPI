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
 * CompositePointMutationOperator.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.pointmutationoperators;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import com.gpapi.individuals.cells.nucleuses.geneticoperators.GeneticOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;



/**
 * This class permits to build a point mutation operator that randomly
 * chooses between several operators.
 * 
 * @author sayag
 * 
 */

public final class CompositePointMutationOperator implements PointMutationOperatorInterface {
	
	private static final long serialVersionUID = 1596282854794667468L;

	private final HashMap<PointMutationOperatorInterface,Integer> operators = new HashMap<PointMutationOperatorInterface,Integer>();
	
	private final int totalWeight;
	
	
	
	public CompositePointMutationOperator(Map<PointMutationOperatorInterface,Integer> operators){
		if(operators == null)
			throw new NullPointerException("Operators can't be null!");
		else if(operators.isEmpty())
			throw new IllegalArgumentException("At least one operator must be specified !");
		
		int totalWeight = 0;
		for(Entry<PointMutationOperatorInterface,Integer> operatorEntry : operators.entrySet()){
			PointMutationOperatorInterface operator = operatorEntry.getKey().copy();
			int weight = Math.max(1, operatorEntry.getValue());
			
			this.operators.put(operator, weight);
			totalWeight += weight;
		}
		this.totalWeight = totalWeight;
	}
	
	/**
	 * Returns a mutated copy of the node given in input
	 * 
	 * @param mutationRoot
	 * 			TODO : Description.
	 * @return
	 * 			The mutated copy of the input node.
	 */
	@Override
	public final AbstractNode mutatedCopy(AbstractNode mutationRoot, GeneticOperatorInterface geneticOperator){
		int randomizer = ThreadLocalRandom.current().nextInt(totalWeight);
		
		int cumWeight = 0;
		for(Entry<PointMutationOperatorInterface,Integer> operatorEntry : operators.entrySet()){
			cumWeight += operatorEntry.getValue();
			if(cumWeight > randomizer)
				return operatorEntry.getKey().mutatedCopy(mutationRoot, geneticOperator);
		}
		
		return mutationRoot.copy();
	}
	
	@Override
	public final CompositePointMutationOperator copy() {
		return new CompositePointMutationOperator(operators);
	}
}
