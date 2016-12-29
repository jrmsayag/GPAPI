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
 * FrequencyMutation.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes;

import java.util.concurrent.ThreadLocalRandom;

import com.gpapi.individuals.cells.nucleuses.geneticoperators.GeneticOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.pointmutationoperators.PointMutationOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;



public final class FrequencyMutation extends AbstractMutationScheme {

	private static final long serialVersionUID = 7885831347477287014L;
	
	private final double frequency;
	public final double getFrequency() {
		return frequency;
	}
	
	

	public FrequencyMutation(PointMutationOperatorInterface pointMutationOperator, double frequency) {
		super(pointMutationOperator);
		this.frequency = frequency;
	}

	@Override
	public final AbstractNode mutateBranch(AbstractNode root, GeneticOperatorInterface geneticOperator) {
		if(frequency > 0.0){
			ThreadLocalRandom generator = ThreadLocalRandom.current();
			
			int nMutations = 0;
			int branchSize = geneticOperator.getDescendantNodes(root).size();
			for(int i = 0; i < branchSize; i++){
				if(generator.nextDouble() < frequency)
					nMutations++;
			}
			
			return mutateBranch(root, nMutations, geneticOperator);
		} else
			return root;
	}
	
	@Override
	public final FrequencyMutation copy() {
		return new FrequencyMutation(getPointMutationOperator(), frequency);
	}
}
