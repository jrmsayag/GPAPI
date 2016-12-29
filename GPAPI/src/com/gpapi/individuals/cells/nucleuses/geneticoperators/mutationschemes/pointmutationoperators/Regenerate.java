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
 * Regenerate.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.pointmutationoperators;

import java.util.concurrent.ThreadLocalRandom;

import com.gpapi.individuals.cells.nucleuses.geneticoperators.GeneticOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;



/**
 * This mutation replaces a subtree with a randomly generated tree.
 * 
 * The replacing subtree is either of fixed size, or of a size distributed
 * uniformly between 1 and 2*RemovedSubtreeSize, in a similar manner as
 * the size fair 50-150% mutation operator described by William Langdon,
 * except here we have approximately a 0-200% mutation.
 * 
 * @author sayag
 *
 */

public final class Regenerate implements PointMutationOperatorInterface {
	
	private static final long serialVersionUID = 3771982824288352053L;
	
	private final int size;
	
	
	public Regenerate(int size){
		this.size = size;
	}
	public Regenerate(){
		this(0);
	}
	
	@Override
	public final AbstractNode mutatedCopy(AbstractNode mutationRoot, GeneticOperatorInterface geneticOperator) {
		if(size > 0)
			return geneticOperator.generateTree(mutationRoot.getReturnType(), size);
		else {
			int baseSubtreeSize = geneticOperator.getDescendantNodes(mutationRoot).size();
			int newSubtreeSize = ThreadLocalRandom.current().nextInt(1, 2*baseSubtreeSize);
			return geneticOperator.generateTree(mutationRoot.getReturnType(), newSubtreeSize);
		}
	}

	@Override
	public final Regenerate copy() {
		return new Regenerate(size);
	}
}
