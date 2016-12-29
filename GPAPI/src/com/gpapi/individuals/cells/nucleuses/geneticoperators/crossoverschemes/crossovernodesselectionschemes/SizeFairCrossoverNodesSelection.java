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
 * SizeFairCrossoverNodesSelection.java
 * ---------------
 * (C) Copyright 2015-2016, by Jeremy Sayag and Contributors.
 *
 * Original Author:  Jeremy Sayag;
 * Contributor(s):   ;
 * 
 */


package com.gpapi.individuals.cells.nucleuses.geneticoperators.crossoverschemes.crossovernodesselectionschemes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import com.gpapi.individuals.cells.nucleuses.geneticoperators.GeneticOperatorInterface;
import com.gpapi.individuals.cells.nucleuses.nodes.AbstractNode;
import com.gpapi.individuals.cells.nucleuses.types.AbstractType;



/**
 * 
 * Implements a Size Fair selection scheme for the matching crossover nodes, 
 * cf. <em>Size Fair and Homologous Tree Crossovers for Tree Genetic Programming</em> 
 * by W. B. Langdon.
 * 
 * @author sayag
 *
 */

public final class SizeFairCrossoverNodesSelection implements CrossoverNodesSelectionSchemeInterface {

	private static final long serialVersionUID = 424585758637532114L;

	@Override
	public final HashMap<AbstractNode, AbstractNode> chooseMatchingCrossoverNodes(
			List<AbstractNode> firstBranchCrossoverNodes,
			List<AbstractNode> eligibleNodesList, 
			GeneticOperatorInterface geneticOperator) {
		TreeMap<Integer,ArrayList<AbstractNode>> matchingNodesBySubtreeSize = buildMatchingNodesSubtreeSizes(eligibleNodesList);
		
		HashMap<AbstractNode,AbstractNode> crossoverNodesMap = new HashMap<AbstractNode,AbstractNode>();
		
		for(AbstractNode crossoverNode : firstBranchCrossoverNodes){
			AbstractNode matchingNode = findMatchingCrossoverNode(
					geneticOperator.getDescendantNodes(crossoverNode).size(), 
					crossoverNode.getReturnType(), 
					matchingNodesBySubtreeSize);
			if(matchingNode != null)
				crossoverNodesMap.put(crossoverNode, geneticOperator.replicateSubtree(matchingNode));
		}
		
		return crossoverNodesMap;
	}
	
	private final TreeMap<Integer,ArrayList<AbstractNode>> buildMatchingNodesSubtreeSizes(List<AbstractNode> nodesList){
		TreeMap<Integer,ArrayList<AbstractNode>> matchingNodesBySubtreeSize = new TreeMap<Integer,ArrayList<AbstractNode>>();
		HashMap<AbstractNode,Integer> matchingNodesSubtreeSize = new HashMap<AbstractNode,Integer>();
		
		for(AbstractNode node : nodesList)
			buildSubtreeSize(node, matchingNodesBySubtreeSize, matchingNodesSubtreeSize);
		
		return matchingNodesBySubtreeSize;
	}
	private final int buildSubtreeSize(
			AbstractNode root, 
			TreeMap<Integer,ArrayList<AbstractNode>> matchingNodesBySubtreeSize, 
			HashMap<AbstractNode,Integer> matchingNodesSubtreeSize){
		Integer subtreeSize = matchingNodesSubtreeSize.get(root);
		if(subtreeSize == null){
			subtreeSize = 1;
			
			for(AbstractNode child : root.getArgs())
				subtreeSize += buildSubtreeSize(child, matchingNodesBySubtreeSize, matchingNodesSubtreeSize);
			
			ArrayList<AbstractNode> sameSizeSubtreesRoots = matchingNodesBySubtreeSize.get(subtreeSize);
			if(sameSizeSubtreesRoots == null){
				sameSizeSubtreesRoots = new ArrayList<AbstractNode>();
				matchingNodesBySubtreeSize.put(subtreeSize, sameSizeSubtreesRoots);
			}
			sameSizeSubtreesRoots.add(root);
			
			matchingNodesSubtreeSize.put(root, subtreeSize);
		}
		return subtreeSize;
	}
	
	private final AbstractNode findMatchingCrossoverNode(
			int baseSubtreeSize, 
			AbstractType baseSubtreeType, 
			TreeMap<Integer,ArrayList<AbstractNode>> matchingNodesBySubtreeSize){
		ArrayList<AbstractNode> potentialMatchingNodesList = buildPotentialMatchingNodesList(
				baseSubtreeSize, 
				baseSubtreeType, 
				matchingNodesBySubtreeSize);
		
		if(!potentialMatchingNodesList.isEmpty())
			return potentialMatchingNodesList.get(ThreadLocalRandom.current().nextInt(potentialMatchingNodesList.size()));
		else
			return null;
	}
	private final ArrayList<AbstractNode> buildPotentialMatchingNodesList(
			int baseSubtreeSize, 
			AbstractType baseSubtreeType, 
			TreeMap<Integer,ArrayList<AbstractNode>> matchingNodesBySubtreeSize){
		
		ArrayList<AbstractNode> sameSizeCompatibleSubtrees = new ArrayList<AbstractNode>();
		ArrayList<AbstractNode> sameSizeSubtrees = matchingNodesBySubtreeSize.get(baseSubtreeSize);
		if(sameSizeSubtrees != null){
			for(AbstractNode potentialSameSizeSubtreeRoot : sameSizeSubtrees){
				if(potentialSameSizeSubtreeRoot.getReturnType().isTheSameAs(baseSubtreeType))
					sameSizeCompatibleSubtrees.add(potentialSameSizeSubtreeRoot);
			}
		}
		
		double averageBiggerCompatibleSubtreesSize = 0;
		double averageSmallerCompatibleSubtreesSize = 0;
		
		ArrayList<AbstractNode> biggerCompatibleSubtrees = new ArrayList<AbstractNode>();
		ArrayList<AbstractNode> smallerCompatibleSubtrees = new ArrayList<AbstractNode>();
		
		Entry<Integer,ArrayList<AbstractNode>> currentBiggerSubtreesEntry = matchingNodesBySubtreeSize.ceilingEntry(baseSubtreeSize + 1);
		Entry<Integer,ArrayList<AbstractNode>> currentSmallerSubtreesEntry = matchingNodesBySubtreeSize.floorEntry(baseSubtreeSize - 1);
		
		while(true){
			if(currentBiggerSubtreesEntry != null && currentBiggerSubtreesEntry.getKey() >= 2*baseSubtreeSize)
				currentBiggerSubtreesEntry = null;
			
			if(currentBiggerSubtreesEntry == null && biggerCompatibleSubtrees.isEmpty())
				break;
			else if(currentSmallerSubtreesEntry == null && smallerCompatibleSubtrees.isEmpty())
				break;
			else if(currentBiggerSubtreesEntry == null && currentSmallerSubtreesEntry == null)
				break;
			
			if(currentBiggerSubtreesEntry != null){
				for(AbstractNode potentialBiggerSubtreeRoot : currentBiggerSubtreesEntry.getValue()){
					if(potentialBiggerSubtreeRoot.getReturnType().isTheSameAs(baseSubtreeType)){
						averageBiggerCompatibleSubtreesSize += currentBiggerSubtreesEntry.getKey();
						biggerCompatibleSubtrees.add(potentialBiggerSubtreeRoot);
					}
				}
				currentBiggerSubtreesEntry = matchingNodesBySubtreeSize.ceilingEntry(currentBiggerSubtreesEntry.getKey() + 1);
			}
			
			if(currentSmallerSubtreesEntry != null){
				for(AbstractNode potentialSmallerSubtreeRoot : currentSmallerSubtreesEntry.getValue()){
					if(potentialSmallerSubtreeRoot.getReturnType().isTheSameAs(baseSubtreeType)){
						averageSmallerCompatibleSubtreesSize += currentSmallerSubtreesEntry.getKey();
						smallerCompatibleSubtrees.add(potentialSmallerSubtreeRoot);
					}
				}
				currentSmallerSubtreesEntry = matchingNodesBySubtreeSize.floorEntry(currentSmallerSubtreesEntry.getKey() - 1);
			}
		}
		
		if(!biggerCompatibleSubtrees.isEmpty())
			averageBiggerCompatibleSubtreesSize /= biggerCompatibleSubtrees.size();
		else
			return sameSizeCompatibleSubtrees;
		
		if(!smallerCompatibleSubtrees.isEmpty())
			averageSmallerCompatibleSubtreesSize /= smallerCompatibleSubtrees.size();
		else
			return sameSizeCompatibleSubtrees;
		
		double sameSizeProba = sameSizeCompatibleSubtrees.size() / 
				((double) smallerCompatibleSubtrees.size() + sameSizeCompatibleSubtrees.size() + biggerCompatibleSubtrees.size());
		
		ThreadLocalRandom generator = ThreadLocalRandom.current();
		if(generator.nextDouble() < sameSizeProba)
			return sameSizeCompatibleSubtrees;
		else {
			double biggerSizeProba = (baseSubtreeSize - averageSmallerCompatibleSubtreesSize) / 
					(averageBiggerCompatibleSubtreesSize - averageSmallerCompatibleSubtreesSize);
			
			if(generator.nextDouble() < biggerSizeProba)
				return biggerCompatibleSubtrees;
			else
				return smallerCompatibleSubtrees;
		}
	}

	@Override
	public final SizeFairCrossoverNodesSelection copy() {
		return new SizeFairCrossoverNodesSelection();
	}
}
