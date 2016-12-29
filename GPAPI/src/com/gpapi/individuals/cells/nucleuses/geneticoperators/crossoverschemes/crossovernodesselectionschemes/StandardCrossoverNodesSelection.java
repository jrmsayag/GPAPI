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
 * StandardCrossoverNodesSelection.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.geneticoperators.crossoverschemes.crossovernodesselectionschemes;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.gpapi.individuals.cells.nucleuses.geneticoperators.GeneticOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;



public final class StandardCrossoverNodesSelection implements CrossoverNodesSelectionSchemeInterface {

	private static final long serialVersionUID = -1772633490999572735L;
	
	@Override
	public final HashMap<AbstractNode, AbstractNode> chooseMatchingCrossoverNodes(
			List<AbstractNode> firstBranchCrossoverNodes,
			List<AbstractNode> eligibleNodesList, 
			GeneticOperatorInterface geneticOperator) {
		HashMap<AbstractNode,AbstractNode> crossoverNodesMap = new HashMap<AbstractNode,AbstractNode>();
		if(!firstBranchCrossoverNodes.isEmpty()){
			for(AbstractNode crossoverNode : firstBranchCrossoverNodes){
				AbstractNode matchingNode = findMatchingCrossoverNode(crossoverNode, eligibleNodesList);
				if(matchingNode != null)
					crossoverNodesMap.put(crossoverNode, geneticOperator.replicateSubtree(matchingNode));
			}
		}
		return crossoverNodesMap;
	}
	private final AbstractNode findMatchingCrossoverNode(AbstractNode crossoverNode, List<AbstractNode> nodesList){
		int nodesListSize = nodesList.size();
		if(nodesListSize > 0){
			int randomizer = ThreadLocalRandom.current().nextInt(nodesListSize);
			for(int i = 0; i < nodesListSize; i++){
				AbstractNode matchingNode = nodesList.get((randomizer + i) % nodesListSize);
				if(matchingNode.getReturnType().isTheSameAs(crossoverNode.getReturnType()))
					return matchingNode;
			}
		}
		return null;
	}

	@Override
	public final CrossoverNodesSelectionSchemeInterface copy() {
		return new StandardCrossoverNodesSelection();
	}
}
