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
 * AbstractMutationScheme.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.gpapi.individuals.cells.nucleuses.geneticoperators.GeneticOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.geneticoperators.mutationschemes.pointmutationoperators.PointMutationOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;



public abstract class AbstractMutationScheme implements MutationSchemeInterface {

	private static final long serialVersionUID = 7544058087935928978L;
	
	private final PointMutationOperatorInterface pointMutationOperator;
	public final PointMutationOperatorInterface getPointMutationOperator() {
		return pointMutationOperator;
	}
	
	
	
	public AbstractMutationScheme(PointMutationOperatorInterface pointMutationOperator){
		this.pointMutationOperator = pointMutationOperator.copy();
	}
	
	protected final AbstractNode mutateBranch(AbstractNode root, int nMutations, GeneticOperatorInterface geneticOperator){
		if(nMutations > 0){
			ThreadLocalRandom generator = ThreadLocalRandom.current();
			
			List<AbstractNode> nodesList = geneticOperator.getDescendantNodes(root);
			
			int visitedNodes = 0;
			double remainingMutations = nMutations;
			
			for(AbstractNode node : nodesList){
				double mutationProba = remainingMutations / (nodesList.size() - visitedNodes);
				if(generator.nextDouble() < mutationProba){
					AbstractNode mutatedNode = getPointMutationOperator().mutatedCopy(node, geneticOperator);
					
					if(!mutatedNode.isReady())
						mutatedNode.setArgs(node.getArgs());
					
					if(node != root)
						node.getParent().replaceArg(node, mutatedNode);
					else
						root = mutatedNode;
					
					remainingMutations--;
					if(remainingMutations <= 0.0)
						break;
				}
				visitedNodes++;
			}
		}
		return root;
	}
}
