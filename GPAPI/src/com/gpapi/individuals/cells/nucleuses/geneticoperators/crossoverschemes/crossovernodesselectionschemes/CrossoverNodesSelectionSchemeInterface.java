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
 * CrossoverNodesSelectionSchemeInterface.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.geneticoperators.crossoverschemes.crossovernodesselectionschemes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.gpapi.individuals.cells.nucleuses.geneticoperators.GeneticOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;



public interface CrossoverNodesSelectionSchemeInterface extends Serializable {
	
	/**
	 * TODO : Description.
	 * 
	 * @param eligibleNodesList
	 * @param nCrossoverNodes
	 * @param geneticOperator
	 * @return
	 */
	public default List<AbstractNode> chooseFirstBranchCrossoverNodes(
			List<AbstractNode> eligibleNodesList, 
			int nCrossoverNodes, 
			GeneticOperatorInterface geneticOperator){
		ArrayList<AbstractNode> crossoverNodes = new ArrayList<AbstractNode>(nCrossoverNodes);
		if(nCrossoverNodes > 0){
			ThreadLocalRandom generator = ThreadLocalRandom.current();
			
			int visitedNodes = 0;
			double remainingCrossoverNodes = nCrossoverNodes;
			
			for(AbstractNode node : eligibleNodesList){
				double mutationProba = remainingCrossoverNodes / (eligibleNodesList.size() - visitedNodes);
				if(generator.nextDouble() < mutationProba){
					crossoverNodes.add(node);
					
					remainingCrossoverNodes--;
					if(remainingCrossoverNodes <= 0.0)
						break;
				}
				visitedNodes++;
			}
		}
		return crossoverNodes;
	}
	
	/**
	 * TODO : Description.
	 * 
	 * Note : The subtree of the returned matching nodes (i.e. the subtree of
	 * nodes that appear as values in the returned map) is a replication of the
	 * original subtree, it can therefore be used safely for the children.
	 * 
	 * @param firstBranchCrossoverNodes
	 * @param eligibleNodesList
	 * @param geneticOperator
	 * @return
	 */
	public Map<AbstractNode,AbstractNode> chooseMatchingCrossoverNodes(
			List<AbstractNode> firstBranchCrossoverNodes, 
			List<AbstractNode> eligibleNodesList, 
			GeneticOperatorInterface geneticOperator);
	
	public CrossoverNodesSelectionSchemeInterface copy();
	
}
